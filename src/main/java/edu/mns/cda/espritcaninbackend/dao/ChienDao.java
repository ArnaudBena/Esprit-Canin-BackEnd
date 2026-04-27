package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Chien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChienDao extends JpaRepository<Chien, Integer> {}
