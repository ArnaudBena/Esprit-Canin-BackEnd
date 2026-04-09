package edu.mns.cda.clubcaninbackend.service;

import edu.mns.cda.clubcaninbackend.model.Seance;
import edu.mns.cda.clubcaninbackend.model.TypeSeance;
import org.springframework.stereotype.Service;

@Service
public class SeanceService {
    public Seance creerSeance(Seance seance) {
        TypeSeance type = seance.getTypeSeance();

        if (seance.getDureeMinutes() < type.getDureeMinimale()) {
            throw new IllegalArgumentException(
                    "La durée ne peut être inférieure à "
                    + type.getDureeMinimale() + " minutes"
            );
        }

        if (seance.getDureeMinutes() > type.getDureeMaximale()) {
            throw new IllegalArgumentException(
                    "La durée ne peut pas dépasser"
                    + type.getDureeMaximale() + " minutes"
            );
        }

    return seanceRepository.save(seance);

    }
}
