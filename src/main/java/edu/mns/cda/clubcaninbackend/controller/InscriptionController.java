package edu.mns.cda.clubcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.dao.InscriptionDao;
import edu.mns.cda.clubcaninbackend.model.Inscription;
import edu.mns.cda.clubcaninbackend.view.InscriptionView;
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
@Tag(name = "Inscription", description = """
        API de gestion des inscriptions d'un chien à une séance.
        Table associative à clé composite (chien + séance).
        Permet de récupérer, créer, modifier et supprimer des inscriptions.
        """)
@RequiredArgsConstructor
@RequestMapping("/inscription")
@CrossOrigin
public class InscriptionController {

    protected final InscriptionDao inscriptionDao;

    @GetMapping("/list")
    @Operation(
            summary = "Lister toutes les inscriptions",
            description = "Retourne la liste complète des inscriptions enregistrées en base."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @JsonView(InscriptionView.class)
    public List<Inscription> getAll() {
        return inscriptionDao.findAll();
    }

    @GetMapping("/{idChien}/{idSeance}")
    @Operation(
            summary = "Récupérer une inscription par sa clé composite",
            description = """
                    Retourne les détails d'une inscription à partir des identifiants
                    du chien et de la séance.
                    Retourne une erreur 404 si l'association n'existe pas en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription trouvée et retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune inscription ne correspond à cette clé")
    })
    @JsonView(InscriptionView.class)
    public ResponseEntity<Inscription> get(
            @Parameter(description = "Identifiant du chien", required = true, example = "1")
            @PathVariable Integer idChien,
            @Parameter(description = "Identifiant de la séance", required = true, example = "1")
            @PathVariable Integer idSeance
    ) {
        Inscription.Key key = new Inscription.Key(idChien, idSeance);
        Optional<Inscription> optionalInscription = inscriptionDao.findById(key);

        if (optionalInscription.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalInscription.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle inscription",
            description = """
                    Crée une nouvelle inscription (chien + séance) à partir du corps de la requête.
                    Retourne une erreur 409 si l'association existe déjà.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscription créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants"),
            @ApiResponse(responseCode = "409", description = "Cette inscription existe déjà")
    })
    @JsonView(InscriptionView.class)
    public ResponseEntity<Inscription> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Inscription à créer. La clé est déduite des id du chien et de la séance."
            )
            @RequestBody
            @Valid Inscription inscriptionToInsert
    ) {
        Inscription.Key key = new Inscription.Key(
                inscriptionToInsert.getChien().getId(),
                inscriptionToInsert.getSeance().getId()
        );

        Optional<Inscription> optionalInscription = inscriptionDao.findById(key);

        if (optionalInscription.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        inscriptionToInsert.setId(key);

        inscriptionDao.save(inscriptionToInsert);

        return new ResponseEntity<>(inscriptionToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{idChien}/{idSeance}")
    @Operation(
            summary = "Supprimer une inscription par sa clé composite",
            description = """
                    Supprime définitivement l'inscription identifiée par (idChien, idSeance).
                    Retourne une erreur 404 si l'association n'existe pas.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscription supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune inscription ne correspond à cette clé")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant du chien", required = true, example = "1")
            @PathVariable Integer idChien,
            @Parameter(description = "Identifiant de la séance", required = true, example = "1")
            @PathVariable Integer idSeance
    ) {
        Inscription.Key key = new Inscription.Key(idChien, idSeance);
        Optional<Inscription> optionalInscription = inscriptionDao.findById(key);

        if (optionalInscription.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        inscriptionDao.deleteById(key);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{idChien}/{idSeance}")
    @Operation(
            summary = "Mettre à jour une inscription existante",
            description = """
                    Met à jour une inscription identifiée par (idChien, idSeance).
                    La clé du corps JSON est écrasée par celle de l'URL.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscription mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune inscription ne correspond à cette clé"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide")
    })
    public ResponseEntity<Void> update(
            @PathVariable Integer idChien,
            @PathVariable Integer idSeance,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Inscription avec les nouvelles valeurs. La clé sera écrasée.",
                    required = true
            )
            @RequestBody
            @Valid Inscription inscriptionToUpdate
    ) {
        Inscription.Key key = new Inscription.Key(idChien, idSeance);
        Optional<Inscription> optionalInscription = inscriptionDao.findById(key);

        if (optionalInscription.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        inscriptionToUpdate.setId(key);

        inscriptionDao.save(inscriptionToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}