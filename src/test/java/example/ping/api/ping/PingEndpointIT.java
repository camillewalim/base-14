package example.ping.api.ping;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import example.ping.Application;
import example.ping.domain.model.PingPong;
import reactor.core.publisher.Flux;

/**
 * @author camille.walim
 *
 * Mockito-Integration-Test based of endpoint
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class PingEndpointIT {

	@Autowired
	WebTestClient web;

	@SpyBean
	Function<Boolean, Flux<PingPong>> pingpong;
	@MockBean
	JpaRepository<PingPong, String> memory;

	@Test
	void ping_200() {
		web	.get().uri("/ping?isPing=true")
			.exchange().expectStatus().is2xxSuccessful();
		verify(pingpong,times(1)).apply(any());
		verify(memory,times(1)).saveAndFlush(any());
	}
	
	@Test
	void ping_500() {
		web	.get().uri("/ping")
			.exchange().expectStatus().is5xxServerError();
		verify(pingpong,times(0)).apply(any());
		verify(memory,times(0)).saveAndFlush(any());
	}

}
