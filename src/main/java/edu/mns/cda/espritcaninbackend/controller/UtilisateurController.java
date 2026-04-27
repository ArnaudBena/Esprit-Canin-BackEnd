package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import edu.mns.cda.espritcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.espritcaninbackend.view.UtilisateurView;
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
@Tag(name = "Utilisateur", description = """
        API de gestion des utilisateurs du club esprit canin.
        Permet de récupérer, créer, modifier et supprimer des utilisateurs.
        Toutes les réponses sont au format JSON.        
        """)
@RequiredArgsConstructor
@RequestMapping("/utilisateur")
@CrossOrigin
public class UtilisateurController {

    protected final UtilisateurDao utilisateurDao;

    @GetMapping("/list")
    @Operation(
            summary = "Lister tous les utilisateurs",
            description = """
                    Retourne la liste complète de tous les utilisateurs enregistrés en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucun utilisateur n'existe
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste retournée avec succès"
            )
    })
    @JsonView(UtilisateurView.class)
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurDao.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récuperer un utilisateur par son ID",
            description = """
                    Retourne les détails d'un utilisateur à partir de son identifiant unique passé en paramètre de chemin (path variable).
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé et retourné avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun utilisateur ne correspond à cet ID"
            )
    })
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> get(
            @Parameter(description = "Identifiant unique de l'utilisateur", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Utilisateur> optionalUtilisateur = utilisateurDao.findById(id);

        if (optionalUtilisateur.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalUtilisateur.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = """
                    Crée un nouvel utilisateur à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base (autoincrémenté).
                    Les champs obligatoires sont validés via @Validated (groupe OnCreate).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur crée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> createUtilisateur(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet utilisateur à créer. L'ID sera ignoré"
            )
            @RequestBody
            @Validated(ValidationGroupe.OnCreate.class) Utilisateur utilisateurToInsert
    ) {

        utilisateurToInsert.setId(null);

        utilisateurDao.save(utilisateurToInsert);

        return new ResponseEntity<>(utilisateurToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un utilisateur par son ID",
            description = """
                    Supprime définitivement l'utilisateur correspondant à l'ID passé en path variable.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la suppression est réussie.                    
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Utilisateur supprimé avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun utilisateur ne correspond à cet ID"
            )
    })
    public ResponseEntity<Utilisateur> deleteUtilisateur(
            @Parameter(description = "Identifiant unique de l'utilisateur a supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Utilisateur> optionalUtilisateur = utilisateurDao.findById(id);

        if (optionalUtilisateur.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        utilisateurDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un utilisateur existant",
            description = """
                    Met à jour les informations d'un utilisateur existant identifié par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Utilisateur mis à jour avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun utilisateur ne correspond à cet ID"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    public ResponseEntity<Void> updateUtilisateur(
            @Parameter(description = "Identifiant unique de l'utilisateur à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Utilisateur avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL",
                    required = true
            )
            @RequestBody
            @Validated(ValidationGroupe.OnUpdate.class)
            Utilisateur utilisateurToUpdate
    ) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurDao.findById(id);

        if (optionalUtilisateur.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        utilisateurToUpdate.setId(id);

        utilisateurDao.save(utilisateurToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
