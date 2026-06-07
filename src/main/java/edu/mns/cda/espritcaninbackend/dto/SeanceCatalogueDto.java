package edu.mns.cda.espritcaninbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.mns.cda.espritcaninbackend.model.StatutSeance;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Vue "catalogue" d'une séance pour l'espace adhérent.
 * DTO volontairement restreint : on n'expose JAMAIS la liste des inscrits
 * (chiens / propriétaires des autres adhérents), uniquement un compteur de places.
 */
public record SeanceCatalogueDto(
        Integer id,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm:ss") LocalTime heureDebut,
        Integer dureeMinutes,
        StatutSeance statut,
        Integer typeSeanceId,
        String typeLibelle,
        String typeDescription,
        Integer ageMinimumMois,
        Integer ageMaximumMois,
        Integer coachId,
        String coachNom,
        String coachPrenom,
        Integer participantsMaximum,
        Long nbInscrits
) {

    @JsonProperty
    public long placesRestantes() {
        long max = participantsMaximum == null ? 0 : participantsMaximum;
        long pris = nbInscrits == null ? 0 : nbInscrits;
        return Math.max(0, max - pris);
    }

    @JsonProperty
    public boolean complet() {
        long max = participantsMaximum == null ? 0 : participantsMaximum;
        long pris = nbInscrits == null ? 0 : nbInscrits;
        return pris >= max;
    }
}
