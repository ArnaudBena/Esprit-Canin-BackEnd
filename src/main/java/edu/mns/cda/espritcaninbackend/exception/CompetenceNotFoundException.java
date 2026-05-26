package edu.mns.cda.espritcaninbackend.exception;

public final class CompetenceNotFoundException extends RuntimeException {
    public CompetenceNotFoundException(Integer id) {
        super("Compétence introuvable avec l'id : " + id);
    }
}
