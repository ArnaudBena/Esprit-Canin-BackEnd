package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.RaceDao;
import edu.mns.cda.espritcaninbackend.exception.RaceNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Race;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RaceService {

    protected final RaceDao raceDao;
    protected final ChienDao chienDao;

    public List<Race> findAll() {
        return raceDao.findAllOrderByNom();
    }

    public Optional<Race> findById(int id) {
        return raceDao.findById(id);
    }

    public void insert(Race race) {
        race.setId(null);
        raceDao.save(race);
    }

    public void update(int id, Race raceToUpdate) {
        if (raceDao.findById(id).isEmpty()) {
            throw new RaceNotFoundException(id);
        }
        raceToUpdate.setId(id);
        raceDao.save(raceToUpdate);
    }

    public void delete(int id) {
        if (raceDao.findById(id).isEmpty()) {
            throw new RaceNotFoundException(id);
        }

        // Pas de suppression d'une race utilisée par au moins un chien
        if (chienDao.countByRaceId(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette race est utilisée par un ou plusieurs chiens, suppression impossible"
            );
        }
        raceDao.deleteById(id);
    }
}