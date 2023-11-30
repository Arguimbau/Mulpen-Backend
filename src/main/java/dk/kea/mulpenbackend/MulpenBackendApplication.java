package dk.kea.mulpenbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@ComponentScan(basePackages = {"dk.kea.mulpenbackend.api", "dk.kea.mulpenbackend.Service", "dk.kea.mulpenbackend.config", "dk.kea.mulpenbackend.Repository"})
public class MulpenBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MulpenBackendApplication.class, args);
	}

}
