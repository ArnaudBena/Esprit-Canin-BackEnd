package edu.mns.cda.espritcaninbackend.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gestion des droits et accès pour le role Coach
 * Meta-annotation = alias plus lisbile pour @PreAuthorize("hasRole('COACH')").
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('COACH')")
public @interface IsCoach {
}
