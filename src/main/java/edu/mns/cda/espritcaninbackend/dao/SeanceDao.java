package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeanceDao extends JpaRepository<Seance, Integer> {
}
