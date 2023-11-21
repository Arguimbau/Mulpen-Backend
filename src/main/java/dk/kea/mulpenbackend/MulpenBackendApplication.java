package dk.kea.mulpenbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class MulpenBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MulpenBackendApplication.class, args);
	}

}
