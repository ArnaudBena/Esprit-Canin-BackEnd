package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.TypeSeanceCompetence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeSeanceCompetenceDao extends JpaRepository<TypeSeanceCompetence, TypeSeanceCompetence.Key> {
}
