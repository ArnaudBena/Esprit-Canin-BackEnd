package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.TypeSeanceCompetenceConferee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeSeanceCompetenceConfereeDao extends JpaRepository<TypeSeanceCompetenceConferee, TypeSeanceCompetenceConferee.Key> {
}
