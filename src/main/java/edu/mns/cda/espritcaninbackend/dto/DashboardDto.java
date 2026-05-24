package edu.mns.cda.espritcaninbackend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.view.SeanceView;

import java.util.List;

/**
 * DTO (Data Transfert Object) regroupant toutes les données admin que je veux afficher sur le dashboard.
 * Tous les KPIs (important pour les équipes métier) + liste des prochaines séances
 *
 * Un record car c'est en lecture seule, dédié au transport JSON.
 */
@JsonView(SeanceView.class)
public record DashboardDto(
        long totalUtilisateurs,
        long nouveauxUtilisateursCeMois,
        long totalChiens,
        long seancesMois,
        long seancesMoisDernier,
        int tauxRemplissageMoyen,
        List<Seance> prochainesSeances
) {}
