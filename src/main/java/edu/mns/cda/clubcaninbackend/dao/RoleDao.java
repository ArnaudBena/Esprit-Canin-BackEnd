package edu.mns.cda.clubcaninbackend.dao;

import edu.mns.cda.clubcaninbackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role, Integer> {
}
