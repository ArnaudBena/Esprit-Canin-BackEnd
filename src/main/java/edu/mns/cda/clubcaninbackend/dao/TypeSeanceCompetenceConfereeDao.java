package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.TypeSeanceCompetenceConferee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeSeanceCompetenceConfereeDao extends JpaRepository<TypeSeanceCompetenceConferee, TypeSeanceCompetenceConferee.Key> {
}
