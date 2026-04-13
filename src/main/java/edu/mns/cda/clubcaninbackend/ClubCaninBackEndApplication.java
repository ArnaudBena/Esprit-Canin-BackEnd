package edu.mns.cda.clubcaninbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ClubCaninBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubCaninBackEndApplication.class, args);
    }

}
