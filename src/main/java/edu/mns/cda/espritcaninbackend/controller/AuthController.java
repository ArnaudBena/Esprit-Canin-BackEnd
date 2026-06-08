package edu.mns.cda.espritcaninbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import edu.mns.cda.espritcaninbackend.security.UtilisateurDetails;
import edu.mns.cda.espritcaninbackend.service.UtilisateurService;
import edu.mns.cda.espritcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.espritcaninbackend.view.UtilisateurView;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Inscription publique et connexion (génération du JWT")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    @Value("${jwt.secret}")
    protected String SECRET; // TODO : Externaliser dans app.properties (doit matcher le JwtFilter) > fait a check ?

    protected final UtilisateurService utilisateurService;
    protected final AuthenticationProvider authenticationProvider;

    @PostMapping("/inscription")
    @Operation(summary = "Inscription publique", description = "Crée un compte adhérent. Le rôle est forcé à 'Adherent' côté serveur.")
    @JsonView(UtilisateurView.class)
    public ResponseEntity<Utilisateur> inscription(
            @RequestBody @Validated(ValidationGroupe.OnInscription.class) Utilisateur utilisateurAInscrire
    ) {
        utilisateurService.inscriptionPublique(utilisateurAInscrire);
        return new ResponseEntity<>(utilisateurAInscrire, HttpStatus.CREATED);
    }

    @PostMapping("/connexion")
    @Operation(summary = "Connexion", description = "Vérifie email/mot de passe et retourne un JWT signé en cas de succès.")
    public ResponseEntity<String> connexion(@RequestBody Utilisateur identifiants) {
        try {
            // Délègue la vérif email/mot de pass au DaoAuthenticationProvider (BCrypt sous le capot)
            UtilisateurDetails utilisateurDetails = (UtilisateurDetails) authenticationProvider
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            identifiants.getEmail(),
                            identifiants.getPassword()))
                    .getPrincipal();

            // Construit le JWT
            String role = utilisateurDetails.getUtilisateur().getRole().getNom().toUpperCase();

            String jwt = Jwts.builder()
                    .setSubject(identifiants.getEmail())
                    .addClaims(Map.of("role", role))
                    .signWith(SignatureAlgorithm.HS256, SECRET)
                    .compact();

            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e) {
            // Mauvais email ou mot de passe = 401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
