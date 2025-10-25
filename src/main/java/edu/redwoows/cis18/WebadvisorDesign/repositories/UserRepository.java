package edu.redwoows.cis18.WebadvisorDesign.repositories;

import edu.redwoows.cis18.WebadvisorDesign.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    // Spring Data JPA lets you define other query methods simply by declaring their method signature. For example,
    // the userRepository bean includes the **findByEmail** method after this, and it gets auto-wired to our User entity
    // getEmail getter via JPA.
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserUsername(String userUsername);
    List<User> findByUserUsernameContaining(String userUsername);
    boolean existsByUserUsername(String userUsername);
}