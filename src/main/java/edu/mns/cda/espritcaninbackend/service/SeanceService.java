package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.exception.SeanceNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.model.StatutSeance;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeanceService {

    protected final SeanceDao seanceDao;
    protected final UtilisateurDao utilisateurDao;

    public Optional<Seance> findById(int id) {
        return seanceDao.findById(id);
    }

    public List<Seance> findAll() {
        return seanceDao.findAll();
    }

    public void insert(Seance seance) {
        seance.setId(null);
        if (seance.getStatut() == null) {
            seance.setStatut(StatutSeance.ACTIVE);
        }
        validerCoach(seance);
        validerDuree(seance);
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
        seanceDao.save(seanceToUpdate);
    }

    private void validerDuree(Seance seance) {
        if (seance.getTypeSeance() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le type de séance est obligatoire"
            );
        }

        int duree = seance.getDureeMinutes();
        int min = seance.getTypeSeance().getDureeMinimale();
        int max = seance.getTypeSeance().getDureeMaximale();

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
}
