package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.InscriptionDao;
import edu.mns.cda.espritcaninbackend.exception.InscriptionNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Inscription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    protected final InscriptionDao inscriptionDao;

    public List<Inscription> findAll(){
        return inscriptionDao.findAll();
    }

    public Optional<Inscription> findById(Inscription.Key key){
        return inscriptionDao.findById(key);
    }

    public void insert(Inscription inscription){
        Inscription.Key key = new Inscription.Key(
                inscription.getChien().getId(),
                inscription.getSeance().getId()
        );

        if (inscriptionDao.findById(key).isPresent()){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce chien est déjà inscrit à cette séance"
            );
        }

        inscription.setId(key);
        // TODO (étape séances) : valider l'âge du chien dans les bornes du TypeSeance,
        //  la capacité de la séance non dépassée, et la séance non ANNULEE.

        inscriptionDao.save(inscription);
    }

    public void delete(Inscription.Key key)  {
        if (inscriptionDao.findById(key).isEmpty()){
            throw new InscriptionNotFoundException(key.getChienId(),  key.getSeanceId());
        }
        inscriptionDao.deleteById(key);
    }

    public void update(Inscription.Key key, Inscription inscriptionToUpdate)  {
        if (inscriptionDao.findById(key).isEmpty()){
            throw new InscriptionNotFoundException(key.getChienId(),  key.getSeanceId());
        }
        inscriptionToUpdate.setId(key);
        inscriptionDao.save(inscriptionToUpdate);
    }
}
