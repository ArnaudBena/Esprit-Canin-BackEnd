package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.espritcaninbackend.view.SeanceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    protected final SeanceDao seanceDao;

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
    @JsonView(SeanceView.class)
    public List<Seance> getAllSeances() {
        return seanceDao.findAll();
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
    @JsonView(SeanceView.class)
    public ResponseEntity<Seance> get(
            @Parameter(description = "Identifiant unique de la séance", required = true, example = "1")
            @PathVariable Integer id
    ) {
        Optional<Seance> optionalSeance = seanceDao.findById(id);

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
    @JsonView(SeanceView.class)
    public ResponseEntity<Seance> createSeance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Seance à créer. L'ID sera ignoré."
            )
            @RequestBody
            @Validated(ValidationGroupe.OnCreate.class) Seance seanceToInsert
    ) {
        seanceToInsert.setId(null);

        seanceDao.save(seanceToInsert);

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
    public ResponseEntity<Seance> deleteSeance(
            @Parameter(description = "Identifiant unique de la séance à supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {
        Optional<Seance> optionalSeance = seanceDao.findById(id);

        if (optionalSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        seanceDao.deleteById(id);

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
        Optional<Seance> optionalSeance = seanceDao.findById(id);

        if (optionalSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        seanceToUpdate.setId(id);

        seanceDao.save(seanceToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}