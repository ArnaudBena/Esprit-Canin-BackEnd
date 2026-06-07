package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Sexe;
import edu.mns.cda.espritcaninbackend.security.IsAdherent;
import edu.mns.cda.espritcaninbackend.security.IsAdmin;
import edu.mns.cda.espritcaninbackend.security.UtilisateurDetails;
import edu.mns.cda.espritcaninbackend.service.ChienService;
import edu.mns.cda.espritcaninbackend.view.ChienView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Chien", description = """
        API de gestion des chiens du club esprit canin.
        Permet de récupérer, créer, modifier et supprimer des chiens.
        Toutes les réponses sont au format JSON.
        """)
@RequiredArgsConstructor
@RequestMapping("/chien")
@CrossOrigin
public class ChienController {

    protected final ChienService chienService;

    @GetMapping("/list")
    @Operation(
            summary = "Lister tous les chiens",
            description = """
                    Retourne la liste complète de tous les chiens enregistrés en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucun chien n'existe.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @IsAdmin
    @JsonView(ChienView.class)
    public List<Chien> getAllChiens() {
        return chienService.findAll();
    }

    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des chiens avec filtres",
            description = """
                    Retourne la liste filtrée des chiens selon 2 filtres optionnels :
                    recherche libre dans nom du chien, race, nom/prénom du propriétaire + sexe.
                    Tri imposé serveur : nom du chien ASC.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste filtrée retournée avec succès")
    })
    @IsAdmin
    @JsonView(ChienView.class)
    public List<Chien> searchChiens(
            @Parameter(description = "Texte libre cherché dans nom du chien, race, nom/prénom du propriétaire")
            @RequestParam(required = false) String recherche,
            @Parameter(description = "Sexe du chien (MALE ou FEMELLE)")
            @RequestParam(required = false) Sexe sexe
    ) {
        return chienService.search(recherche, sexe);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un chien par son ID",
            description = """
                    Retourne les détails d'un chien à partir de son identifiant unique passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chien trouvé et retourné avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucun chien ne correspond à cet ID")
    })
    @IsAdmin
    @JsonView(ChienView.class)
    public ResponseEntity<Chien> get(
            @Parameter(description = "Identifiant unique du chien", required = true, example = "1")
            @PathVariable Integer id
    ) {
        Optional<Chien> optionalChien = chienService.findById(id);

        if (optionalChien.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalChien.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer un nouveau chien",
            description = """
                    Crée un nouveau chien à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base.
                    Les champs obligatoires sont validés via @Valid.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chien créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    @IsAdmin
    @JsonView(ChienView.class)
    public ResponseEntity<Chien> createChien(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Chien à créer. L'ID sera ignoré."
            )
            @RequestBody
            @Valid Chien chienToInsert
    ) {
        chienService.insert(chienToInsert);

        return new ResponseEntity<>(chienToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un chien par son ID",
            description = """
            Supprime définitivement le chien correspondant à l'ID passé en path variable.
            Refusé (409 Conflict) si le chien a au moins une inscription enregistrée.
            Retourne 404 si l'ID est introuvable, 204 si la suppression est réussie.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chien supprimé avec succès, aucun contenu retourné"),
            @ApiResponse(responseCode = "404", description = "Aucun chien ne correspond à cet ID"),
            @ApiResponse(responseCode = "409", description = "Le chien a des inscriptions enregistrées, suppression refusée")
    })
    @IsAdmin
    public ResponseEntity<Void> deleteChien(
            @Parameter(description = "Identifiant unique du chien à supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {
        chienService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un chien existant",
            description = """
                    Met à jour les informations d'un chien existant identifié par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Chien mis à jour avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun chien ne correspond à cet ID"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    @IsAdmin
    public ResponseEntity<Void> updateChien(
            @Parameter(description = "Identifiant unique du chien à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Chien avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL.",
                    required = true
            )
            @RequestBody
            @Valid
            Chien chienToUpdate
    ) {
        chienService.update(id, chienToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Espace adhérent : mes chiens (self-service, owner-only)
    // Cloisonnement strict : l'adhérent ne touche QUE ses propres chiens.
    // Le propriétaire vient toujours du token, jamais du corps JSON.
    @GetMapping("/mes-chiens")
    @Operation(
            summary = "Lister mes chiens (adhérent connecté)",
            description = """
                    Retourne la liste des chiens appartenant à l'utilisateur connecté.
                    Le propriétaire est déterminé à partir du token (jamais du client).
                    Retourne un tableau vide si l'adhérent n'a aucun chien.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste de mes chiens retournée avec succès")
    })
    @IsAdherent
    @JsonView(ChienView.class)
    public List<Chien> getMesChiens(@AuthenticationPrincipal UtilisateurDetails userDetails) {
        return chienService.findByProprietaire(userDetails.getUtilisateur().getId());
    }

    @GetMapping("/mes-chiens/{id}")
    @Operation(
            summary = "Récupérer un de mes chiens (fiche détail, adhérent connecté)",
            description = """
                    Retourne le détail d'un chien appartenant à l'utilisateur connecté,
                    inscriptions + séances incluses (séances à venir / historique côté front).
                    Retourne 404 si le chien n'existe pas, 403 s'il n'appartient pas à l'adhérent.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chien trouvé et retourné avec succès"),
            @ApiResponse(responseCode = "403", description = "Ce chien n'appartient pas à l'adhérent connecté"),
            @ApiResponse(responseCode = "404", description = "Aucun chien ne correspond à cet ID")
    })
    @IsAdherent
    @JsonView(ChienView.class)
    public ResponseEntity<Chien> getMonChien(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @PathVariable Integer id
    ) {
        Optional<Chien> optionalChien = chienService.findById(id);
        if (optionalChien.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // le chien doit appartenir au user connecté, sinon 403
        if (!optionalChien.get().getUtilisateur().getId().equals(userDetails.getUtilisateur().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(optionalChien.get(), HttpStatus.OK);
    }

    @PostMapping("/mes-chiens")
    @Operation(
            summary = "Ajouter un de mes chiens (adhérent connecté)",
            description = """
                    Crée un chien rattaché à l'utilisateur connecté.
                    L'ID et le propriétaire fournis dans le corps sont ignorés :
                    l'ID est généré par la base, le propriétaire est forcé depuis le token.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chien créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    @IsAdherent
    @JsonView(ChienView.class)
    public ResponseEntity<Chien> createMonChien(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @RequestBody @Valid Chien chienToInsert
    ) {
        chienToInsert.setId(null);
        chienToInsert.setUtilisateur(userDetails.getUtilisateur()); // propriétaire forcé depuis le token
        chienService.insert(chienToInsert);

        return new ResponseEntity<>(chienToInsert, HttpStatus.CREATED);
    }

    @PutMapping("/mes-chiens/{id}")
    @Operation(
            summary = "Modifier un de mes chiens (adhérent connecté)",
            description = """
                    Met à jour un chien appartenant à l'utilisateur connecté.
                    Retourne 404 si le chien n'existe pas, 403 s'il n'appartient pas à l'adhérent.
                    Le propriétaire est ré-forcé depuis le token (anti-réassignation).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chien mis à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Ce chien n'appartient pas à l'adhérent connecté"),
            @ApiResponse(responseCode = "404", description = "Aucun chien ne correspond à cet ID"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    @IsAdherent
    public ResponseEntity<Void> updateMonChien(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @PathVariable Integer id,
            @RequestBody @Valid Chien chienToUpdate
    ) {
        Optional<Chien> optionalChien = chienService.findById(id);
        if (optionalChien.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // le chien doit appartenir au user connecté, sinon 403
        if (!optionalChien.get().getUtilisateur().getId().equals(userDetails.getUtilisateur().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        chienToUpdate.setId(id);
        chienToUpdate.setUtilisateur(userDetails.getUtilisateur()); // anti-réassignation du propriétaire
        chienService.update(id, chienToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/mes-chiens/{id}")
    @Operation(
            summary = "Supprimer un de mes chiens (adhérent connecté)",
            description = """
                    Supprime un chien appartenant à l'utilisateur connecté.
                    Retourne 404 si le chien n'existe pas, 403 s'il n'appartient pas à l'adhérent,
                    409 si le chien a des inscriptions enregistrées (conservation de l'historique).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chien supprimé avec succès"),
            @ApiResponse(responseCode = "403", description = "Ce chien n'appartient pas à l'adhérent connecté"),
            @ApiResponse(responseCode = "404", description = "Aucun chien ne correspond à cet ID"),
            @ApiResponse(responseCode = "409", description = "Le chien a des inscriptions enregistrées, suppression refusée")
    })
    @IsAdherent
    public ResponseEntity<Void> deleteMonChien(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @PathVariable Integer id
    ) {
        Optional<Chien> optionalChien = chienService.findById(id);
        if (optionalChien.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!optionalChien.get().getUtilisateur().getId().equals(userDetails.getUtilisateur().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        chienService.delete(id); // garde la règle métier 409 si inscriptions

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}