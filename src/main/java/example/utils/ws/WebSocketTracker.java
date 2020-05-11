package example.utils.ws;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;



/**
 * 
 * All Boiler plate code. Just intended to
 * 
 * (1) replace the instruction : sendObject(frames, i -> true) over the original
 * one : sendObject(frames) Otherwise all our Flux system is broken by default
 * buffering strategy of ReactorNettyWebSocketSession (not sure why netty dev
 * force such behavior...)
 * 
 * (2) keep track of the websockets connections to automatically kill the old
 * ones through our own callback (not sure why netty do not let read the inbound
 * messages...)
 * 
 * @author camille.walim
 */
public class WebSocketTracker extends WebSocketHandlerAdapter {
	static ConcurrentMap<String, ReactorNettyWebSocketSession> connections = new ConcurrentHashMap<>();

	public static WebSocketTracker create() { return new WebSocketTracker(); }

	private WebSocketTracker() {
		super(new HandshakeWebSocketService(new CustomNetty()));
	}

}



class CustomNetty extends ReactorNettyRequestUpgradeStrategy {

	@Override
	public Mono<Void> upgrade(
		ServerWebExchange exchange, WebSocketHandler handler,
		String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory
	) {

		ServerHttpResponse		response		= exchange.getResponse();
		HttpServerResponse		reactorResponse	= getNativeResponse(response);
		HandshakeInfo			handshakeInfo	= handshakeInfoFactory.get();
		NettyDataBufferFactory	bufferFactory	= (NettyDataBufferFactory) response.bufferFactory();

		return reactorResponse.sendWebsocket(subProtocol, this.getMaxFramePayloadLength(), 
			(in, out) -> {

			String key = handshakeInfo.getRemoteAddress().getHostName() + ":" + exchange.getRequest().getURI().getPath();
			WebSocketTracker.connections.remove(key);

			ReactorNettyWebSocketSession session = new CustomSession(in, out, handshakeInfo, bufferFactory, this.getMaxFramePayloadLength());

			WebSocketTracker.connections.put(key, session);

			URI uri = exchange.getRequest().getURI();
			return handler.handle(session).checkpoint(uri + " [ReactorNettyRequestUpgradeStrategy]");
		});
	}

	private HttpServerResponse getNativeResponse(ServerHttpResponse response) {
		if (response instanceof AbstractServerHttpResponse)				return ((AbstractServerHttpResponse) response).getNativeResponse();
		else if (response instanceof ServerHttpResponseDecorator)		return getNativeResponse(((ServerHttpResponseDecorator) response).getDelegate());
		else throw 														new IllegalArgumentException("Couldn't find native response in "+ response.getClass().getName());
	}
}



class CustomSession extends ReactorNettyWebSocketSession {

	public CustomSession(
		WebsocketInbound inbound, WebsocketOutbound outbound,
		HandshakeInfo info, NettyDataBufferFactory bufferFactory,
		int maxFramePayloadLength
	) {
		super(inbound, outbound, info, bufferFactory, maxFramePayloadLength);
	}

	@Override
	public Mono<Void> send(org.reactivestreams.Publisher<org.springframework.web.reactive.socket.WebSocketMessage> messages) {
		Flux<io.netty.handler.codec.http.websocketx.WebSocketFrame> frames = Flux
			.from(messages)
			.doOnNext(message -> {
				if (logger.isTraceEnabled()) 
					logger.trace(getLogPrefix() + "Sending " + message);})
			.map(this::toFrame);
		return getDelegate().getOutbound()
							.sendObject(frames, i -> true)
							.then();
	}
}
