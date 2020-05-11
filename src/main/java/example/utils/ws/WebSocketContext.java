package example.utils.ws;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Flux;



/**
 * @author camille.walim
 * 
 *         Less verbose handler for ws with more features
 */
public record WebSocketContext(
	WebSocketSession session, UriComponents uri, String connectionId,
	BooleanSupplier isValidUntil
) {

	public static WebSocketContext of(WebSocketSession session) {
		var	uri				= UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri()).build();
		var	connectionId	= session.getHandshakeInfo().getRemoteAddress().getHostName() + ":" + uri.getPath();
		return new WebSocketContext(session, uri, connectionId, 
			() -> WebSocketTracker.connections.get(connectionId) == session);
	}

	public static WebSocketHandler handler(Handler handler) {
		return (WebSocketSession session) -> session.send(handler.apply(WebSocketContext.of(session)));
	}

	public boolean getBooleanParam(String key) {
		return Boolean.valueOf(uri.getQueryParams().getFirst(key));
	}

	public long getLngParam(String key) {
		return Integer.valueOf(uri.getQueryParams().getFirst(key));
	}

	public double getDoubleParam(String key) {
		return Double.valueOf(uri.getQueryParams().getFirst(key));
	}

	public static interface Handler extends Function<WebSocketContext, Flux<WebSocketMessage>> {}
}
