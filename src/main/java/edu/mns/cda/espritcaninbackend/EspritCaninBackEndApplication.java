package edu.mns.cda.espritcaninbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EspritCaninBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(EspritCaninBackEndApplication.class, args);
    }

}
