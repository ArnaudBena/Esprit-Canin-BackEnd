package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.ChienCompetence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChienCompetenceDao extends JpaRepository<ChienCompetence, ChienCompetence.Key> {
}
