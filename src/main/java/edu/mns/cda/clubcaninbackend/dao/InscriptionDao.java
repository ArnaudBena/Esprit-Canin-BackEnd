package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionDao extends JpaRepository<Inscription, Inscription.Key> {
}
