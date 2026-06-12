package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.dto.PrerequisDto;
import edu.mns.cda.espritcaninbackend.dto.SeanceCatalogueDto;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import edu.mns.cda.espritcaninbackend.model.StatutSeance;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    /**
     * Recherche des séances pour l'admin.
     * TODO post-MVP : ajouter Pageable pour la pagination quand le volume de séances le justifiera.
     */
    @Query("SELECT s FROM Seance s " +
            "WHERE (:typeSeanceId IS NULL OR s.typeSeance.id = :typeSeanceId) " +
            "AND (:filtrerDate = FALSE OR s.date = :date) " +
            "AND (:coachId IS NULL OR s.coach.id = :coachId) " +
            "ORDER BY s.date ASC, s.heureDebut ASC")
    List<Seance> search(@Param("typeSeanceId") Integer typeSeanceId,
                        @Param("date") LocalDate date,
                        @Param("filtrerDate") boolean filtrerDate,
                        @Param("coachId") Integer coachId);

    /**
     * Catalogue adhérent : séances à venir et ACTIVE, projetées dans un DTO
     * restreint (aucune fuite des inscrits). Filtrage 100% serveur.
     * Les places sont calculées via une sous-requête COUNT des inscriptions non annulées.
     */
    @Query("""
            SELECT new edu.mns.cda.espritcaninbackend.dto.SeanceCatalogueDto(
                s.id, s.date, s.heureDebut, s.dureeMinutes, s.statut,
                s.typeSeance.id, s.typeSeance.libelle, s.typeSeance.description,
                s.typeSeance.ageMinimumMois, s.typeSeance.ageMaximumMois,
                s.coach.id, s.coach.nom, s.coach.prenom,
                s.typeSeance.participantsMaximum,
                (SELECT COUNT(i) FROM Inscription i WHERE i.seance = s AND i.statutPresence <> :annulee)
            )
            FROM Seance s
            WHERE s.date >= :today
              AND s.statut = :active
              AND (:typeSeanceId IS NULL OR s.typeSeance.id = :typeSeanceId)
              AND (:filtrerDate = FALSE OR s.date = :date)
              AND (:disponible IS NULL
                   OR (:disponible = TRUE  AND (SELECT COUNT(i) FROM Inscription i WHERE i.seance = s AND i.statutPresence <> :annulee) <  s.typeSeance.participantsMaximum)
                   OR (:disponible = FALSE AND (SELECT COUNT(i) FROM Inscription i WHERE i.seance = s AND i.statutPresence <> :annulee) >= s.typeSeance.participantsMaximum))
            ORDER BY s.date ASC, s.heureDebut ASC
            """)
    List<SeanceCatalogueDto> catalogue(@Param("today") LocalDate today,
                                       @Param("active") StatutSeance active,
                                       @Param("annulee") StatutPresence annulee,
                                       @Param("typeSeanceId") Integer typeSeanceId,
                                       @Param("date") LocalDate date,
                                       @Param("filtrerDate") boolean filtrerDate,
                                       @Param("disponible") Boolean disponible);

    /**
     * Détail catalogue d'une séance, même projection DTO restreinte.
     */
    @Query("""
            SELECT new edu.mns.cda.espritcaninbackend.dto.SeanceCatalogueDto(
                s.id, s.date, s.heureDebut, s.dureeMinutes, s.statut,
                s.typeSeance.id, s.typeSeance.libelle, s.typeSeance.description,
                s.typeSeance.ageMinimumMois, s.typeSeance.ageMaximumMois,
                s.coach.id, s.coach.nom, s.coach.prenom,
                s.typeSeance.participantsMaximum,
                (SELECT COUNT(i) FROM Inscription i WHERE i.seance = s AND i.statutPresence <> :annulee)
            )
            FROM Seance s
            WHERE s.id = :id
            """)
    Optional<SeanceCatalogueDto> catalogueById(@Param("id") Integer id,
                                               @Param("annulee") StatutPresence annulee);

    @Query("""
        SELECT new edu.mns.cda.espritcaninbackend.dto.PrerequisDto(c.nom, tsc.niveauMinimumRequis)
        FROM TypeSeanceCompetence tsc
        JOIN tsc.competence c
        WHERE tsc.typeSeance.id = (SELECT s.typeSeance.id FROM Seance s WHERE s.id = :seanceId)
        """)
    List<PrerequisDto> findPrerequisBySeanceId(@Param("seanceId") int seanceId);

    /**
     * Séances d'un coach, triées chronologiquement (ASC), avec filtres optionnels :
     * type de séance + fourchette de dates (période). null = filtre ignoré.
     */
    @Query("SELECT s FROM Seance s " +
            "WHERE s.coach.id = :coachId " +
            "AND (:typeSeanceId IS NULL OR s.typeSeance.id = :typeSeanceId) " +
            "AND (:filtrerPeriode = FALSE OR s.date BETWEEN :debut AND :fin) " +
            "ORDER BY s.date ASC, s.heureDebut ASC")
    List<Seance> findByCoach(@Param("coachId") Integer coachId,
                             @Param("typeSeanceId") Integer typeSeanceId,
                             @Param("filtrerPeriode") boolean filtrerPeriode,
                             @Param("debut") LocalDate debut,
                             @Param("fin") LocalDate fin);

    /**
     * Séances "à traiter" d'un coach : ACTIVE, dont au moins une inscription est encore INSCRIT
     * (l'appel n'a pas été fait), et déjà passées (date <= today, affinage précis ensuite côté service).
     */
    @Query("SELECT DISTINCT s FROM Seance s JOIN s.inscriptions i " +
            "WHERE s.coach.id = :coachId AND s.statut = :active " +
            "AND i.statutPresence = :inscrit AND s.date <= :today " +
            "ORDER BY s.date ASC, s.heureDebut ASC")
    List<Seance> findATraiterParCoach(@Param("coachId") Integer coachId,
                                      @Param("active") StatutSeance active,
                                      @Param("inscrit") StatutPresence inscrit,
                                      @Param("today") LocalDate today);
}
