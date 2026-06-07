package edu.mns.cda.espritcaninbackend.exception;

public final class EligibiliteAgeException extends RuntimeException {
    public EligibiliteAgeException(int ageChienMois, Integer ageMinMois, Integer ageMaxMois) {
        super("Le chien a " + ageChienMois + " mois, hors des bornes d'âge requises pour ce type de séance "
                + "(min : " + (ageMinMois != null ? ageMinMois + " mois" : "aucune")
                + ", max : " + (ageMaxMois != null ? ageMaxMois + " mois" : "aucune") + ")");
    }
}