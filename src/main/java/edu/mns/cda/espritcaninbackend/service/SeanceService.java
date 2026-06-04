package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.dao.TypeSeanceDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.dto.SeanceCatalogueDto;
import edu.mns.cda.espritcaninbackend.exception.SeanceNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import edu.mns.cda.espritcaninbackend.model.StatutSeance;
import edu.mns.cda.espritcaninbackend.model.TypeSeance;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeanceService {

    protected final SeanceDao seanceDao;
    protected final UtilisateurDao utilisateurDao;
    protected final TypeSeanceDao typeSeanceDao;

    public Optional<Seance> findById(int id) {
        return seanceDao.findById(id);
    }

    public List<Seance> findAll() {
        return seanceDao.findAllOrderByDateHeure();
    }

    /**
     * Recherche des séances pour l'admin avec 3 filtres.
     */
    public List<Seance> search(Integer typeSeanceId,
                               LocalDate date,
                               Integer coachId) {
        boolean filtrerDate = (date != null);
        LocalDate dateEffective = filtrerDate ? date : LocalDate.now(); // valeur bidon non utilisée si filtrerDate=false
        return seanceDao.search(typeSeanceId, dateEffective, filtrerDate, coachId);
    }

    /**
     * Catalogue adhérent (écran 07) : séances à venir + ACTIVE, filtrées côté serveur.
     * @param disponible NULL = toutes, TRUE = avec places, FALSE = complètes
     */
    public List<SeanceCatalogueDto> catalogue(Integer typeSeanceId, LocalDate date, Boolean disponible) {
        boolean filtrerDate = (date != null);
        LocalDate dateEffective = filtrerDate ? date : LocalDate.now(); // valeur ignorée si filtrerDate=false
        return seanceDao.catalogue(LocalDate.now(), StatutSeance.ACTIVE, StatutPresence.ANNULEE,
                typeSeanceId, dateEffective, filtrerDate, disponible);
    }

    /**
     * Détail catalogue d'une séance (écran 08).
     */
    public Optional<SeanceCatalogueDto> catalogueDetail(int id) {
        return seanceDao.catalogueById(id, StatutPresence.ANNULEE);
    }

    public void insert(Seance seance) {
        seance.setId(null);
        if (seance.getStatut() == null) {
            seance.setStatut(StatutSeance.ACTIVE);
        }
        validerCoach(seance);
        validerDuree(seance);
        validerChevauchement(seance);
        seanceDao.save(seance);
    }

    public void delete(int id) {
        Optional<Seance> optionalSeance = seanceDao.findById(id);
        if (optionalSeance.isEmpty()) {
            throw new SeanceNotFoundException(id);
        }

        Seance seance = optionalSeance.get();
        if (seance.getInscriptions() != null && !seance.getInscriptions().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette séance a des inscriptions. Annulez-la avant de la supprimer"
            );
        }

        seanceDao.deleteById(id);
    }

    public void update(int id, Seance seanceToUpdate) {
        Optional<Seance> optionalSeance = seanceDao.findById(id);

        if(optionalSeance.isEmpty()) {
            throw new SeanceNotFoundException(id);
        }

        seanceToUpdate.setId(id);
        validerCoach(seanceToUpdate);
        validerDuree(seanceToUpdate);
        validerChevauchement(seanceToUpdate);
        seanceDao.save(seanceToUpdate);
    }

    private void validerDuree(Seance seance) {
        if (seance.getTypeSeance() == null || seance.getTypeSeance().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le type de séance est obligatoire"
            );
        }

        TypeSeance typeSeance = typeSeanceDao.findById(seance.getTypeSeance().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Type de séance introuvable (id: " + seance.getTypeSeance().getId() + ")"

                ));

        int duree = seance.getDureeMinutes();
        int min = typeSeance.getDureeMinimale();
        int max = typeSeance.getDureeMaximale();

        if (duree < min) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La durée ne peut pas être inférieure à " + min + " minutes"
            );
        }

        if (duree > max) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La durée ne peut pas depasser " + max + " minutes"
            );
        }
    }

    private void validerCoach(Seance seance) {
        if (seance.getCoach() == null || seance.getCoach().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le coach est obligatoire"
            );
        }

        Optional<Utilisateur> coachOptional = utilisateurDao.findById(seance.getCoach().getId());
        if (coachOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Coach introuvable (id : " + seance.getCoach().getId() + ")"
            );
        }

        Utilisateur coach = coachOptional.get();
        if (coach.getRole() == null || !"Coach".equalsIgnoreCase(coach.getRole().getNom())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'utilisateur assigné doit avoir le rôle Coach"
            );
        }
    }

    private void validerChevauchement(Seance seance) {
        List<Seance> seancesDuJour = seanceDao.findByCoachAndDate(seance.getCoach(), seance.getDate());

        LocalTime debutA = seance.getHeureDebut();
        LocalTime finA = debutA.plusMinutes(seance.getDureeMinutes());

        for (Seance autre : seancesDuJour) {
            // On ignore la séance qu'on est en train de modifier sinon elle se chauverait elle même
            if (autre.getId().equals(seance.getId())) continue;

            // On ignore les séances annulées car elle n'ont plus lieu
            if (autre.getStatut() == StatutSeance.ANNULEE) continue;

            LocalTime debutB = autre.getHeureDebut();
            LocalTime finB =  debutB.plusMinutes(autre.getDureeMinutes());

            // Chevauchement : finA > debutB et finB > debutA
            if (finA.isAfter(debutB) && finB.isAfter(debutA)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Le coach a déjà une seance ce jour-là entre " + debutB + " et " + finB
                );
            }
        }
    }
}
