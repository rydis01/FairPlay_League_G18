package FairplayLeagueG18;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
/**
 * Startar upp applikationen med springboot
 * @author Alla
 */
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
