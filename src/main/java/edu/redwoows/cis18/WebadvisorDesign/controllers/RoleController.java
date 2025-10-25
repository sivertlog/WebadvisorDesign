package edu.redwoows.cis18.WebadvisorDesign.controllers;

import edu.redwoows.cis18.WebadvisorDesign.models.Role;
import edu.redwoows.cis18.WebadvisorDesign.models.Permission;
import edu.redwoows.cis18.WebadvisorDesign.repositories.RoleRepository;
import edu.redwoows.cis18.WebadvisorDesign.repositories.PermissionRepository;
import edu.redwoows.cis18.WebadvisorDesign.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    // CREATE - Add new role
    @PostMapping
    @RequiresPermission("ROLE_CREATE")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
        try {
            // Check if role name already exists
            Optional<Role> existingRole = roleRepository.findByRoleName(roleRequest.getName());
            if (existingRole.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Role name already exists"));
            }

            Role role = new Role();
            role.setRoleName(roleRequest.getName());

            // Set permissions if provided
            if (roleRequest.getPermissionIds() != null && !roleRequest.getPermissionIds().isEmpty()) {
                Set<Permission> permissions = new HashSet<>();
                for (Integer permId : roleRequest.getPermissionIds()) {
                    Permission permission = permissionRepository.findById(permId)
                            .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permId));
                    permissions.add(permission);
                }
                role.setPermissions(permissions);
            }

            Role savedRole = roleRepository.save(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create role: " + e.getMessage()));
        }
    }

    // READ - Get all roles
    @GetMapping
    @RequiresPermission("ROLE_READ")
    public ResponseEntity<Iterable<Role>> getAllRoles() {
        Iterable<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    // READ - Get role by ID
    @GetMapping("/{id}")
    @RequiresPermission("ROLE_READ")
    public ResponseEntity<?> getRoleById(@PathVariable Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return ResponseEntity.ok(role.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Role not found with id: " + id));
        }
    }

    // READ - Get role by name
    @GetMapping("/name/{name}")
    @RequiresPermission("ROLE_READ")
    public ResponseEntity<?> getRoleByName(@PathVariable String name) {
        Optional<Role> role = roleRepository.findByRoleName(name);
        if (role.isPresent()) {
            return ResponseEntity.ok(role.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Role not found with name: " + name));
        }
    }

    // UPDATE - Update role
    @PutMapping("/{id}")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody RoleRequest roleRequest) {
        try {
            Optional<Role> existingRoleOpt = roleRepository.findById(id);
            if (existingRoleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Role not found with id: " + id));
            }

            Role existingRole = existingRoleOpt.get();

            // Update name if provided
            if (roleRequest.getName() != null) {
                // Check if new name is already taken by another role
                Optional<Role> roleWithSameName = roleRepository.findByRoleName(roleRequest.getName());
                if (roleWithSameName.isPresent() && !roleWithSameName.get().getRoleId().equals(id)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Role name already taken by another role"));
                }
                existingRole.setRoleName(roleRequest.getName());
            }

            // Update permissions if provided
            if (roleRequest.getPermissionIds() != null) {
                Set<Permission> permissions = new HashSet<>();
                for (Integer permId : roleRequest.getPermissionIds()) {
                    Permission permission = permissionRepository.findById(permId)
                            .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permId));
                    permissions.add(permission);
                }
                existingRole.setPermissions(permissions);
            }

            Role updatedRole = roleRepository.save(existingRole);
            return ResponseEntity.ok(updatedRole);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update role: " + e.getMessage()));
        }
    }

    // DELETE - Delete role
    @DeleteMapping("/{id}")
    @RequiresPermission("ROLE_DELETE")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        try {
            if (!roleRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Role not found with id: " + id));
            }

            roleRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete role: " + e.getMessage()));
        }
    }

    // ADD PERMISSION to role
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<?> addPermissionToRole(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

            if (role.getPermissions() == null) {
                role.setPermissions(new HashSet<>());
            }

            role.getPermissions().add(permission);
            Role updatedRole = roleRepository.save(role);
            return ResponseEntity.ok(updatedRole);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add permission to role: " + e.getMessage()));
        }
    }

    // REMOVE PERMISSION from role
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<?> removePermissionFromRole(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

            if (role.getPermissions() != null) {
                role.getPermissions().remove(permission);
                Role updatedRole = roleRepository.save(role);
                return ResponseEntity.ok(updatedRole);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Role has no permissions"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove permission from role: " + e.getMessage()));
        }
    }

    // GET USERS with this role
    @GetMapping("/{roleId}/users")
    @RequiresPermission("ROLE_READ")
    public ResponseEntity<?> getUsersWithRole(@PathVariable Integer roleId) {
        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

            if (role.getUsers() != null) {
                return ResponseEntity.ok(role.getUsers());
            } else {
                return ResponseEntity.ok(Collections.emptyList());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get users with role: " + e.getMessage()));
        }
    }

    // Request DTO for Role operations
    public static class RoleRequest {
        private String name;
        private List<Integer> permissionIds;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Integer> getPermissionIds() { return permissionIds; }
        public void setPermissionIds(List<Integer> permissionIds) { this.permissionIds = permissionIds; }
    }
}