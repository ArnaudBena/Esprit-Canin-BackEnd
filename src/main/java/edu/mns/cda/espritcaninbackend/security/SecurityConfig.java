package edu.mns.cda.espritcaninbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration centrale de Spring Security.
 *
 * - CSRF désactivé : pas besoin pour une API stateless (CSRF protège les
 *   sessions cookie, je n'en ai pas).
 * - Sessions STATELESS : pas de HttpSession côté serveur, chaque requête
 *   doit porter son JWT.
 * - CORS activé : le front Angular tourne sur un autre port (4200) et
 *   doit pouvoir appeler le back (8080).
 * - JwtFilter branché AVANT UsernamePasswordAuthenticationFilter : on
 *   alimente le SecurityContext depuis le JWT avant les filtres standards.
 * - @EnableMethodSecurity : active @PreAuthorize sur les méthodes des
 *   controllers (sécu fine par endpoint, plutôt que par URL).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    protected final PasswordEncoder passwordEncoder;
    protected final UtilisateurDetailsService utilisateurDetailsService;
    protected final JwtFilter jwtFilter;

    /**
     * AuthenticationProvider utilisé par AuthenticationManager pour le login :
     * - charge le user via UserDetailsService (= notre UtilisateurDetailsService)
     * - vérifie le password via PasswordEncoder (= BCrypt)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(utilisateurDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Config CORS permissive pour le dev (origin *, toutes méthodes, tous headers).
     * À durcir en prod : remplacer "*" par l'URL exacte du front.
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));
        corsConfiguration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
