package example.ping.domain.model;

import static org.junit.Assert.assertTrue;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.booleans;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

/**
 * @author camille.walim
 * Property-Based Testing of model
 */
class PingPongTest {

	@Test
	void uuidIsNonNullAndUnique() {
		var uniques = new HashSet<String>();
		qt()
		.forAll(booleans().all())
		.as(PingPong::new)
		.checkAssert(p->{
			assertTrue(p.uuid!=null && ! uniques.contains(p.uuid));
			uniques.add(p.uuid);
		});
	}
}
