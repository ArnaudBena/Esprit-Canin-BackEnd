package edu.mns.cda.espritcaninbackend.dto;

import edu.mns.cda.espritcaninbackend.model.NiveauCompetence;

public record PrerequisDto(
        String competence,
        NiveauCompetence niveauMinimumRequis
) {}