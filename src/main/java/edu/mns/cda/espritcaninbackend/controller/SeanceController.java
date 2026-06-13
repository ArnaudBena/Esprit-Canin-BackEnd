package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dto.PrerequisDto;
import edu.mns.cda.espritcaninbackend.dto.SeanceCatalogueDto;
import edu.mns.cda.espritcaninbackend.model.Inscription;
import edu.mns.cda.espritcaninbackend.security.*;
import edu.mns.cda.espritcaninbackend.service.SeanceService;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.espritcaninbackend.view.InscriptionView;
import edu.mns.cda.espritcaninbackend.view.SeanceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Seance", description = """
        API de gestion des séances du club esprit canin.
        Permet de récupérer, créer, modifier et supprimer des séances planifiées.
        Toutes les réponses sont au format JSON.
        """)
@RequiredArgsConstructor
@RequestMapping("/seance")
@CrossOrigin
public class SeanceController {

    protected final SeanceService seanceService;

    @GetMapping("/list")
    @Operation(
            summary = "Lister toutes les séances",
            description = """
                    Retourne la liste complète de toutes les séances enregistrées en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucune séance n'existe.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @IsAdmin
    @JsonView(SeanceView.class)
    public List<Seance> getAllSeances() {
        return seanceService.findAll();
    }

    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des séances avec filtres",
            description = """
                    Retourne la liste filtrée des séances selon les 3 filtres optionnels (maquette ecran-15) :
                    type de séance, date exacte, coach.
                    Tri imposé serveur : date ASC, heureDebut ASC.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste filtrée retournée avec succès")
    })
    @IsAdmin
    @JsonView(SeanceView.class)
    public List<Seance> searchSeances(
            @Parameter(description = "ID du type de séance")
            @RequestParam(required = false) Integer typeSeanceId,
            @Parameter(description = "Date exacte (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "ID du coach assigné")
            @RequestParam(required = false) Integer coachId
    ) {
        return seanceService.search(typeSeanceId, date, coachId);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une séance par son ID",
            description = """
                    Retourne les détails d'une séance à partir de son identifiant unique passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Séance trouvée et retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune séance ne correspond à cet ID")
    })
    @IsAdmin
    @JsonView(SeanceView.class)
    public ResponseEntity<Seance> get(
            @Parameter(description = "Identifiant unique de la séance", required = true, example = "1")
            @PathVariable Integer id
    ) {
        Optional<Seance> optionalSeance = seanceService.findById(id);

        if (optionalSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalSeance.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle séance",
            description = """
                    Crée une nouvelle séance à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base.
                    Les champs sont validés via le groupe OnCreate (date obligatoirement dans le futur ou présent).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Séance créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    @IsAdmin
    @JsonView(SeanceView.class)
    public ResponseEntity<Seance> createSeance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Seance à créer. L'ID sera ignoré."
            )
            @RequestBody
            @Validated(ValidationGroupe.OnCreate.class) Seance seanceToInsert
    ) {

        seanceService.insert(seanceToInsert);

        return new ResponseEntity<>(seanceToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une séance par son ID",
            description = """
                    Supprime définitivement la séance correspondant à l'ID passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la suppression est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Séance supprimée avec succès, aucun contenu retourné"),
            @ApiResponse(responseCode = "404", description = "Aucune séance ne correspond à cet ID")
    })
    @IsAdmin
    public ResponseEntity<Seance> deleteSeance(
            @Parameter(description = "Identifiant unique de la séance à supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {

        seanceService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une séance existante",
            description = """
                    Met à jour les informations d'une séance existante identifiée par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Validation via le groupe OnUpdate (la date peut être dans le passé pour corriger une séance déjà tenue).
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Séance mise à jour avec succès, aucun contenu retourné"),
            @ApiResponse(responseCode = "404", description = "Aucune séance ne correspond à cet ID"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    @IsAdmin
    public ResponseEntity<Void> updateSeance(
            @Parameter(description = "Identifiant unique de la séance à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Seance avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL.",
                    required = true
            )
            @RequestBody
            @Validated(ValidationGroupe.OnUpdate.class)
            Seance seanceToUpdate
    ) {

        seanceService.update(id, seanceToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ===================================================================
    // Catalogue adhérent : séances à venir + ACTIVE.
    // Réponse via DTO restreint (aucune fuite des inscrits). Filtrage serveur.
    // ===================================================================

    @GetMapping("/catalogue")
    @Operation(
            summary = "Catalogue des séances à venir (adhérent connecté)",
            description = """
                    Retourne les séances à venir et actives, sous forme de DTO restreint
                    (sans la liste des inscrits). Filtres optionnels côté serveur :
                    type de séance, date exacte, disponibilité.
                    Tri imposé serveur : date ASC, heureDebut ASC.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catalogue retourné avec succès")
    })
    @IsAdherent
    public List<SeanceCatalogueDto> getCatalogue(
            @Parameter(description = "ID du type de séance")
            @RequestParam(required = false) Integer typeSeanceId,
            @Parameter(description = "Date exacte (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Disponibilité : true = places restantes, false = complètes, absent = toutes")
            @RequestParam(required = false) Boolean disponible
    ) {
        return seanceService.catalogue(typeSeanceId, date, disponible);
    }

    @GetMapping("/catalogue/{id}")
    @Operation(
            summary = "Détail catalogue d'une séance (adhérent connecté)",
            description = """
                    Retourne le détail d'une séance (DTO restreint, sans la liste des inscrits)
                    pour la page d'inscription. Retourne 404 si l'ID est introuvable.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Séance trouvée et retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune séance ne correspond à cet ID")
    })
    @IsAdherent
    public ResponseEntity<SeanceCatalogueDto> getCatalogueSeance(
            @Parameter(description = "Identifiant unique de la séance", required = true, example = "1")
            @PathVariable Integer id
    ) {
        return seanceService.catalogueDetail(id)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/catalogue/{id}/prerequis")
    @Operation(summary = "Compétences prérequises d'une séance (adhérent connecté)")
    @IsAdherent
    public List<PrerequisDto> getPrerequis(@PathVariable Integer id) {
        return seanceService.prerequis(id);
    }

    // Espace coach

    @GetMapping("/mes-seances")
    @Operation(summary = "Mes séances (coach connecté)")
    @IsCoach
    @JsonView(SeanceView.class)
    public List<Seance> getMesSeances(@AuthenticationPrincipal UtilisateurDetails userDetails,
                                      @RequestParam(required = false) String periode,
                                      @RequestParam(required = false) Integer typeSeanceId) {
        return seanceService.mesSeances(userDetails.getUtilisateur().getId(), periode, typeSeanceId);
    }

    @GetMapping("/mes-seances-a-traiter")
    @Operation(summary = "Mes séances passées dont l'appel reste à faire (coach connecté)")
    @IsCoach
    @JsonView(SeanceView.class)
    public List<Seance> getMesSeancesATraiter(@AuthenticationPrincipal UtilisateurDetails userDetails) {
        return seanceService.mesSeancesATraiter(userDetails.getUtilisateur().getId());
    }

    @GetMapping("/{id}/participants")
    @Operation(summary = "Participants (inscriptions) d'une de mes séances (coach) ou de n'importe quelle séance (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des participants"),
            @ApiResponse(responseCode = "403", description = "Pas le coach de cette séance"),
            @ApiResponse(responseCode = "404", description = "Séance introuvable")
    })
    @IsCoachOuAdmin
    @JsonView(InscriptionView.class)
    public ResponseEntity<List<Inscription>> getParticipants(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @PathVariable Integer id
    ) {
        Optional<Seance> optionalSeance = seanceService.findById(id);
        if (optionalSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Seance seance = optionalSeance.get();
        // le coach de la séance, OU un admin
        boolean estAdmin = "Admin".equalsIgnoreCase(userDetails.getUtilisateur().getRole().getNom());
        boolean estLeCoach = seance.getCoach() != null
                && seance.getCoach().getId().equals(userDetails.getUtilisateur().getId());
        if (!estAdmin && !estLeCoach) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(seance.getInscriptions(), HttpStatus.OK);
    }
}