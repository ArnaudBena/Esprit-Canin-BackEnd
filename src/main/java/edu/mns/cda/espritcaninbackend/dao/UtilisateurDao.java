package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UtilisateurDao extends JpaRepository<Utilisateur, Integer> {

    /**
     * Retourne tous les utilisateurs triés alphabétiquement (nom puis prénom).
     */
    @Query("SELECT u FROM Utilisateur u ORDER BY u.nom ASC, u.prenom ASC")
    List<Utilisateur> findAllOrderByNomPrenom();

    /**
     * Compte les utilisateurs inscrits depuis la date donnée.
     * Utilisé pour le KPI "nouveaux utilisateurs ce mois" du dashboard admin.
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.dateInscription >= :debut ")
    long countNouveauxDepuis(@Param("debut") LocalDate debut);
}
