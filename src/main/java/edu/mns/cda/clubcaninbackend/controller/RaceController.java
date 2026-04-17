package edu.mns.cda.clubcaninbackend.controller;


import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.dao.RaceDao;
import edu.mns.cda.clubcaninbackend.model.Race;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.RaceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Race", description = """
        API de gestion des races canines.
        Permet de récupérer, créer, modifier et supprimer des races.
        Toutes les réponses sont au format JSON.
        """)
@RequiredArgsConstructor
@RequestMapping("/race")
@CrossOrigin
public class RaceController {

    protected final RaceDao raceDao;

    @GetMapping("/list")
    @Operation(
            summary = "Lister toutes les races",
            description = """
                    Retourne la liste complète de toutes les races enregistrées en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucune race n'existe
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste retournée avec succès"
            )
    })
    @JsonView(RaceView.class)
    public List<Race> getAllRaces() {
        return raceDao.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récuperer une race par son ID",
            description = """
                    Retourne les détails d'une race à partir de son identifiant unique passé en paramètre de chemin (path variable).
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Race trouvée et retournée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune race ne correspond à cet ID")
    })
    @JsonView(RaceView.class)
    public ResponseEntity<Race> get(
            @Parameter(description = "Identifiant unique de la race", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Race> optionalRace = raceDao.findById(id);

        if(optionalRace.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalRace.get(), HttpStatus.OK);

    }

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle race",
            description = """
                    Crée une nouvelle race à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base (autoincrémenté).
                    Les champs obligatoire sont validés via @Valid.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Race crée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    @JsonView(RaceView.class)
    public ResponseEntity<Race> createRace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Race à créer. L'ID sera ignoré."
            )
            @RequestBody
            @Valid Race raceToInsert
    ) {

        raceToInsert.setId(null);

        raceDao.save(raceToInsert);

        return new ResponseEntity<>(raceToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une race par son ID",
            description = """
                    Supprime définitevement la race correspondant à l'ID passé en path variable.
                    Retourne une erreur 404 s i l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la suppression est réussie.                    
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Race supprimée avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune race ne correspond à cet ID"
            )
    })
    public ResponseEntity<Race> deleteRace(
            @Parameter(description = "Identifiant unique de la race à supprimer", required = true, example = "1")
            @PathVariable Integer id) {

        Optional<Race> optionalRace = raceDao.findById(id);

        if(optionalRace.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        raceDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une race existante",
            description = """
                    Met à jour les informations d'une race existante identifée par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Race mise à jour avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune race ne correspond à cet ID"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    public ResponseEntity<Void> updateRace(
            @Parameter(description = "Identifiant unique de la race à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Race avec les nouvelles valeurs. L'ID sera écrasé par celui d l'URL.",
                    required = true
            )
            @RequestBody
            @Validated(ValidationGroupe.OnUpdate.class)
            Race raceToUpdate) {

        Optional<Race> optionalRace = raceDao.findById(id);

        if(optionalRace.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // On écrase l'id du json par celui en paramètre
        raceToUpdate.setId(id);

        raceDao.save(raceToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
