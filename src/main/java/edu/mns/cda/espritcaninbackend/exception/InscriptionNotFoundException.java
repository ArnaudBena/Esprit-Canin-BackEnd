package edu.mns.cda.espritcaninbackend.exception;

public final class InscriptionNotFoundException extends RuntimeException {
    public InscriptionNotFoundException(Integer idChien, Integer idSeance) {
        super("Inscription introuvable pour le chien " + idChien + " et la séance " + idSeance);
    }
}
