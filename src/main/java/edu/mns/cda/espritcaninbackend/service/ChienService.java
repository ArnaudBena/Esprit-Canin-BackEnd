package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.exception.ChienNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Sexe;
import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChienService {

    protected final ChienDao chienDao;

    public List<Chien> findAll() {
        return chienDao.findAllOrderByNom();
    }

    public Optional<Chien> findById(int id) {
        return chienDao.findById(id);
    }

    /**
     * Liste les chiens d'un propriétaire (espace adhérent : "mes chiens").
     */
    public List<Chien> findByProprietaire(int utilisateurId) {
        return chienDao.findByProprietaire(utilisateurId);
    }

    /**
     * Recherche avec filtre optionnels
     */
    public List<Chien> search(String recherche, Sexe sexe) {
        String rechercheNormalisee = (recherche == null || recherche.isBlank()) ? "" : recherche.trim();
        boolean filtrerSexe = (sexe != null);
        Sexe sexeEffectif = filtrerSexe ? sexe : Sexe.MALE; // valeur ignorée si filtrerSexe = false
        return chienDao.search(rechercheNormalisee, sexeEffectif, filtrerSexe);
    }

    public void insert(Chien chien) {
        chien.setId(null);
        chienDao.save(chien);
    }

    public void update(int id, Chien chienToUpdate) {
        Chien existant = chienDao.findById(id)
                .orElseThrow(() -> new ChienNotFoundException(id));

        // Mise à jour PARTIELLE : on recopie uniquement les champs éditables sur
        // l'entité existante. On ne touche jamais aux collections (inscriptions,
        // chienCompetences) : un save() de l'entité entière déclencherait le
        // orphanRemoval et supprimerait l'historique et les compétences du chien.
        existant.setNom(chienToUpdate.getNom());
        existant.setPoids(chienToUpdate.getPoids());
        existant.setTaille(chienToUpdate.getTaille());
        existant.setSexe(chienToUpdate.getSexe());
        existant.setDateNaissance(chienToUpdate.getDateNaissance());
        existant.setNumeroPuce(chienToUpdate.getNumeroPuce());
        existant.setRace(chienToUpdate.getRace());

        chienDao.save(existant);
    }

    /**
     * Règle métier : on refuse la suppression d'un chien qui a au moins
     * une inscription (passée ou à venir). Choix volontaire pour préserver
     * l'historique des séances.
     *
     * TODO : Évolution possible : soft delete (deletedAt) + anonymisation si un utilisateur exerce son droit à l'effacement RGPD.
     */
    public void delete(int id) {
        Chien chien = chienDao.findById(id)
                .orElseThrow(() -> new ChienNotFoundException(id));

        // On bloque seulement s'il existe une inscription NON annulée (participation réelle
        // ou engagement à venir). Les inscriptions annulées ne comptent pas : le chien n'a
        // jamais participé, et elles partent en cascade avec lui.
        boolean aInscriptionActive = chien.getInscriptions().stream()
                .anyMatch(i -> i.getStatutPresence() != StatutPresence.ANNULEE);
        if (aInscriptionActive) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce chien a participé ou est inscrit à des séances. Suppression impossible (conservation de l'historique)."
            );
        }

        chienDao.deleteById(id);
    }
}
