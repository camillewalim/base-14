package example.ping.api.ping;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import example.ping.domain.model.PingPong;
import example.utils.ws.WebSocketContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 * Main endpoint that support both http & ws
 * @author camille.walim
 */
@Configuration
public class PingEndpoint {

	@Autowired
	private Function<Boolean, Flux<PingPong>> pingpong;
	@Autowired
	private JpaRepository<PingPong, String> memory;

	Flux<PingPong> flux(boolean isPing) {
		return pingpong.apply(isPing)
			.sampleFirst(Duration.ofMillis(1000))
			.doOnNext(memory::saveAndFlush);
	}

	@Bean
	@Qualifier("/ping")
	HandlerFunction<ServerResponse> ping() {
		return (ServerRequest http) -> ServerResponse.ok()
			.body(http	.queryParam("isPing")
						.map(isPing -> flux(Boolean.valueOf(isPing)).next())
						.orElseGet(() -> Mono.just((PingPong) memory.findById("0").get()))
			,PingPong.class);
	}

	/**
	 * for testing ws :
	 * ws://localhost:8080/ws/ping?isPing=false&updateFrequency=500
	 */
	@Bean
	@Qualifier("/ws/ping")
	WebSocketContext.Handler pingWs() {
		return context -> flux(context.getBooleanParam("isPing"))
			.sampleFirst(Duration.ofMillis(context.getLngParam("updateFrequency")))
			.map(Object::toString)
			.map(context.session()::textMessage);
	}

}