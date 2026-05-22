package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.TypeSeanceCompetence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeSeanceCompetenceDao extends JpaRepository<TypeSeanceCompetence, TypeSeanceCompetence.Key> {
}
