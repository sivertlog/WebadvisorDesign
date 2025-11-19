package edu.redwoows.cis18.WebadvisorDesign.repositories;

import edu.redwoows.cis18.WebadvisorDesign.models.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {
    Optional<Course> findByCourseName(String opRoute);
}