package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dao.CompetenceDao;
import edu.mns.cda.espritcaninbackend.model.Competence;
import edu.mns.cda.espritcaninbackend.view.CompetenceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Competence", description = """
        API de gestion des competences du club esprit canin.
        Permet de récupérer, créer, modifier et supprimer des competences.
        Toutes les réponses sont au format JSON.        
        """)
@RequiredArgsConstructor
@RequestMapping("/competence")
@CrossOrigin
public class CompetenceController {

    protected final CompetenceDao competenceDao;

    @GetMapping("/list")
    @Operation(
            summary = "Lister toutes les competences",
            description = """
                    Retourne la liste complète de toutes les competences enregistrées en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucune competence n'existe
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste retournée avec succès"
            )
    })
    @JsonView(CompetenceView.class)
    public List<Competence> getAllCompetences() {
        return competenceDao.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récuperer une competence par son ID",
            description = """
                    Retourne les détails d'une competence à partir de son identifiant unique passé en paramètre de chemin (path variable).
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Competence trouvée et retournée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune competence ne correspond à cet ID"
            )
    })
    @JsonView(CompetenceView.class)
    public ResponseEntity<Competence> get(
            @Parameter(description = "Identifiant unique sur la competence", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Competence> optionalCompetence = competenceDao.findById(id);

        if (optionalCompetence.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCompetence.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle competence",
            description = """
                    Crée une nouvelle competence à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base (autoincrémenté).
                    Les champs obligatoires sont validés via @Valid.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Competence créée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    @JsonView(CompetenceView.class)
    public ResponseEntity<Competence> createCompetence(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet competence à créer. L'ID sera ignoré"
            )
            @RequestBody
            @Valid Competence competenceToInsert
    ) {

        competenceToInsert.setId(null);

        competenceDao.save(competenceToInsert);

        return new ResponseEntity<>(competenceToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une competence par son ID",
            description = """
                    Supprime définitivement la competence correspondante à l'ID passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la suppression est réussie.                    
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Competence supprimée avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune competence ne correspond à cet ID"
            )
    })
    public ResponseEntity<Competence> deleteCompetence(
            @Parameter(description = "Identifiant unique de la competence a supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Competence> optionalCompetence = competenceDao.findById(id);

        if (optionalCompetence.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        competenceDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une competence existante",
            description = """
                    Met à jour les informations d'une competence existante identifiée par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Competence mise à jour avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune competence ne correspond à cet ID"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    public ResponseEntity<Void> updateCompetence(
            @Parameter(description = "Identifiant unique de la competence à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Competence avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL",
                    required = true
            )
            @RequestBody
            @Valid
            Competence competenceToUpdate
    ) {
        Optional<Competence> optionalCompetence = competenceDao.findById(id);

        if (optionalCompetence.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        competenceToUpdate.setId(id);

        competenceDao.save(competenceToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
