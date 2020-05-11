package example.ping;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author camille.walim
 *
 * Verify that non-DDD but basic components are in place:
 *  - web: swagger, jackson
 *  - data: h2, flyway, hibernate
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class ApplicationIT {
	
	@Autowired
	WebTestClient web;
	
	@Test
	void swagger_is_here(){
		web	.get().uri("/swagger-ui.html").exchange()
			.expectStatus().is2xxSuccessful();
	}

	@Test
	void h2_console_is_here(){
		web	.get().uri("http://localhost:8082/login.do").exchange()
			.expectStatus().is2xxSuccessful();
	}
	
	@Autowired
	DataSource source;
	@Test
	void hibernate_is_here(){
		assertNotNull(source);
	}
	
	@Autowired
	Flyway flyway;
	@Test
	void flyway_is_here(){
		assertNotNull(flyway);
	}
	
	@Autowired
	ObjectMapper jackson;
	@Test
	void jackson_is_here(){
		assertNotNull(jackson);
	}
	
}
