package edu.mns.cda.espritcaninbackend.exception;

public final class ChienNotFoundException extends RuntimeException {
    public ChienNotFoundException(Integer id) {
        super("Chien introuvable avec l'id : " + id);
    }
}
