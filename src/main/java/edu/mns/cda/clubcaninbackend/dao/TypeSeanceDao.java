package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.Seance;
import edu.mns.cda.clubcaninbackend.model.TypeSeance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeSeanceDao extends JpaRepository<TypeSeance, Integer> {
}
