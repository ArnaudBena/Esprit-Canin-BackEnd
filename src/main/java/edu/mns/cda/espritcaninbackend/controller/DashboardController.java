package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dto.DashboardDto;
import edu.mns.cda.espritcaninbackend.service.DashboardService;
import edu.mns.cda.espritcaninbackend.view.SeanceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Dashboard", description = """
        API d'agrégation pour le tableau de bord admin.
        Retourne en une seule réponse les KPIs du club (utilisateurs, chiens, séances, taux de remplissage)
        et la liste des prochaines séances à venir.
        """)
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
@CrossOrigin
public class DashboardController {

    protected final DashboardService dashboardService;

    @GetMapping
    @Operation(
            summary = "Récupérer les données du dashboard admin",
            description = """
                    Retourne un objet DashboardDto contenant les KPIs du club et les 5 prochaines séances actives.
                    Aucun paramètre requis.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard retourné avec succès")
    })
    @JsonView(SeanceView.class)
    public DashboardDto getDashboard() {
        return dashboardService.getDashboard();
    }
}
