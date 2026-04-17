package edu.mns.cda.clubcaninbackend.exception;

public final class SeanceNotFoundException extends RuntimeException {
    public SeanceNotFoundException(Integer id) {
        super("Séance introuvable avec l'id : " + id);
    }
}
