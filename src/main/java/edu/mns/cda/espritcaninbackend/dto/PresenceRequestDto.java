package edu.mns.cda.espritcaninbackend.dto;

import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import jakarta.validation.constraints.NotNull;

public record PresenceRequestDto(
        @NotNull(message = "Le statut de présence est obligatoire") StatutPresence statutPresence
) {}