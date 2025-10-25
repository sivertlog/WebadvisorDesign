package edu.redwoows.cis18.WebadvisorDesign.repositories;

import edu.redwoows.cis18.WebadvisorDesign.models.Operation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OperationRepository extends CrudRepository<Operation, Integer> {
    Optional<Operation> findByOpRoute(String opRoute);
}