package example.ping.domain.service;

import java.util.function.Function;

import example.ping.domain.model.PingPong;
import reactor.core.publisher.Flux;



/**
 * dummy service
 * 
 * @author camille.walim
 */
public class PingPongService
	implements
	Function<Boolean, Flux<PingPong>> {

	public Flux<PingPong> apply(Boolean isPing) {
		return Flux.<PingPong>generate(sink -> sink.next(new PingPong(isPing)));
	}
}
