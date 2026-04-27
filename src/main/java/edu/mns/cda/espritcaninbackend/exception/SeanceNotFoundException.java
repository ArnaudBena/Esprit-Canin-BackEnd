package edu.mns.cda.espritcaninbackend.exception;

public final class SeanceNotFoundException extends RuntimeException {
    public SeanceNotFoundException(Integer id) {
        super("Séance introuvable avec l'id : " + id);
    }
}
