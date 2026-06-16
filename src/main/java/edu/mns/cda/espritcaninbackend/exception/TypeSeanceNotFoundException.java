package edu.mns.cda.espritcaninbackend.exception;

public final class TypeSeanceNotFoundException extends RuntimeException {
    public TypeSeanceNotFoundException(Integer id) {
        super("Type de séance introuvable avec l'id : " + id);
    }
}