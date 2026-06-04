package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionDao extends JpaRepository<Inscription, Inscription.Key> {

    /**
     * Toutes les inscriptions des chiens d'un propriétaire (espace adhérent : "mes inscriptions").
     * Séance la plus récente d'abord.
     */
    @Query("SELECT i FROM Inscription i WHERE i.chien.utilisateur.id = :utilisateurId ORDER BY i.seance.date DESC")
    List<Inscription> findByProprietaire(@Param("utilisateurId") Integer utilisateurId);
}