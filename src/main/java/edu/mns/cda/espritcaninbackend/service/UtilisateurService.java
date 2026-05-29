package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.exception.UtilisateurNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    protected final UtilisateurDao utilisateurDao;
    protected final PasswordEncoder passwordEncoder;

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

        existant.setNom(utilisateurToUpdate.getNom());
        existant.setPrenom(utilisateurToUpdate.getPrenom());
        existant.setEmail(utilisateurToUpdate.getEmail());
        existant.setTelephone(utilisateurToUpdate.getTelephone());
        existant.setRole(utilisateurToUpdate.getRole());

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
     * Règle métier MVP : on refuse la suppression d'un utilisateur qui
     * a encore des chiens enregistrés OU qui est coach d'au moins une
     * séance (passée ou future).
     * TODO Évolution possible : orphelinage (chien.utilisateur = null + anonymisation)
     */
    public void delete(int id) {
        Utilisateur utilisateur = utilisateurDao.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        if (utilisateur.getChiens() != null && !utilisateur.getChiens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet utilisateur a encore " + utilisateur.getChiens().size() + " chien(s) enregistré(s). Supprimez-les avant."
            );
        }

        if (utilisateur.getSeancesCoachees() != null && !utilisateur.getSeancesCoachees().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet utilisateur est coach sur " + utilisateur.getSeancesCoachees().size() + " séance(s). Réaffectez avant."
            );
        }

        utilisateurDao.deleteById(id);
    }
}
