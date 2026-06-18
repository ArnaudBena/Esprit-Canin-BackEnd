package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienCompetenceDao;
import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.InscriptionDao;
import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.exception.*;
import edu.mns.cda.espritcaninbackend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    protected final ChienCompetenceDao chienCompetenceDao;

    public List<Inscription> findAll() {
        return inscriptionDao.findAll();
    }

    public Optional<Inscription> findById(Inscription.Key key) {
        return inscriptionDao.findById(key);
    }

    /**
     * Inscrit un chien à une séance après avoir validé les 5 règles métier.
     * @Transactional : opération multi-étapes (lectures + écriture) → atomicité.
     */
    @Transactional
    public Inscription inscrire(Integer chienId, Integer seanceId) {
        // On charge les entités complètes depuis la BDD (le client n'envoie que les id)
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

        // 2. Doublon vs réinscription : une ligne ANNULEE ne bloque pas (on la réactivera)
        Inscription.Key key = new Inscription.Key(chienId, seanceId);
        Optional<Inscription> existante = inscriptionDao.findById(key);
        if (existante.isPresent() && existante.get().getStatutPresence() != StatutPresence.ANNULEE) {
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

        // 5. Compétences requises (necessite) : le chien doit AVOIR acquis la compétence
        //    à un niveau >= requis. Pas de ligne = non acquise = bloqué.
        //    Liste vide = séance libre = aucune vérif.
        if (type.getTypeSeancesCompetences() != null) {
            for (TypeSeanceCompetence requis : type.getTypeSeancesCompetences()) {
                NiveauCompetence niveauRequis = requis.getNiveauMinimumRequis();
                Optional<NiveauCompetence> niveauChien = niveauDuChien(chien, requis.getCompetence().getId());
                if (niveauChien.isEmpty() || niveauChien.get().ordinal() < niveauRequis.ordinal()) {
                    throw new EligibiliteCompetenceException(
                            requis.getCompetence().getNom(), niveauRequis, niveauChien.orElse(null));
                }
            }
        }

        // 6. Tout est OK → réactivation de l'inscription annulée, sinon création
        Inscription inscription = existante.orElseGet(Inscription::new);
        inscription.setId(key);
        inscription.setChien(chien);
        inscription.setSeance(seance);
        inscription.setStatutPresence(StatutPresence.INSCRIT);
        inscription.setAcquisitionValidee(false); // réinscription : on repart à zéro
        return inscriptionDao.save(inscription);
    }

    /**
     * Niveau actuel du chien sur une compétence donnée.
     * Optional vide = aucune ligne = compétence non acquise (sous le niveau débutant).
     */
    private Optional<NiveauCompetence> niveauDuChien(Chien chien, Integer competenceId) {
        if (chien.getChienCompetences() == null) {
            return Optional.empty();
        }
        return chien.getChienCompetences().stream()
                .filter(cc -> cc.getCompetence().getId().equals(competenceId))
                .map(ChienCompetence::getNiveauActuel)
                .findFirst();
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

    /**
     * Inscriptions d'un propriétaire (tous ses chiens). Espace adhérent.
     */
    public List<Inscription> findByProprietaire(int utilisateurId) {
        return inscriptionDao.findByProprietaire(utilisateurId);
    }

    /**
     * Annulation par l'adhérent : passage en ANNULEE (soft delete, conserve l'historique).
     * Refusé (409) si la séance est déjà passée.
     */
    public void annuler(Inscription.Key key) {
        Inscription inscription = inscriptionDao.findById(key)
                .orElseThrow(() -> new InscriptionNotFoundException(key.getChienId(), key.getSeanceId()));

        if (inscription.getSeance().getDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible d'annuler l'inscription : la séance est déjà passée."
            );
        }

        inscription.setStatutPresence(StatutPresence.ANNULEE);
        inscriptionDao.save(inscription);
    }

    /**
     * Appel du coach (après la séance) : marque un chien PRESENT ou ABSENT.
     * Refusé si l'inscription est ANNULEE ou si la séance n'est pas terminée.
     */
    @Transactional
    public void faireAppel(Inscription.Key key, StatutPresence statutPresence) {
        // L'appel n'accepte QUE PRESENT ou ABSENT (pas INSCRIT/ANNULEE)
        if (statutPresence != StatutPresence.PRESENT && statutPresence != StatutPresence.ABSENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'appel n'accepte que PRESENT ou ABSENT.");
        }

        Inscription inscription = inscriptionDao.findById(key)
                .orElseThrow(() -> new InscriptionNotFoundException(key.getChienId(), key.getSeanceId()));

        // Garde-fou : pas d'appel sur une inscription annulée par l'adhérent
        if (inscription.getStatutPresence() == StatutPresence.ANNULEE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inscription annulée : appel impossible.");
        }
        // La séance doit être terminée
        if (!inscription.getSeance().getTerminee()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La séance n'est pas encore terminée.");
        }

        inscription.setStatutPresence(statutPresence);
        inscriptionDao.save(inscription);
    }

    /**
     * Validation pédagogique par le coach. En bloc : valide toutes les
     * compétences que le TYPE de la séance confère. Préconditions : séance
     * terminée + chien PRESENT. Progression sans rétrogradation.
     */
    @Transactional
    public void validerAcquisition(Inscription.Key key) {
        Inscription inscription = inscriptionDao.findById(key)
                .orElseThrow(() -> new InscriptionNotFoundException(key.getChienId(), key.getSeanceId()));

        Seance seance = inscription.getSeance();
        Chien chien = inscription.getChien();

        // Précondition 1 : séance terminée
        if (!seance.getTerminee()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La séance n'est pas encore terminée.");
        }
        // Précondition 2 : on ne valide que pour un chien présent (l'appel d'abord)
        if (inscription.getStatutPresence() != StatutPresence.PRESENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Le chien doit d'abord être marqué présent (faites l'appel).");
        }

        // Pour chaque compétence CONFEREE par le type, on fait progresser le chien
        TypeSeance type = seance.getTypeSeance();
        if (type.getTypeSeancesCompetencesConferees() != null) {
            for (TypeSeanceCompetenceConferee conferee : type.getTypeSeancesCompetencesConferees()) {
                appliquerProgression(chien, conferee.getCompetence(), conferee.getNiveauConfere(), seance.getDate());
            }
        }

        // On trace la validation sur l'inscription
        inscription.setAcquisitionValidee(true);
        inscriptionDao.save(inscription);
    }

    /**
     * Monte le niveau d'un chien sur une compétence, sans jamais rétrograder.
     * Absence de ligne = DEBUTANT. date_acquisition = date de la séance.
     */
    private void appliquerProgression(Chien chien, Competence competence,
                                      NiveauCompetence niveauConfere, LocalDate dateSeance) {
        ChienCompetence.Key key = new ChienCompetence.Key(chien.getId(), competence.getId());
        Optional<ChienCompetence> existante = chienCompetenceDao.findById(key);

        // Pas de ligne = non acquise -> on crée toujours. Sinon on ne progresse que si
        // le niveau conféré est strictement supérieur (pas de rétrogradation).
        if (existante.isPresent()
                && niveauConfere.ordinal() <= existante.get().getNiveauActuel().ordinal()) {
            return;
        }

        ChienCompetence cc = existante.orElseGet(ChienCompetence::new);
        cc.setId(key);
        cc.setChien(chien);
        cc.setCompetence(competence);
        cc.setNiveauActuel(niveauConfere);
        cc.setDateAcquisition(dateSeance);
        chienCompetenceDao.save(cc);
    }

    public void commenter(Inscription.Key key, String commentaire) {
        Inscription inscription = inscriptionDao.findById(key)
                .orElseThrow(() -> new InscriptionNotFoundException(key.getChienId(), key.getSeanceId()));
        inscription.setCommentaireCoach(commentaire);
        inscriptionDao.save(inscription);
    }
}