package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.InscriptionDao;
import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.exception.*;
import edu.mns.cda.espritcaninbackend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    protected final InscriptionDao inscriptionDao;
    protected final ChienDao chienDao;
    protected final SeanceDao seanceDao;

    public List<Inscription> findAll() {
        return inscriptionDao.findAll();
    }

    public Optional<Inscription> findById(Inscription.Key key) {
        return inscriptionDao.findById(key);
    }

    /**
     * Inscrit un chien à une séance après avoir validé les 5 règles métier (RG03).
     * @Transactional : opération multi-étapes (lectures + écriture) → atomicité.
     */
    @Transactional
    public Inscription insert(Inscription inscription) {
        Integer chienId = inscription.getChien().getId();
        Integer seanceId = inscription.getSeance().getId();

        // On recharge les entités complètes depuis la BDD (le client n'envoie que les id)
        Chien chien = chienDao.findById(chienId)
                .orElseThrow(() -> new ChienNotFoundException(chienId));
        Seance seance = seanceDao.findById(seanceId)
                .orElseThrow(() -> new SeanceNotFoundException(seanceId));

        TypeSeance type = seance.getTypeSeance();

        // 1. La séance ne doit pas être annulée
        if (seance.getStatut() == StatutSeance.ANNULEE) {
            throw new SeanceAnnuleeException(seanceId);
        }

        // 1b. La séance ne doit pas être déjà passée (on ne s'inscrit pas à un cours qui a eu lieu)
        if (seance.getDate().isBefore(LocalDate.now())) {
            throw new SeancePasseeException(seanceId);
        }

        // 2. Pas de doublon (unicité chien + séance)
        Inscription.Key key = new Inscription.Key(chienId, seanceId);
        if (inscriptionDao.findById(key).isPresent()) {
            throw new InscriptionDoublonException(chienId, seanceId);
        }

        // 3. Âge du chien (calculé au jour de la séance) dans les bornes du type
        int ageMois = (int) ChronoUnit.MONTHS.between(chien.getDateNaissance(), seance.getDate());
        Integer ageMin = type.getAgeMinimumMois();
        Integer ageMax = type.getAgeMaximumMois();
        if ((ageMin != null && ageMois < ageMin) || (ageMax != null && ageMois > ageMax)) {
            throw new EligibiliteAgeException(ageMois, ageMin, ageMax);
        }

        // 4. Capacité : inscriptions actives (hors annulée) < participantsMaximum
        long inscritsActifs = seance.getInscriptions() == null ? 0
                : seance.getInscriptions().stream()
                  .filter(i -> i.getStatutPresence() != StatutPresence.ANNULEE)
                  .count();
        if (inscritsActifs >= type.getParticipantsMaximum()) {
            throw new SeanceCompleteException(seanceId, type.getParticipantsMaximum());
        }

        // 5. Compétences requises (necessite) : niveau du chien >= niveau requis.
        //    Liste vide = séance libre = aucune vérif.
        if (type.getTypeSeancesCompetences() != null) {
            for (TypeSeanceCompetence requis : type.getTypeSeancesCompetences()) {
                NiveauCompetence niveauRequis = requis.getNiveauMinimumRequis();
                NiveauCompetence niveauChien = niveauDuChien(chien, requis.getCompetence().getId());
                if (niveauChien.ordinal() < niveauRequis.ordinal()) {
                    throw new EligibiliteCompetenceException(
                            requis.getCompetence().getNom(), niveauRequis, niveauChien);
                }
            }
        }

        // 6. Tout est OK → on crée l'inscription
        inscription.setId(key);
        inscription.setStatutPresence(StatutPresence.INSCRIT); // valeur initiale (corrige le NOT NULL)
        return inscriptionDao.save(inscription);
    }

    /**
     * Niveau actuel du chien sur une compétence donnée.
     * absence de ligne dans `maitriser` (chienCompetences) = DEBUTANT par défaut.
     */
    private NiveauCompetence niveauDuChien(Chien chien, Integer competenceId) {
        if (chien.getChienCompetences() == null) {
            return NiveauCompetence.DEBUTANT;
        }
        return chien.getChienCompetences().stream()
                .filter(cc -> cc.getCompetence().getId().equals(competenceId))
                .map(ChienCompetence::getNiveauActuel)
                .findFirst()
                .orElse(NiveauCompetence.DEBUTANT);
    }

    public void delete(Inscription.Key key) {
        if (inscriptionDao.findById(key).isEmpty()) {
            throw new InscriptionNotFoundException(key.getChienId(), key.getSeanceId());
        }
        inscriptionDao.deleteById(key);
    }

    public void update(Inscription.Key key, Inscription inscriptionToUpdate) {
        if (inscriptionDao.findById(key).isEmpty()) {
            throw new InscriptionNotFoundException(key.getChienId(), key.getSeanceId());
        }
        inscriptionToUpdate.setId(key);
        inscriptionDao.save(inscriptionToUpdate);
    }
}