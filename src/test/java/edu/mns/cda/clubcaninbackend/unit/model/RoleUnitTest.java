package edu.mns.cda.clubcaninbackend.unit.model;

import edu.mns.cda.clubcaninbackend.model.Role;
import edu.mns.cda.clubcaninbackend.utile.TestUtilitaire;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class RoleUnitTest {

    protected static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Un rôle avec un nom vide doit échouer la validation (@NotBlank)")
    public void validRoleWithBlankNom_shouldNotBeValid() {
        Role role = new Role();
        role.setNom("");

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        boolean erreurExiste = violations.stream().anyMatch(contrainte -> {
            String champs = contrainte.getPropertyPath().toString();
            String erreur = contrainte.getConstraintDescriptor()
                    .getAnnotation().annotationType().getSimpleName();
            return champs.equals("nom") && erreur.equals("NotBlank");
        });

        Assertions.assertTrue(erreurExiste, "La contrainte @NotBlank sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un rôle avec un nom de moins de 3 caractères doit échouer la validation (@Length)")
    public void validRoleWithNomTooShort_shouldNotBeValid() {
        Role role = new Role();
        role.setNom("ab");

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(
                violations,
                "nom",
                "Length"
        );

        Assertions.assertTrue(erreurExiste, "La contrainte @Length(min=3) sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un rôle avec un nom de plus de 30 caractères doit échouer la validation (@Length)")
    public void validRoleWithNomTooLong_shouldNotBeValid() {
        Role role = new Role();
        role.setNom("a".repeat(31));

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(
                violations,
                "nom",
                "Length"
        );

        Assertions.assertTrue(erreurExiste, "La contrainte @Length(max=30) sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un role avec un nom valide doit passer la validation")
    public void validRoleWithNomValide_shouldBeValid() {
        Role role = new Role();
        role.setNom("Coach");

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        Assertions.assertTrue(violations.isEmpty(),
                "Le rôle aurait dû être valide mais " + violations.size() + " violation(s) détectée(s)");
    }
}
