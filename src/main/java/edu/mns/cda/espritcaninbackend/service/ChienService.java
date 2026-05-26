package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.exception.ChienNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Sexe;
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
        if (chienDao.findById(id).isEmpty()) {
            throw new ChienNotFoundException(id);
        }
        chienToUpdate.setId(id);
        chienDao.save(chienToUpdate);
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

        if (!chien.getInscriptions().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce chien a des inscriptions enregistrées. Suppression impossible (conservation de l'historique)."
            );
        }

        chienDao.deleteById(id);
    }
}
