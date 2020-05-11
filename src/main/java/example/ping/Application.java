package example.ping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;


/**
 * @author  camille.walim
 * 
 * Boot Spring Application
 */
@SpringBootApplication
@EnableWebFlux
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
}
