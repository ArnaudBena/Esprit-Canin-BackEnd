package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Sexe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChienDao extends JpaRepository<Chien, Integer> {

    @Query("SELECT c FROM Chien c ORDER BY c.nom ASC")
    List<Chien> findAllOrderByNom();

    /**
     * Recherche des chiens pour l'admin.
     * - recherche : texte cherché dans nom chien, race.nom, nom/prénom propriétaire.
     */
    @Query("SELECT c FROM Chien c " +
            "WHERE (LOWER(c.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
            "       OR LOWER(c.race.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
            "       OR LOWER(c.utilisateur.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
            "       OR LOWER(c.utilisateur.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))) " +
            "AND (:filtrerSexe = FALSE OR c.sexe = :sexe) " +
            "ORDER BY c.nom ASC")
    List<Chien> search(@Param("recherche") String recherche,
                       @Param("sexe") Sexe sexe,
                       @Param("filtrerSexe") boolean filtrerSexe);

    /**
     * Compte le nombre de chiens qui utilisent la race donnée.
     * Utilisé par RaceService.delete pour bloquer la suppression d'une race référencée.
     */
    @Query("SELECT COUNT(c) FROM Chien c WHERE c.race.id = :raceId")
    long countByRaceId(@Param("raceId") Integer raceId);
}