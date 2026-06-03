package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dto.InscriptionRequestDto;
import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Inscription;
import edu.mns.cda.espritcaninbackend.security.IsAdherent;
import edu.mns.cda.espritcaninbackend.security.IsAdmin;
import edu.mns.cda.espritcaninbackend.security.UtilisateurDetails;
import edu.mns.cda.espritcaninbackend.service.ChienService;
import edu.mns.cda.espritcaninbackend.service.InscriptionService;
import edu.mns.cda.espritcaninbackend.view.InscriptionView;
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
@Tag(name = "Inscription", description = """
        API de gestion des inscriptions d'un chien à une séance.
        Table associative à clé composite (chien + séance).
        Permet de récupérer, créer, modifier et supprimer des inscriptions.
        """)
@RequiredArgsConstructor
@RequestMapping("/inscription")
@CrossOrigin
public class InscriptionController {

    protected final InscriptionService inscriptionService;
    protected final ChienService chienService;

    @GetMapping("/list")
    @Operation(
            summary = "Lister toutes les inscriptions",
            description = "Retourne la liste complète des inscriptions enregistrées en base."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @JsonView(InscriptionView.class)
    @IsAdmin
    public List<Inscription> getAll() {
        return inscriptionService.findAll();
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
    @IsAdmin
    public ResponseEntity<Inscription> get(
            @Parameter(description = "Identifiant du chien", required = true, example = "1")
            @PathVariable Integer idChien,
            @Parameter(description = "Identifiant de la séance", required = true, example = "1")
            @PathVariable Integer idSeance
    ) {
        Inscription.Key key = new Inscription.Key(idChien, idSeance);
        Optional<Inscription> optionalInscription = inscriptionService.findById(key);

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
    @IsAdmin
    public ResponseEntity<Inscription> create(
            @RequestBody @Valid InscriptionRequestDto request
    ) {
        Inscription inscriptionCreee = inscriptionService.inscrire(request.chienId(), request.seanceId());

        return new ResponseEntity<>(inscriptionCreee, HttpStatus.CREATED);
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
    @IsAdmin
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant du chien", required = true, example = "1")
            @PathVariable Integer idChien,
            @Parameter(description = "Identifiant de la séance", required = true, example = "1")
            @PathVariable Integer idSeance
    ) {
        Inscription.Key key = new Inscription.Key(idChien, idSeance);

        inscriptionService.delete(key);

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
    @IsAdmin
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

        inscriptionService.update(key, inscriptionToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // Espace adhérent : mes inscriptions

    @GetMapping("/mes-inscriptions")
    @Operation(
            summary = "Lister mes inscriptions (adhérent connecté)",
            description = """
                    Retourne toutes les inscriptions des chiens de l'utilisateur connecté
                    (séances à venir et passées). Le propriétaire vient du token.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste de mes inscriptions retournée avec succès")
    })
    @IsAdherent
    @JsonView(InscriptionView.class)
    public List<Inscription> getMesInscriptions(@AuthenticationPrincipal UtilisateurDetails userDetails) {
        return inscriptionService.findByProprietaire(userDetails.getUtilisateur().getId());
    }

    @PostMapping("/mes-inscriptions")
    @Operation(
            summary = "Inscrire un de mes chiens à une séance (adhérent connecté)",
            description = """
                    Inscrit un chien de l'utilisateur connecté à une séance.
                    Le chien doit appartenir à l'adhérent (sinon 403).
                    Applique les règles métier d'inscription (âge, capacité, compétences,
                    doublon, séance annulée/passée) → 409 si une règle est violée.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscription créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Corps invalide (chien/séance manquant)"),
            @ApiResponse(responseCode = "403", description = "Ce chien n'appartient pas à l'adhérent connecté"),
            @ApiResponse(responseCode = "404", description = "Chien introuvable"),
            @ApiResponse(responseCode = "409", description = "Règle métier violée (doublon, complète, âge, compétence...)")
    })
    @IsAdherent
    @JsonView(InscriptionView.class)
    public ResponseEntity<Inscription> inscrireMonChien(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @RequestBody @Valid InscriptionRequestDto request
    ) {
        Optional<Chien> optionalChien = chienService.findById(request.chienId());
        if (optionalChien.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // le chien inscrit doit appartenir a l'user connecté, sinon 403
        if (!optionalChien.get().getUtilisateur().getId().equals(userDetails.getUtilisateur().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Inscription inscriptionCreee = inscriptionService.inscrire(request.chienId(), request.seanceId());
        return new ResponseEntity<>(inscriptionCreee, HttpStatus.CREATED);
    }

    @PatchMapping("/mes-inscriptions/{idChien}/{idSeance}/annuler")
    @Operation(
            summary = "Annuler une de mes inscriptions (adhérent connecté)",
            description = """
                    Annule (statut ANNULEE, conserve l'historique) une inscription portant
                    sur un chien de l'utilisateur connecté.
                    404 si l'inscription n'existe pas, 403 si elle ne lui appartient pas,
                    409 si la séance est déjà passée.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscription annulée avec succès"),
            @ApiResponse(responseCode = "403", description = "Cette inscription n'appartient pas à l'adhérent connecté"),
            @ApiResponse(responseCode = "404", description = "Aucune inscription ne correspond à cette clé"),
            @ApiResponse(responseCode = "409", description = "La séance est déjà passée")
    })
    @IsAdherent
    public ResponseEntity<Void> annulerMonInscription(
            @AuthenticationPrincipal UtilisateurDetails userDetails,
            @PathVariable Integer idChien,
            @PathVariable Integer idSeance
    ) {
        Inscription.Key key = new Inscription.Key(idChien, idSeance);
        Optional<Inscription> optionalInscription = inscriptionService.findById(key);
        if (optionalInscription.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // l'inscription doit porter sur un chien de l'user connecté
        if (!optionalInscription.get().getChien().getUtilisateur().getId().equals(userDetails.getUtilisateur().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        inscriptionService.annuler(key);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}