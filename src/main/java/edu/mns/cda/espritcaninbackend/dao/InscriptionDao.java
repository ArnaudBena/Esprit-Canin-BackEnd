package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Inscription;
import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InscriptionDao extends JpaRepository<Inscription, Inscription.Key> {

    /**
     * Toutes les inscriptions des chiens d'un propriétaire (espace adhérent : "mes inscriptions").
     * Séance la plus récente d'abord.
     */
    @Query("SELECT i FROM Inscription i WHERE i.chien.utilisateur.id = :utilisateurId ORDER BY i.seance.date DESC")
    List<Inscription> findByProprietaire(@Param("utilisateurId") Integer utilisateurId);

    /**
     * Compte les inscriptions futures et non annulées portant sur les chiens d'un
     * utilisateur. Sert à empêcher la suppression admin d'un adhérent encore engagé.
     */
    @Query("""
        SELECT COUNT(i) FROM Inscription i
        WHERE i.chien.utilisateur.id = :userId
          AND i.seance.date >= :today
          AND i.statutPresence <> :annulee
        """)
    long countInscriptionsFuturesActives(@Param("userId") int userId,
                                         @Param("today") LocalDate today,
                                         @Param("annulee") StatutPresence annulee);

}