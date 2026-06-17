package edu.mns.cda.espritcaninbackend.exception;

import edu.mns.cda.espritcaninbackend.model.NiveauCompetence;

public final class EligibiliteCompetenceException extends RuntimeException {
    public EligibiliteCompetenceException(String competenceNom, NiveauCompetence requis, NiveauCompetence actuel) {
        super("Le chien n'a pas le niveau requis en '" + competenceNom + "' "
                + "(requis : " + requis + ", actuel : " + (actuel == null ? "non acquis" : actuel) + ")");
    }
}