package nl.stackftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
		EmbeddedServletContainerAutoConfiguration.class,
		WebMvcAutoConfiguration.class
})
@EnableCaching
public class StackftpApplication {

	public static void main(String[] args) {
		SpringApplication.run(StackftpApplication.class, args);
	}
}
