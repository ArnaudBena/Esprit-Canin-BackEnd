package edu.mns.cda.espritcaninbackend.config;

import edu.mns.cda.espritcaninbackend.exception.InscriptionNotFoundException;
import edu.mns.cda.espritcaninbackend.exception.SeanceNotFoundException;
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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Erreur";
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("erreur", message));
    }
}