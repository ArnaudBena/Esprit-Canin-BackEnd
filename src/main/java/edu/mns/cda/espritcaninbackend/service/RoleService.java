package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.RoleDao;
import edu.mns.cda.espritcaninbackend.exception.RoleNotFoundException;
import edu.mns.cda.espritcaninbackend.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    protected final RoleDao roleDao;

    public List<Role> findAll() {
        return roleDao.findAllOrderByNom();
    }

    public Optional<Role> findById(int id) {
        return roleDao.findById(id);
    }

    public void insert(Role role) {
        role.setId(null);
        roleDao.save(role);
    }

    public void update(int id, Role roleToUpdate) {
        if (roleDao.findById(id).isEmpty()) {
            throw new RoleNotFoundException(id);
        }
        roleToUpdate.setId(id);
        roleDao.save(roleToUpdate);
    }

    public void delete(int id) {
        if (roleDao.findById(id).isEmpty()) {
            throw new RoleNotFoundException(id);
        }
        roleDao.deleteById(id);
    }
}
