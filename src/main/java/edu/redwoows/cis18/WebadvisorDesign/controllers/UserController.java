package edu.redwoows.cis18.WebadvisorDesign.controllers;

import edu.redwoows.cis18.WebadvisorDesign.models.User;
import edu.redwoows.cis18.WebadvisorDesign.models.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path="/ping")
    public @ResponseBody String ping() {
        return "pong";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping(path="/add")
    public @ResponseBody String add(@RequestParam String userName,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam String primaryEmail) {
        User u = new User();
        u.setUserName(userName);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPrimaryEmail(primaryEmail);
        userRepository.save(u);
        return "Saved";
    }
}
