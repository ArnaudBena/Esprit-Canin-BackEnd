package edu.mns.cda.espritcaninbackend.exception;

public final class SeanceCompleteException extends RuntimeException {
    public SeanceCompleteException(Integer seanceId, int placesMax) {
        super("Séance complète (id " + seanceId + ") : " + placesMax + "/" + placesMax + " places occupées");
    }
}