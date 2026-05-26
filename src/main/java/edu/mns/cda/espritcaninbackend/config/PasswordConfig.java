package edu.mns.cda.espritcaninbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Expose le bean PasswordEncoder utilisé par toute l'app pour hasher et vérifier les mots de passe.
 * BCrypt = standard recommandé (lent volontairement + sel automatique).
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
