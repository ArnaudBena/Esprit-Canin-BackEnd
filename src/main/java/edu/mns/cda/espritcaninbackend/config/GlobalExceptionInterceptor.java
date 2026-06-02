package edu.mns.cda.espritcaninbackend.config;

import edu.mns.cda.espritcaninbackend.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global d'exceptions pour transformer les exceptions Java en réponses HTTP propres.
 *
 * Convention du projet : exceptions UNCHECKED (héritent de RuntimeException)
 * Toutes les exceptions custom du projet (XxxNotFoundException) héritent de RuntimeException.
 *  - Pas besoin de `throws XxxException` dans toutes les signatures de méthodes.
 *  - Les exceptions remontent naturellement jusqu'à ce @RestControllerAdvice qui les mappe en HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionInterceptor {

    //Intercepte les exceptions du à un JSON qui ne respecte pas des contraintes (@NotBlank, @Size...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> constraintViolationInterceptor(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errors;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> constraintViolationDatabase(DataIntegrityViolationException ex) {

        return Map.of("erreur", "Erreur de contrainte dans la base de données");
    }

    @ExceptionHandler(SeanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> seanceNotFound(SeanceNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(InscriptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> inscriptionNotFound(InscriptionNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> roleNotFound(RoleNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(RaceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> raceNotFound(RaceNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(CompetenceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> competenceNotFound(CompetenceNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(ChienNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> chienNotFound(ChienNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(UtilisateurNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> utilisateurNotFound(UtilisateurNotFoundException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Erreur";
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("erreur", message));
    }

    @ExceptionHandler(InscriptionDoublonException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> inscriptionDoublon(InscriptionDoublonException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(SeanceAnnuleeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> seanceAnnulee(SeanceAnnuleeException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(SeancePasseeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> seancePassee(SeancePasseeException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(SeanceCompleteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> seanceComplete(SeanceCompleteException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(EligibiliteAgeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> eligibiliteAge(EligibiliteAgeException ex) {
        return Map.of("erreur", ex.getMessage());
    }

    @ExceptionHandler(EligibiliteCompetenceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> eligibiliteCompetence(EligibiliteCompetenceException ex) {
        return Map.of("erreur", ex.getMessage());
    }
}