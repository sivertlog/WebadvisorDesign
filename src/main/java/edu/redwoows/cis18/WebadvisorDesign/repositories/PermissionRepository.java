package edu.redwoows.cis18.WebadvisorDesign.repositories;

import edu.redwoows.cis18.WebadvisorDesign.models.Permission;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, Integer> {}