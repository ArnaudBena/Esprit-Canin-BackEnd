package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.TypeSeanceDao;
import edu.mns.cda.espritcaninbackend.exception.TypeSeanceNotFoundException;
import edu.mns.cda.espritcaninbackend.model.TypeSeance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TypeSeanceService {

    protected final TypeSeanceDao typeSeanceDao;

    public List<TypeSeance> findAll() {
        return typeSeanceDao.findAllOrderByLibelle();
    }

    public Optional<TypeSeance> findById(int id) {
        return typeSeanceDao.findById(id);
    }

    public void insert(TypeSeance typeSeance) {
        typeSeance.setId(null);
        validerCoherence(typeSeance);
        typeSeanceDao.save(typeSeance);
    }

    public void update(int id, TypeSeance typeSeanceToUpdate) {
        if (typeSeanceDao.findById(id).isEmpty()) {
            throw new TypeSeanceNotFoundException(id);
        }
        typeSeanceToUpdate.setId(id);
        validerCoherence(typeSeanceToUpdate);
        typeSeanceDao.save(typeSeanceToUpdate);
    }

    public void delete(int id) {
        if (typeSeanceDao.findById(id).isEmpty()) {
            throw new TypeSeanceNotFoundException(id);
        }
        typeSeanceDao.deleteById(id);
    }

    // min <= max (âge / durée / participants) : cohérence non couverte par les @Min/@Max isolés.
    // Contrôle serveur, car le minMaxValidator du front ne protège pas un appel API direct.
    private void validerCoherence(TypeSeance typeSeance) {
        Integer ageMin = typeSeance.getAgeMinimumMois();
        Integer ageMax = typeSeance.getAgeMaximumMois();
        if (ageMin != null && ageMax != null && ageMin > ageMax) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'âge minimum ne peut pas être supérieur à l'âge maximum.");
        }

        if (typeSeance.getDureeMinimale() != null && typeSeance.getDureeMaximale() != null
                && typeSeance.getDureeMinimale() > typeSeance.getDureeMaximale()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La durée minimale ne peut pas être supérieure à la durée maximale.");
        }

        if (typeSeance.getParticipantsMinimum() != null && typeSeance.getParticipantsMaximum() != null
                && typeSeance.getParticipantsMinimum() > typeSeance.getParticipantsMaximum()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nombre minimum de participants ne peut pas être supérieur au maximum.");
        }
    }
}