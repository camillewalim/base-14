package example.ping.api.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.Collections;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import example.utils.ws.WebSocketContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;



/**
 * @author camille.walim
 * 
 *         Configuration from a network point of view : websocket, routes,
 *         filters, handlers, etc.
 */
@Configuration
class ApiConfig {

	/**
	 * enable swagger for documentation
	 */
	@Bean
	public Docket swagger() {
		return new Docket(DocumentationType.SWAGGER_2)
			.apiInfo(new ApiInfoBuilder()
				.description("Swagger do not work with WebFlux+Router yet : https://github.com/springfox/springfox/issues/2799.")
				.title("marketdata api").version("0.0.1-SNAPSHOT").build())
			.genericModelSubstitutes(Mono.class, Flux.class, Publisher.class)
			.select().apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any()).build();

	}

	/**
	 * routes for websockets protocols
	 */
	@Bean
	HandlerMapping webSockets(
		@Qualifier("/ws/ping") WebSocketContext.Handler ping
		// add more websockets if you want
	) {
		return new SimpleUrlHandlerMapping() {
			{
				setUrlMap(Collections.singletonMap("/ws/ping", WebSocketContext.handler(ping)));
				setOrder(10);
			}
		};
	}

	/**
	 * routes for http/s protocols
	 */
	@Bean
	RouterFunction<ServerResponse> routes(
		@Qualifier("/ping") HandlerFunction<ServerResponse> hello
		// add more http endpoints if you want
	) {
		return route(GET("/ping"), hello);
	}

	/**
	 * cors for UI
	 */
	@Bean
	CorsWebFilter cors() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Collections.singletonList("*"));
		configuration.setAllowedHeaders(Collections.singletonList("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return new CorsWebFilter(source);
	}

}