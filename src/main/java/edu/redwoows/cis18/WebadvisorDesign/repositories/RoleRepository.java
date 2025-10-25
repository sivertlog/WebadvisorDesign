package edu.redwoows.cis18.WebadvisorDesign.repositories;

import edu.redwoows.cis18.WebadvisorDesign.models.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName);
}