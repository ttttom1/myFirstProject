package hello.hello_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@EnableFeignClients
@SpringBootApplication
public class HelloSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloSpringApplication.class, args);
	}

}
