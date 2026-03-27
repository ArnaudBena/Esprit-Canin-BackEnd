package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceDao extends JpaRepository<Race, Integer> {
}
