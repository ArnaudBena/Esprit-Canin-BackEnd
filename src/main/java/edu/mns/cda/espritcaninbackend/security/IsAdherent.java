package edu.mns.cda.espritcaninbackend.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gestion des droits et accès pour le role Adhrent
 * Meta-annotation = alias plus lisbile pour @PreAuthorize("hasRole('ADHERENT')").
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADHERENT')")
public @interface IsAdherent {
}
