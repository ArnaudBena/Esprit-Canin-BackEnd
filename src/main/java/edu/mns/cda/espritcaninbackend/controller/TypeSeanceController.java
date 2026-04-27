package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dao.TypeSeanceDao;
import edu.mns.cda.espritcaninbackend.model.TypeSeance;
import edu.mns.cda.espritcaninbackend.view.TypeSeanceView;
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
@Tag(name = "TypeSeance", description = """
        API de gestion des types de séances du club esprit canin.
        Permet de récupérer, créer, modifier et supprimer des types de séances.
        Toutes les réponses sont au format JSON.
        """)
@RequiredArgsConstructor
@RequestMapping("/typeSeance")
@CrossOrigin
public class TypeSeanceController {

    protected final TypeSeanceDao typeSeanceDao;

    @GetMapping("/list")
    @Operation(
            summary = "Lister tous les types de séances",
            description = """
                    Retourne la liste complète de tous les types de séances enregistrés en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucun type de séance n'existe.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @JsonView(TypeSeanceView.class)
    public List<TypeSeance> getAllTypesSeance() {
        return typeSeanceDao.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un type de séance par son ID",
            description = """
                    Retourne les détails d'un type de séance à partir de son identifiant unique passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Type de séance trouvé et retourné avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucun type de séance ne correspond à cet ID")
    })
    @JsonView(TypeSeanceView.class)
    public ResponseEntity<TypeSeance> get(
            @Parameter(description = "Identifiant unique du type de séance", required = true, example = "1")
            @PathVariable Integer id
    ) {
        Optional<TypeSeance> optionalTypeSeance = typeSeanceDao.findById(id);

        if (optionalTypeSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalTypeSeance.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer un nouveau type de séance",
            description = """
                    Crée un nouveau type de séance à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base.
                    Les champs obligatoires sont validés via @Valid.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Type de séance créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    @JsonView(TypeSeanceView.class)
    public ResponseEntity<TypeSeance> createTypeSeance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet TypeSeance à créer. L'ID sera ignoré."
            )
            @RequestBody
            @Valid TypeSeance typeSeanceToInsert
    ) {
        typeSeanceToInsert.setId(null);

        typeSeanceDao.save(typeSeanceToInsert);

        return new ResponseEntity<>(typeSeanceToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un type de séance par son ID",
            description = """
                    Supprime définitivement le type de séance correspondant à l'ID passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la suppression est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Type de séance supprimé avec succès, aucun contenu retourné"),
            @ApiResponse(responseCode = "404", description = "Aucun type de séance ne correspond à cet ID")
    })
    public ResponseEntity<TypeSeance> deleteTypeSeance(
            @Parameter(description = "Identifiant unique du type de séance à supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {
        Optional<TypeSeance> optionalTypeSeance = typeSeanceDao.findById(id);

        if (optionalTypeSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        typeSeanceDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un type de séance existant",
            description = """
                    Met à jour les informations d'un type de séance existant identifié par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Type de séance mis à jour avec succès, aucun contenu retourné"),
            @ApiResponse(responseCode = "404", description = "Aucun type de séance ne correspond à cet ID"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants")
    })
    public ResponseEntity<Void> updateTypeSeance(
            @Parameter(description = "Identifiant unique du type de séance à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet TypeSeance avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL.",
                    required = true
            )
            @RequestBody
            @Valid
            TypeSeance typeSeanceToUpdate
    ) {
        Optional<TypeSeance> optionalTypeSeance = typeSeanceDao.findById(id);

        if (optionalTypeSeance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        typeSeanceToUpdate.setId(id);

        typeSeanceDao.save(typeSeanceToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}