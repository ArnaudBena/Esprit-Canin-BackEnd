package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.Chien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChienDao extends JpaRepository<Chien, Long> {}
