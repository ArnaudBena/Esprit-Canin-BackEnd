package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.TypeSeance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeSeanceDao extends JpaRepository<TypeSeance, Integer> {

    @Query("SELECT t FROM TypeSeance t ORDER BY t.libelle ASC")
    List<TypeSeance> findAllOrderByLibelle();
}