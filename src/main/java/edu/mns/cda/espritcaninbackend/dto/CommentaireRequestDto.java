package edu.mns.cda.espritcaninbackend.dto;

import jakarta.validation.constraints.Size;

public record CommentaireRequestDto(
        @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères") String commentaire
) {}