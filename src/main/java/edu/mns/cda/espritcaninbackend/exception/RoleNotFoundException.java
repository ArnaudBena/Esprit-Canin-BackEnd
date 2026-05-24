package edu.mns.cda.espritcaninbackend.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Integer id) {
        super("Rôle introuvable avec l'id : " + id);
    }
}
