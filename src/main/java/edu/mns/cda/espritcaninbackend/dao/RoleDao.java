package edu.mns.cda.espritcaninbackend.dao;

import edu.mns.cda.espritcaninbackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r ORDER BY r.nom ASC")
    List<Role> findAllOrderByNom();

    /**
     * Récupère un role par son nom. On utilise ça à l'inscription publique
     * pour forcer le role Adhrent (ne jamais faire confiance au role envoyé par le client). Derired Query car requette simple
     */
    Optional<Role> findByNom(String nom);
}