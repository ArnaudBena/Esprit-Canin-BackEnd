package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceDao extends JpaRepository<Race, Integer> {

    @Query("SELECT r FROM Race r ORDER BY r.nom ASC")
    List<Race> findAllOrderByNom();
}
