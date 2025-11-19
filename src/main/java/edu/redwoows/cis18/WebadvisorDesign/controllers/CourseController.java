package edu.redwoows.cis18.WebadvisorDesign.controllers;

import edu.redwoows.cis18.WebadvisorDesign.models.Course;
import edu.redwoows.cis18.WebadvisorDesign.models.User;
import edu.redwoows.cis18.WebadvisorDesign.repositories.CourseRepository;
import edu.redwoows.cis18.WebadvisorDesign.repositories.PermissionRepository;
import edu.redwoows.cis18.WebadvisorDesign.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class CourseController {
}
