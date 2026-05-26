package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceDao extends JpaRepository<Competence, Integer> {

    @Query("SELECT c FROM Competence c ORDER BY c.nom ASC")
    List<Competence> findAllOrderByNom();
}