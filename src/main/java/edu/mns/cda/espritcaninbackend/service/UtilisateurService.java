package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.InscriptionDao;
import edu.mns.cda.espritcaninbackend.dao.RoleDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.exception.UtilisateurNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Role;
import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    protected final UtilisateurDao utilisateurDao;
    protected final PasswordEncoder passwordEncoder;
    protected final RoleDao roleDao;
    protected final ChienDao chienDao;
    protected final InscriptionDao inscriptionDao;

    public List<Utilisateur> findAll() {
        return utilisateurDao.findAllOrderByNomPrenom();
    }

    public Optional<Utilisateur> findById(int id) {
        return utilisateurDao.findById(id);
    }

    /**
     * Recherche avec filtres optionnels.
     */
    public List<Utilisateur> search(String recherche, Integer roleId) {
        String rechercheNormalisee = (recherche == null || recherche.isBlank()) ? "" : recherche.trim();
        return utilisateurDao.search(rechercheNormalisee, roleId);
    }

    public void insert(Utilisateur utilisateur) {
        if (roleEstAdmin(utilisateur.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Le rôle Admin n'est pas attribuable.");
        }
        if (utilisateurDao.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet email est déjà utilisé.");
        }
        utilisateur.setId(null);
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateurDao.save(utilisateur);
    }

    /**
     * Met à jour les infos modifiables d'un utilisateur. On part de l'entité
     * existante pour ne PAS écraser les champs non éditables via cet endpoint
     * (password, dateInscription). Le password est modifiable uniquement via
     * updatePassword().
     */
    public void update(int id, Utilisateur utilisateurToUpdate) {
        Utilisateur existant = utilisateurDao.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        if (estAdmin(existant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Un compte Admin n'est pas modifiable.");
        }
        if (roleEstAdmin(utilisateurToUpdate.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Le rôle Admin n'est pas attribuable.");
        }

        existant.setNom(utilisateurToUpdate.getNom());
        existant.setPrenom(utilisateurToUpdate.getPrenom());
        existant.setEmail(utilisateurToUpdate.getEmail());
        existant.setTelephone(utilisateurToUpdate.getTelephone());
        existant.setRole(utilisateurToUpdate.getRole());

        utilisateurDao.save(existant);
    }

    /**
     * Mise à jour du profil par l'adhérent lui-même.
     * On set UNIQUEMENT nom/prénom/email/téléphone : le rôle, le password et la
     * dateInscription sont volontairement préservés depuis l'existant.
     * Sécurité : empêche un adhérent de changer son propre rôle (escalade de privilège).
     */
    public void updateProfil(int id, Utilisateur profil) {
        Utilisateur existant = utilisateurDao.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        existant.setNom(profil.getNom());
        existant.setPrenom(profil.getPrenom());
        existant.setEmail(profil.getEmail());
        existant.setTelephone(profil.getTelephone());

        utilisateurDao.save(existant);
    }

    /**
     * Met à jour uniquement le mot de passe. Validation longueur (≥ 8)
     */
    public void updatePassword(int id, String nouveauPassword) {
        if (nouveauPassword == null || nouveauPassword.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le mot de passe doit faire au moins 8 caractères."
            );
        }
        Utilisateur existant = utilisateurDao.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        existant.setPassword(passwordEncoder.encode(nouveauPassword));
        utilisateurDao.save(existant);
    }

    /**
     * Suppression d'un compte par un admin.
     * - Un compte Admin n'est pas supprimable (403).
     * - Un coach assigné à des séances est protégé (409).
     * - Un adhérent encore inscrit à des séances futures est protégé (409).
     * - Sinon : cascade (chiens + inscriptions passées) puis suppression.
     * TODO Évolution : Soft delete + anonymisation des données.
     */
    @Transactional
    public void delete(int id) {
        Utilisateur utilisateur = utilisateurDao.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        if (estAdmin(utilisateur)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Un compte Admin n'est pas supprimable.");
        }
        if (utilisateur.getSeancesCoachees() != null && !utilisateur.getSeancesCoachees().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet utilisateur est coach sur " + utilisateur.getSeancesCoachees().size() + " séance(s). Réaffectez avant.");
        }
        long futures = inscriptionDao.countInscriptionsFuturesActives(id, LocalDate.now(), StatutPresence.ANNULEE);
        if (futures > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet utilisateur a " + futures + " inscription(s) à des séances futures. Annulez-les avant.");
        }

        supprimerUtilisateurEtChiens(utilisateur);
    }

    /**
     * Suppression de SON propre compte (droit à l'effacement RGPD).
     * - Un Admin ne peut pas se supprimer (403).
     * - Un coach avec séances est protégé (409).
     * - Sinon : cascade TOTALE (chiens + toutes inscriptions, même futures).
     */
    @Transactional
    public void supprimerMonCompte(int id) {
        Utilisateur utilisateur = utilisateurDao.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        if (estAdmin(utilisateur)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Un compte Admin ne peut pas être supprimé.");
        }
        if (utilisateur.getSeancesCoachees() != null && !utilisateur.getSeancesCoachees().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vous êtes coach sur des séances. Contactez un administrateur.");
        }

        supprimerUtilisateurEtChiens(utilisateur);
    }

    /**
     * Inscription publique : Crée un utilisateur en lui forçant le role Adhrent. J'ignore volontairement tout role envoyé par le client
     * Sinon n'importe qui pourrais s'auto déclarer Coach ou Admin
     * Hash du password avec insert
     */
    public void inscriptionPublique(Utilisateur utilisateur) {
        Role adherent = roleDao.findByNom("Adherent")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Rôle 'Adherent' introuvable en base."
                ));
        utilisateur.setRole(adherent);
        insert(utilisateur); // setId(null) + hash password + save
    }

    /** Vrai si l'utilisateur a le rôle Admin. */
    private boolean estAdmin(Utilisateur utilisateur) {
        return utilisateur.getRole() != null
                && "Admin".equalsIgnoreCase(utilisateur.getRole().getNom());
    }

    /** Vrai si la référence de rôle (résolue en base) correspond au rôle Admin. */
    private boolean roleEstAdmin(Role role) {
        if (role == null || role.getId() == null) return false;
        return roleDao.findById(role.getId())
                .map(r -> "Admin".equalsIgnoreCase(r.getNom()))
                .orElse(false);
    }

    /** Suppression en cascade : les chiens d'abord (cascade inscriptions + compétences), puis l'utilisateur. */
    private void supprimerUtilisateurEtChiens(Utilisateur utilisateur) {
        if (utilisateur.getChiens() != null && !utilisateur.getChiens().isEmpty()) {
            chienDao.deleteAll(utilisateur.getChiens());
        }
        utilisateurDao.deleteById(utilisateur.getId());
    }
}
