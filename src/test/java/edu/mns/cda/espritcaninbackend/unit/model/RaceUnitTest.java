package edu.mns.cda.espritcaninbackend.unit.model;


import edu.mns.cda.espritcaninbackend.model.Race;
import edu.mns.cda.espritcaninbackend.utile.TestUtilitaire;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class RaceUnitTest {

    protected static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Une race avec un nom vide doit échouer la validation (@NotBlank)")
    public void validRaceWithBlankNom_shouldNotBeValid() {
        Race race = new Race();
        race.setNom("");

        Set<ConstraintViolation<Race>> violations = validator.validate(race);

        boolean erreurExiste = violations.stream().anyMatch(contrainte -> {
            String champs = contrainte.getPropertyPath().toString();
            String erreur = contrainte.getConstraintDescriptor()
                    .getAnnotation().annotationType().getSimpleName();
            return champs.equals("nom") && erreur.equals("NotBlank");
        });

        Assertions.assertTrue(erreurExiste, "La contrainte @NotBlank sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Une race avec un nom de moins de 3 caractères doit échouer la validation (@Length)")
    public void validRaceWithNomTooShort_shouldNotBeValid() {
        Race race = new Race();
        race.setNom("ab");

        Set<ConstraintViolation<Race>> violations = validator.validate(race);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(
                violations,
                "nom",
                "Length"
        );

        Assertions.assertTrue(erreurExiste, "La contrainte @Length(min=3) sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Une race avec un nom de plus de 50 caractères doit échouer la validation (@Length)")
    public void validRaceWithNomTooLong_shouldNotBeValid() {
        Race race = new Race();
        race.setNom("a".repeat(51));

        Set<ConstraintViolation<Race>> violations = validator.validate(race);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(
                violations,
                "nom",
                "Length"
        );

        Assertions.assertTrue(erreurExiste, "La contrainte @Length(max=50) sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Une race avec un nom valide doit passer la validation")
    public void validRaceWithNomValide_shouldBeValid() {
        Race race = new Race();
        race.setNom("Berger Malinois");

        Set<ConstraintViolation<Race>> violations = validator.validate(race);

        Assertions.assertTrue(violations.isEmpty(),
            "La race aurait dû être valide mais " + violations.size() + " violation(s) détectée(s)");
    }
}
