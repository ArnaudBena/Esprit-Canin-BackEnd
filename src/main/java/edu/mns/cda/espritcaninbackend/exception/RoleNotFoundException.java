package edu.mns.cda.espritcaninbackend.exception;

public final class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Integer id) {
        super("Rôle introuvable avec l'id : " + id);
    }
}
