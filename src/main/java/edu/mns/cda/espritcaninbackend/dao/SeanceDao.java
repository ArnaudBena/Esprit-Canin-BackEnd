package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.model.StatutSeance;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeanceDao extends JpaRepository<Seance, Integer> {

    /**
     * Séances d'un coach à une date donnée.
     * Utilisé par la validation de chevauchement dans SeanceService.
     */
    @Query("SELECT s FROM Seance s WHERE s.coach = :coach AND s.date = :date")
    List<Seance> findByCoachAndDate(@Param("coach") Utilisateur coach,
                                    @Param("date") LocalDate date);

    /**
     * Toutes les séances triées par date ASC puis heureDebut ASC.
     * Utilisé pour le listing admin.
     */
    @Query("SELECT s FROM Seance s ORDER BY s.date ASC, s.heureDebut ASC")
    List<Seance> findAllOrderByDateHeure();

    /**
     * Compte les séances comprises dans un interval de dates (bornes incluses), filtrées par statut.
     * Utilisé pour les KPIs "seances ce mois" et "séances mois dernier" du dashboard admin.
     */
    @Query("SELECT COUNT(s) FROM Seance s " +
            "WHERE s.date BETWEEN :debut AND :fin " +
            "AND s.statut = :statut")
    long countByStatutEntre(@Param("debut") LocalDate debut,
                            @Param("fin") LocalDate fin,
                            @Param("statut") StatutSeance statut);

    /**
     * Retourne les séances à venir (date >= today) pour un statut donné, triées chronologiquement.
     * Utilisé pour la liste "prochaines séances" et le calcul du taux de remplissage moyen.
     */
    @Query("SELECT s FROM Seance s " +
            "WHERE s.date >= :today " +
            "AND s.statut = :statut " +
            "ORDER BY s.date ASC, s.heureDebut ASC")
    List<Seance> findByStatutDepuis(@Param("today") LocalDate today,
                                    @Param("statut") StatutSeance statut);
}
