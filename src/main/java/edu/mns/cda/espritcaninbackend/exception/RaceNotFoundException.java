package edu.mns.cda.espritcaninbackend.exception;

public final class RaceNotFoundException extends RuntimeException {
    public RaceNotFoundException(Integer id) {
        super("Race introuvable avec l'id : " + id);
    }
}
