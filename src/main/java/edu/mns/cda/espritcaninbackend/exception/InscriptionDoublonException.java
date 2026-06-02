package edu.mns.cda.espritcaninbackend.exception;

public final class InscriptionDoublonException extends RuntimeException {
    public InscriptionDoublonException(Integer chienId, Integer seanceId) {
        super("Ce chien (id " + chienId + ") est déjà inscrit à cette séance (id " + seanceId + ")");
    }
}