package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import edu.mns.cda.espritcaninbackend.security.IsAdmin;
import edu.mns.cda.espritcaninbackend.security.IsConnecte;
import edu.mns.cda.espritcaninbackend.security.UtilisateurDetails;
import edu.mns.cda.espritcaninbackend.service.UtilisateurService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    protected final UtilisateurService utilisateurService;

    @GetMapping("/list")
    @IsAdmin
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
        return utilisateurService.findAll();
    }

    @GetMapping("/search")
    @IsAdmin
    @Operation(
            summary = "Rechercher des utilisateurs avec filtres",
            description = """
                    Retourne la liste filtrée des utilisateurs selon 2 filtres optionnels (maquette ecran-17) :
                    recherche libre dans nom/prénom/email + rôle.
                    Tri imposé serveur : nom ASC, prénom ASC.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste filtrée retournée avec succès")
    })
    @JsonView(UtilisateurView.class)
    public List<Utilisateur> searchUtilisateurs(
            @Parameter(description = "Texte libre cherché dans nom, prénom et email")
            @RequestParam(required = false) String recherche,
            @Parameter(description = "ID du rôle (Adherent, Coach, Admin)")
            @RequestParam(required = false) Integer roleId
    ) {
        return utilisateurService.search(recherche, roleId);
    }

    @GetMapping("/{id}")
    @IsAdmin
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
        Optional<Utilisateur> optionalUtilisateur = utilisateurService.findById(id);

        if (optionalUtilisateur.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalUtilisateur.get(), HttpStatus.OK);
    }

    @PostMapping
    @IsAdmin
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email déjà utilisé"
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
        utilisateurService.insert(utilisateurToInsert);

        return new ResponseEntity<>(utilisateurToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    @Operation(
            summary = "Supprimer un utilisateur par son ID",
            description = """
                    Supprime définitivement l'utilisateur correspondant à l'ID passé en path variable.
                    Refusé (409 Conflict) si l'utilisateur a encore des chiens enregistrés
                    ou s'il est coach d'au moins une séance.
                    Retourne 404 si l'ID est introuvable, 204 si la suppression est réussie.
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "L'utilisateur a encore des chiens ou des séances coachées"
            )
    })
    public ResponseEntity<Void> deleteUtilisateur(
            @Parameter(description = "Identifiant unique de l'utilisateur a supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {
        utilisateurService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @IsAdmin
    @Operation(
            summary = "Mettre à jour un utilisateur existant",
            description = """
                    Met à jour les informations d'un utilisateur existant identifié par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    Le password n'est pas modifiable via cet endpoint (utiliser PATCH /utilisateur/{id}/password).
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
            ),
            @ApiResponse(
                    responseCode = "409", description = "Email déjà utilisé"
            )
    })
    public ResponseEntity<Void> updateUtilisateur(
            @Parameter(description = "Identifiant unique de l'utilisateur à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Utilisateur avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL. Le password n'est pas modifiable via cet endpoint, utiliser PATCH /utilisateur/{id}/password.",
                    required = true
            )
            @RequestBody
            @Validated(ValidationGroupe.OnUpdate.class)
            Utilisateur utilisateurToUpdate
    ) {
        utilisateurService.update(id, utilisateurToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/password")
    @IsAdmin
    @Operation(
            summary = "Modifier le mot de passe d'un utilisateur",
            description = """
                    Met à jour uniquement le mot de passe de l'utilisateur identifié par son ID.
                    Le nouveau mot de passe doit faire au moins 8 caractères.
                    Retourne une erreur 404 si l'ID est introuvable.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Mot de passe mis à jour avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun utilisateur ne correspond à cet ID"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Mot de passe trop court (min 8 caractères) ou manquant"
            )
    })
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Identifiant unique de l'utilisateur", required = true, example = "1")
            @PathVariable Integer id,
            @RequestBody PasswordUpdateRequest body
    ) {
        utilisateurService.updatePassword(id, body == null ? null : body.password());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ----- Espace adhérent  -----

    @GetMapping("/profil")
    @IsConnecte
    @Operation(
            summary = "Récupérer mon profil",
            description = """
                    Retourne les informations de l'utilisateur actuellement connecté (déduit du JWT).
                    Aucun paramètre : l'identité vient du token.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil retourné avec succès")
    })
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> getMonProfil(
            @AuthenticationPrincipal UtilisateurDetails utilisateurDetails
    ) {
        // On recharge depuis la BDD (dans la session de la requête) pour éviter une
        // LazyInitializationException : l'entité du principal vient du JwtFilter (détachée).
        Optional<Utilisateur> optionalUtilisateur =
                utilisateurService.findById(utilisateurDetails.getUtilisateur().getId());

        if (optionalUtilisateur.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalUtilisateur.get(), HttpStatus.OK);
    }

    @PutMapping("/profil")
    @IsConnecte
    @Operation(
            summary = "Mettre à jour mon profil",
            description = """
                    Met à jour les informations de l'utilisateur connecté (nom, prénom, email, téléphone).
                    Le rôle et le mot de passe ne sont PAS modifiables via cet endpoint (sécurité).
                    Le mot de passe se change via PATCH /utilisateur/profil/password.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profil mis à jour avec succès, aucun contenu retourné"),
            @ApiResponse(responseCode = "400", description = "Corps de la requête invalide ou champs manquants"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<Void> updateMonProfil(
            @AuthenticationPrincipal UtilisateurDetails utilisateurDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles valeurs du profil : nom, prénom, email, téléphone."
            )
            @RequestBody
            @Validated(ValidationGroupe.OnProfilUpdate.class)
            Utilisateur profil
    ) {
        utilisateurService.updateProfil(utilisateurDetails.getUtilisateur().getId(), profil);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/profil/password")
    @IsConnecte
    @Operation(
            summary = "Changer mon mot de passe",
            description = """
                    Met à jour le mot de passe de l'utilisateur connecté.
                    Exige l'ancien mot de passe (vérifié), et un nouveau d'au moins 8 caractères.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mot de passe mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect, ou nouveau trop court (min 8 caractères)")
    })
    public ResponseEntity<Void> updateMonPassword(
            @AuthenticationPrincipal UtilisateurDetails utilisateurDetails,
            @RequestBody ChangePasswordRequest body
    ) {
        utilisateurService.changeMonPassword(
                utilisateurDetails.getUtilisateur().getId(),
                body == null ? null : body.ancienPassword(),
                body == null ? null : body.nouveauPassword()
        );

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public record PasswordUpdateRequest(String password) {}

    public record ChangePasswordRequest(String ancienPassword, String nouveauPassword) {}

    @DeleteMapping("/profil")
    @IsConnecte
    @Operation(summary = "Supprimer mon compte (droit à l'effacement RGPD)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Compte supprimé"),
            @ApiResponse(responseCode = "403", description = "Compte Admin non supprimable."),
            @ApiResponse(responseCode = "409", description = "Coach encore assigné à des séances")
    })
    public ResponseEntity<Void> supprimerMonCompte(
            @AuthenticationPrincipal UtilisateurDetails utilisateurDetails
    ) {
        utilisateurService.supprimerMonCompte(utilisateurDetails.getUtilisateur().getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
