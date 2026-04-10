package edu.mns.cda.clubcaninbackend.service;

import edu.mns.cda.clubcaninbackend.dao.SeanceDao;
import edu.mns.cda.clubcaninbackend.model.Seance;
import edu.mns.cda.clubcaninbackend.model.TypeSeance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeanceService {

    public static class SeanceNotFoundException extends Exception {}

    protected final SeanceDao seanceDao;

    public Optional<Seance> findById(int id) {
        return seanceDao.findById(id);
    }

    public void insert(Seance seance) {
        seance.setId(null);
        validerDuree(seance);
        seanceDao.save(seance);
    }

    public void delete(int id) {
        seanceDao.deleteById(id);
    }

    public void update(int id, Seance seanceToUpdate) throws SeanceNotFoundException {
        Optional<Seance> optionalSeance = seanceDao.findById(id);

        if(optionalSeance.isEmpty()) {
            throw new SeanceNotFoundException();
        }

        seanceToUpdate.setId(id);
        validerDuree(seanceToUpdate);
        seanceDao.save(seanceToUpdate);
    }

    private void  validerDuree(Seance seance) {
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
}
