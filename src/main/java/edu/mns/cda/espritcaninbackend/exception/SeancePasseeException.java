package edu.mns.cda.espritcaninbackend.exception;

public final class SeancePasseeException extends RuntimeException {
    public SeancePasseeException(Integer seanceId) {
        super("La séance (id " + seanceId + ") est déjà passée : inscription impossible");
    }
}
