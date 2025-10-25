package edu.redwoows.cis18.WebadvisorDesign.controllers;

import edu.redwoows.cis18.WebadvisorDesign.models.User;
import edu.redwoows.cis18.WebadvisorDesign.models.Role;
import edu.redwoows.cis18.WebadvisorDesign.repositories.UserRepository;
import edu.redwoows.cis18.WebadvisorDesign.repositories.RoleRepository;
import edu.redwoows.cis18.WebadvisorDesign.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // CREATE - Add new user
    @PostMapping("/add")
    @RequiresPermission("USER_CREATE")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        try {
            // Check if username already exists
            if (userRepository.findByUserUsername(userRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username already exists"));
            }

            User user = new User();
            user.setUserUsername(userRequest.getUsername());
            user.setUserEmail(userRequest.getEmail());

            // Encode password
            String salt = UUID.randomUUID().toString();
            String hashedPassword = passwordEncoder.encode(userRequest.getPassword() + salt);
            user.setUserPasswordHash(hashedPassword);
            user.setUserSalt(salt);

            // Set roles if provided
            if (userRequest.getRoleIds() != null && !userRequest.getRoleIds().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                for (Integer roleId : userRequest.getRoleIds()) {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
                    roles.add(role);
                }
                user.setRoles(roles);
            }

            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    // READ - Get all users
    @GetMapping("/all")
    @RequiresPermission("USER_READ")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // READ - Get user by ID
    @GetMapping("/getid/{id}")
    @RequiresPermission("USER_READ")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found with id: " + id));
        }
    }

    // READ - Get user by username
    @GetMapping("/getun/{username}")
    @RequiresPermission("USER_READ")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUserUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found with username: " + username));
        }
    }

    // UPDATE - Update user
    @PutMapping("/updateid/{id}")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequest) {
        try {
            Optional<User> existingUserOpt = userRepository.findById(id);
            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with id: " + id));
            }

            User existingUser = existingUserOpt.get();

            // Update username if provided
            if (userRequest.getUsername() != null) {
                // Check if new username is already taken by another user
                Optional<User> userWithSameUsername = userRepository.findByUserUsername(userRequest.getUsername());
                if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getUserId().equals(id)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Username already taken by another user"));
                }
                existingUser.setUserUsername(userRequest.getUsername());
            }

            // Update password if provided
            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
                existingUser.setUserPasswordHash(hashedPassword);
            }

            // Update roles if provided
            if (userRequest.getRoleIds() != null) {
                Set<Role> roles = new HashSet<>();
                for (Integer roleId : userRequest.getRoleIds()) {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
                    roles.add(role);
                }
                existingUser.setRoles(roles);
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    // DELETE - Delete user
    @DeleteMapping("/del/{id}")
    @RequiresPermission("USER_DELETE")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with id: " + id));
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    // ADD ROLE to user
    @PostMapping("/addrole/{userId}/roles/{roleId}")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> addRoleToUser(@PathVariable Integer userId, @PathVariable Integer roleId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>());
            }

            user.getRoles().add(role);
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add role to user: " + e.getMessage()));
        }
    }

    // REMOVE ROLE from user
    @DeleteMapping("/delrole/{userId}/roles/{roleId}")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Integer userId, @PathVariable Integer roleId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

            if (user.getRoles() != null) {
                user.getRoles().remove(role);
                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(updatedUser);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "User has no roles"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove role from user: " + e.getMessage()));
        }
    }

    // Request DTO for User operations
    public static class UserRequest {
        private String username;
        private String password;
        private String email;
        private List<Integer> roleIds;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public List<Integer> getRoleIds() { return roleIds; }
        public void setRoleIds(List<Integer> roleIds) { this.roleIds = roleIds; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}