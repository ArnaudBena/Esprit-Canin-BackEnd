package edu.mns.cda.espritcaninbackend.unit.model;

import edu.mns.cda.espritcaninbackend.model.Role;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import edu.mns.cda.espritcaninbackend.utile.TestUtilitaire;
import edu.mns.cda.espritcaninbackend.utile.ValidationGroupe;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

public class UtilisateurUnitTest {

    protected static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Un utilisateur avec un email vide doit échouer la validation (@NotBlank)")
    public void validUtilisateurWithBlankEmail_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "email", "NotBlank");

        Assertions.assertTrue(erreurExiste, "La contrainte @NotBlank sur email n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un email mal formé doit échouer la validation (@Email)")
    public void validUtilisateurWithInvalidEmail_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("pas-un-email");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "email", "Email");

        Assertions.assertTrue(erreurExiste, "La contrainte @Email sur email n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un email de plus de 100 caractères doit échouer la validation (@Size)")
    public void validUtilisateurWithEmailTooLong_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("a".repeat(100) + "@mail.fr");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "email", "Size");

        Assertions.assertTrue(erreurExiste, "La contrainte @Size(max=100) sur email n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec tous les champs valides doit passer la validation")
    public void validUtilisateurWithAllFieldsValid_shouldBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setEmail("jean.dupont@mail.fr");
        utilisateur.setPassword("motdepasse123");
        utilisateur.setDateInscription(LocalDate.now());
        utilisateur.setRole(new Role());
        // telephone est optionnel, on peut le laisser null

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        Assertions.assertTrue(violations.isEmpty(),
                "L'utilisateur aurait dû être valide mais " + violations.size() + " violation(s) détectée(s)");
    }

    @Test
    @DisplayName("Un utilisateur avec un nom vide doit échouer la validation (@NotBlank)")
    public void validUtilisateurWithBlankNom_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "nom", "NotBlank");

        Assertions.assertTrue(erreurExiste, "La contrainte @NotBlank sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un nom de plus de 50 caractères doit échouer la validation (@Size)")
    public void validUtilisateurWithNomTooLong_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("a".repeat(51));

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "nom", "Size");

        Assertions.assertTrue(erreurExiste, "La contrainte @Size(max=50) sur nom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un prenom vide doit échouer la validation (@NotBlank)")
    public void validUtilisateurWithBlankPrenom_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom("");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "prenom", "NotBlank");

        Assertions.assertTrue(erreurExiste, "La contrainte @NotBlank sur prenom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un prenom de plus de 50 caractères doit échouer la validation (@Size)")
    public void validUtilisateurWithPrenomTooLong_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom("a".repeat(51));

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "prenom", "Size");

        Assertions.assertTrue(erreurExiste, "La contrainte @Size(max=50) sur prenom n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un password vide doit échouer la validation (@NotBlank)")
    public void validUtilisateurWithBlankPassword_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPassword("");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "password", "NotBlank");

        Assertions.assertTrue(erreurExiste, "La contrainte @NotBlank sur password n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un password de moins de 8 caractères doit échouer la validation (@Size)")
    public void validUtilisateurWithPasswordTooShort_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPassword("abc");

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "password", "Size");

        Assertions.assertTrue(erreurExiste, "La contrainte @Size(min=8) sur password n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec une dateInscription null doit échouer la validation (@NotNull)")
    public void validUtilisateurWithNullDateInscription_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        // dateInscription reste null volontairement

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "dateInscription", "NotNull");

        Assertions.assertTrue(erreurExiste, "La contrainte @NotNull sur dateInscription n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un role null doit échouer la validation (@NotNull)")
    public void validUtilisateurWithNullRole_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        // role reste null volontairement

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "role", "NotNull");

        Assertions.assertTrue(erreurExiste, "La contrainte @NotNull sur role n'a pas fonctionné");
    }

    @Test
    @DisplayName("Un utilisateur avec un téléphone de plus de 20 caractères doit échouer la validation (@Size)")
    public void validUtilisateurWithTelephoneTooLong_shouldNotBeValid() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setTelephone("a".repeat(21));

        Set<ConstraintViolation<Utilisateur>> violations = validator.validate(utilisateur, ValidationGroupe.OnCreate.class);

        boolean erreurExiste = TestUtilitaire.constraintViolationExist(violations, "telephone", "Size");

        Assertions.assertTrue(erreurExiste, "La contrainte @Size(max=20) sur telephone n'a pas fonctionné");
    }
}
