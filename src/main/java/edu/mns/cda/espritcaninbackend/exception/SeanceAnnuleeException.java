package edu.mns.cda.espritcaninbackend.exception;

public final class SeanceAnnuleeException extends RuntimeException {
    public SeanceAnnuleeException(Integer seanceId) {
        super("La séance (id " + seanceId + ") est annulée : inscription impossible");
    }
}