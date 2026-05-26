package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Sexe;
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
}