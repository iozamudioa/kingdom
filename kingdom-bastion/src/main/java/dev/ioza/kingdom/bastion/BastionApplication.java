package dev.ioza.kingdom.bastion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BastionApplication {

	public static void main(String[] args) {
		log.info("**************** BASTION NUEVO ****************");
		SpringApplication.run(BastionApplication.class, args);
	}

}
