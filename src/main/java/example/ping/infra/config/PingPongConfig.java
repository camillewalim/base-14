package example.ping.infra.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import example.ping.domain.model.PingPong;
import example.ping.domain.service.PingPongService;
import reactor.core.publisher.Flux;

/**
 * @author camille.walim
 * 
 * Configuration for declaring services
 */
@Configuration
public class PingPongConfig {

	@Bean
	Function<Boolean,Flux<PingPong>> service(){
		return new PingPongService();
	}
}
