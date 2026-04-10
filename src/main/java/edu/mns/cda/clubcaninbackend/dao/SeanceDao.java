package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeanceDao extends JpaRepository<Seance, Integer> {
}
