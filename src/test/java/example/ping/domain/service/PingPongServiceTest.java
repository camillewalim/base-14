package example.ping.domain.service;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import reactor.test.StepVerifier;

/**
 * @author camille.walim
 * Step-Based Testing of Services
 */
class PingPongServiceTest {

	PingPongService service = new PingPongService();
	
	@Test
	void generatorIsPingBounded() {
		Consumer<Boolean> isPingTest = isPing ->
			StepVerifier.create(service.apply(isPing).take(1))
				.expectNextMatches(p -> p.ping==isPing)
				.verifyComplete();
		isPingTest.accept(true);
		isPingTest.accept(false);
	}
}
