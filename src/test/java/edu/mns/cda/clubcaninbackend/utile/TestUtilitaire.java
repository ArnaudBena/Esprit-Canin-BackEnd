package edu.mns.cda.clubcaninbackend.utile;


import jakarta.validation.ConstraintViolation;

import java.util.Set;

/**
 * Classe utilitaire pour les tests de validation Bean Validation.
 * Factorise la vérification "est-ce qu'une violation existe sur tel champ
 * et provient de telle annotation ?" afin d'éviter le copié-collé dans chaque test.
 */
public class TestUtilitaire {

    public static boolean constraintViolationExist(
            Set<ConstraintViolation<Object>> constraintViolations,
            String fieldName,
            String annotationName
    ) {
        return constraintViolations.stream()
                .anyMatch(contrainte -> {
            String champs = contrainte.getPropertyPath().toString();
            String erreur = contrainte
                    .getConstraintDescriptor()
                    .getAnnotation()
                    .annotationType()
                    .getSimpleName();
            return champs.equals(fieldName) && erreur.equals(annotationName);
        });
    }
}
