package edu.mns.cda.clubcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.dao.RoleDao;
import edu.mns.cda.clubcaninbackend.model.Role;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.RoleView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Role", description = """
        API de gestion des roles du club canin.
        Permet de récupérer, créer, modifier et supprimer des roles.
        Toutes les réponses sont au format JSON.        
        """)
@RequiredArgsConstructor
@RequestMapping("/role")
@CrossOrigin
public class RoleController {

    protected final RoleDao roleDao;

    @GetMapping("/list")
    @Operation(
            summary = "Lister tous les roles",
            description = """
                    Retourne la liste complète de tous les roles enregistrés en base.
                    Aucun paramètre requis.
                    Retourne un tableau vide si aucun role n'existe
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste retournée avec succès"
            )
    })
    @JsonView(RoleView.class)
    public List<Role> getAllRoles() {
        return roleDao.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récuperer un role par son ID",
            description = """
                    Retourne les détails d'un role à partir de son identifiant unique passé en paramètre de chemin (path variable).
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role trouvé et retourné avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun role ne correspond à cet ID"
            )
    })
    @JsonView(RoleView.class)
    public ResponseEntity<Role> get(
            @Parameter(description = "Identifiant unique sur le role", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Role> optionalRole = roleDao.findById(id);

        if (optionalRole.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalRole.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Créer un nouveau role",
            description = """
                    Crée un nouveau role à partir du corps de la requête au format JSON.
                    L'ID fourni dans le corps est ignoré : il sera généré automatiquement par la base (autoincrémenté).
                    Les champs obligatoire sont validés via @Valid.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Role crée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    @JsonView(RoleView.class)
    public ResponseEntity<Role> createRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet role à créer. L'ID sera ignoré"
            )
            @RequestBody
            @Valid Role roleToInsert
    ) {

        roleToInsert.setId(null);

        roleDao.save(roleToInsert);

        return new ResponseEntity<>(roleToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un role par son ID",
            description = """
                    Supprime définitevement le role correspondant à l'ID passé en path variable.
                    Retourne une erreur 404 s i l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la suppression est réussie.                    
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Role supprimé avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun role ne correspond à cet ID"
            )
    })
    public ResponseEntity<Role> deleteRole(
            @Parameter(description = "Identifiant unique du role a supprimer", required = true, example = "1")
            @PathVariable Integer id
    ) {

        Optional<Role> optionalRole = roleDao.findById(id);

        if (optionalRole.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        roleDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un role existant",
            description = """
                    Met à jour les informations d'un role existant identifée par son ID.
                    L'ID du corps JSON est écrasé par celui de l'URL pour éviter toute incohérence.
                    Retourne une erreur 404 si l'ID est introuvable en base.
                    Retourne un statut 204 sans corps de réponse si la mise à jour est réussie.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Role mis à jour avec succès, aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun role ne correspond à cet ID"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corps de la requête invalide ou champs manquants"
            )
    })
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "Identifiant unique du role à mettre à jour", required = true, example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objet Role avec les nouvelles valeurs. L'ID sera écrasé par celui de l'URL",
                    required = true
            )
            @RequestBody
            @Validated(ValidationGroupe.OnUpdate.class)
            Role roleToUpdate
    ) {
        Optional<Role> optionalRole = roleDao.findById(id);

        if (optionalRole.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        roleToUpdate.setId(id);

        roleDao.save(roleToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
