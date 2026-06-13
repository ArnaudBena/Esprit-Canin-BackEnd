package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurDao extends JpaRepository<Utilisateur, Integer> {

    /**
     * Retourne tous les utilisateurs triés alphabétiquement (nom puis prénom).
     */
    @Query("SELECT u FROM Utilisateur u ORDER BY u.nom ASC, u.prenom ASC")
    List<Utilisateur> findAllOrderByNomPrenom();

    /**
     * Recherche des utilisateurs pour l'admin.
     * Texte cherché dans nom/prénom/email.
     */
    @Query("SELECT u FROM Utilisateur u " +
            "WHERE (LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
            "       OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
            "       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :recherche, '%'))) " +
            "AND (:roleId IS NULL OR u.role.id = :roleId) " +
            "ORDER BY u.nom ASC, u.prenom ASC")
    List<Utilisateur> search(@Param("recherche") String recherche,
                             @Param("roleId") Integer roleId);

    /**
     * Compte les utilisateurs inscrits depuis la date donnée.
     * Utilisé pour le KPI "nouveaux utilisateurs ce mois" du dashboard admin.
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.dateInscription >= :debut ")
    long countNouveauxDepuis(@Param("debut") LocalDate debut);

    /**
     * Récupère un utilisateur par son email. Utilisé par UtilisateurDetailsService
     * pour le login (Spring Security identifie un user par son "username" = email dans mon projet).
     * Decouverte des Derived query suite au cours: Spring Data JPA génère le SQL automatiquement depuis le nom de la méthode.
     * Uniquement pour les requetes simples : SELECT * FROM utilisateur WHERE email = ?
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Compte les utilisateurs d'un rôle donné (par nom).
     * Utilisé par AdminInitializer pour savoir s'il existe déjà un admin.
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role.nom = :roleNom")
    long countByRoleNom(@Param("roleNom") String roleNom);
}
