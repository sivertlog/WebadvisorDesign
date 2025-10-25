package edu.redwoows.cis18.WebadvisorDesign.security;

import edu.redwoows.cis18.WebadvisorDesign.models.*;
import edu.redwoows.cis18.WebadvisorDesign.repositories.OperationRepository;
import edu.redwoows.cis18.WebadvisorDesign.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationRepository operationRepository;

    public boolean hasPermission(String username, String operationRoute, String requiredPermission) {
        System.out.printf("~~~ 1. hasPermission%n");
        User user = userRepository.findByUserUsername(username).orElse(null);
        System.out.printf("~~~ 2. hasPermission and found user %s%n", user.getUserUsername());
        if (user == null) return false;

        // Check if user has 'ALL' permission
        if (hasAllPermission(user)) {
            return true;
        }

        System.out.printf("~~~ 3. hasPermission and bypassed hasAllPermission looking for route %s%n", operationRoute);
        // Find the operation by route
        Operation operation = operationRepository.findByOpRoute(operationRoute).orElse(null);
        if (operation == null) return false;
        System.out.printf("~~~ 4. hasPermission operation is %s%n", operation.getOpId());

        // Get all permissions required for this operation
        //TODO: Currently we get these permissions, but don't use them for verification.
        //  Technically we could NOT include a permission in our annotation and ONLY put
        //  them in the databse, but that would force us to use operationPermissions here.
        Set<String> operationPermissions = operation.getPermissions().stream()
                .map(Permission::getPermName)
                .collect(Collectors.toSet());

        System.out.printf("~~~ 5. hasPermission operation perms %s%n", String.join(", ", operationPermissions));

        // Check if user has any of the required permissions through their roles
        Set<String> userPermissions = getUserPermissions(user);

        System.out.printf("~~~ 6. hasPermission user perms %s%n", String.join(", ", userPermissions));

        return userPermissions.stream()
                .anyMatch(operationPermissions::contains) ||
                userPermissions.contains(requiredPermission);
    }

    private boolean hasAllPermission(User user) {
        return getUserPermissions(user).contains("ALL");
    }

    private Set<String> getUserPermissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermName)
                .collect(Collectors.toSet());
    }
}