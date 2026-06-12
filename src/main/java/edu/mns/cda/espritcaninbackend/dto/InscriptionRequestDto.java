package edu.mns.cda.espritcaninbackend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de requête : inscrire un chien à une séance.
 */
public record InscriptionRequestDto(
        @NotNull(message = "L'identifiant du chien est obligatoire") Integer chienId,
        @NotNull(message = "L'identifiant de la séance est obligatoire") Integer seanceId
) {}