package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionDao extends JpaRepository<Inscription, Inscription.Key> {
}
