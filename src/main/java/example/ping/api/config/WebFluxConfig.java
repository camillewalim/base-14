package example.ping.api.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import example.utils.ws.WebSocketTracker;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;



/**
 * Due to novelty of webflux, a set of boilerplate code components are needed to
 * lose some of spring framework features: h2 console, swagger, etc.
 * @author William Le Cam
 */
@Configuration
@EnableSwagger2WebFlux
public class WebFluxConfig implements WebFluxConfigurer {

	/**
	 * Let swagger access web files through netty
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/swagger-ui.html**")
			.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	WebSocketTracker webSocketTracker() { return WebSocketTracker.create(); }

	/**
	 * @author user H2 use tomcat, not netty -> so you need a dedicated server
	 *         for this
	 */
	@Component
	public class H2 {

		private Server webServer;

		@EventListener(ContextRefreshedEvent.class)
		public void start() {
			try {
				this.webServer = org.h2.tools.Server
					.createWebServer("-webPort", "8082", "-tcpAllowOthers")
					.start();
			} catch (SQLException e) {
				System.out.print(" h2: port already in use.");
			}
		}

		@EventListener(ContextClosedEvent.class)
		public void stop() { this.webServer.stop(); }

	}

}
