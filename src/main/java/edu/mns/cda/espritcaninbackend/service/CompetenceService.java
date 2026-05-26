package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.CompetenceDao;
import edu.mns.cda.espritcaninbackend.exception.CompetenceNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Competence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompetenceService {

    protected final CompetenceDao competenceDao;

    public List<Competence> findAll() {
        return competenceDao.findAllOrderByNom();
    }

    public Optional<Competence> findById(int id) {
        return competenceDao.findById(id);
    }

    public void insert(Competence competence) {
        competence.setId(null);
        competenceDao.save(competence);
    }

    public void update(int id, Competence competenceToUpdate) {
        if (competenceDao.findById(id).isEmpty()) {
            throw new CompetenceNotFoundException(id);
        }
        competenceToUpdate.setId(id);
        competenceDao.save(competenceToUpdate);
    }

    public void delete(int id) {
        if (competenceDao.findById(id).isEmpty()) {
            throw new CompetenceNotFoundException(id);
        }
        competenceDao.deleteById(id);
    }
}