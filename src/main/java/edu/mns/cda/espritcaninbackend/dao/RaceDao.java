package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceDao extends JpaRepository<Race, Integer> {
}
