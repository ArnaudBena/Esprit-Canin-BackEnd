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
     * - recherche : texte cherché dans nom chien, race.nom, nom/prénom propriétaire (case-insensitive).
     *   Toujours non-null (chaîne vide = pas de filtre, LIKE '%%' matche tout).
     * - sexe + filtrerSexe : Postgres ne sait pas inférer un enum null,
     *   donc on passe TOUJOURS un sexe + un booléen "filtrerSexe" qui décide si la clause s'applique.
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
}