package edu.mns.cda.espritcaninbackend.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restreint l'accès à un endpoint à TOUT utilisateur authentifié, quel que soit son rôle.
 * Chacun accède au sien via @AuthenticationPrincipal.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADHERENT','COACH','ADMIN')")
public @interface IsConnecte {
}
