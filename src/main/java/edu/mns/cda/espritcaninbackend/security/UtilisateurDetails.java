package edu.mns.cda.espritcaninbackend.security;

import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter entre mon entité JPA Utilisateur et le modèle UserDetails
 * attendu par Spring Security. Permet de garder mon entité métier
 * indépendante de la lib de sécu.
 *
 * TODO : ajouter un statut de compte (bloqué, désactivé) plus tard.
 */
@AllArgsConstructor
@Getter
public class UtilisateurDetails implements UserDetails {

    protected Utilisateur utilisateur;

    /**
     * Convertit le rôle de l'utilisateur en GrantedAuthority Spring Security.
     * Convention : préfixe "ROLE_" + nom du rôle EN MAJUSCULES.
     * Ex : Role.nom = "Adherent" → authority = "ROLE_ADHERENT"
     * Cohérent avec @PreAuthorize("hasRole('ADHERENT')") qui rajoute ROLE_ automatiquement.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().getNom().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return utilisateur.getPassword();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }
}
