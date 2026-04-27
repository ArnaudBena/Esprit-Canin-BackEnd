package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.TypeSeance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeSeanceDao extends JpaRepository<TypeSeance, Integer> {
}
