package dev.ioza.kingdom.tavern.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TavernHostApplication {

    public static void main(String[] args) {
        System.out.println("Starting Tavern Host Application...");
        System.out.println("########################################################");
        SpringApplication.run(TavernHostApplication.class, args);
    }

}
