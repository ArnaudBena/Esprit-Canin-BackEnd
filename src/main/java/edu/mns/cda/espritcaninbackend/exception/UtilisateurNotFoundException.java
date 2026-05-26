package edu.mns.cda.espritcaninbackend.exception;

public final class UtilisateurNotFoundException extends RuntimeException {
    public UtilisateurNotFoundException(Integer id) {
        super("Utilisateur introuvable avec l'id : " + id);
    }
}
