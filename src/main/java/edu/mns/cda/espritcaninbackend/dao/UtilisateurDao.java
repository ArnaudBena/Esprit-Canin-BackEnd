package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UtilisateurDao extends JpaRepository<Utilisateur, Integer> {

    /**
     * Compte les utilisateurs inscrits depuis la date donnée.
     * Utilisé pour le KPI "nouveaux utilisateurs ce mois" du dashboard admin.
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.dateInscription >= :debut ")
    long countNouveauxDepuis(@Param("debut") LocalDate debut);
}
