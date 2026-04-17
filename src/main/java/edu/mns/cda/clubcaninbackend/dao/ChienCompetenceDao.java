package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.ChienCompetence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChienCompetenceDao extends JpaRepository<ChienCompetence, ChienCompetence.Key> {
}
