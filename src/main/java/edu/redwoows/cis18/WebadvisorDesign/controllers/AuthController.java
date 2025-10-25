package edu.redwoows.cis18.WebadvisorDesign.controllers;

import edu.redwoows.cis18.WebadvisorDesign.models.User;
import edu.redwoows.cis18.WebadvisorDesign.repositories.UserRepository;
import edu.redwoows.cis18.WebadvisorDesign.security.JwtTokenProvider;
import edu.redwoows.cis18.WebadvisorDesign.security.RequiresPermission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            AuthenticationConfiguration authenticationConfiguration,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            //TODO: Investigate if it would be possible to eliminate setting the security context
            // here and userRepository.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Get user details for JWT claims
            User user = userRepository.findByUserUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // No real reason to get permission authorities form JWT token since @RequiresPermission does it per method.
            /*
            // Extract authorities for JWT
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUserUsername(), authorities);
             */

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUserUsername());
            //TODO: If it's acceptable to remove the userRepository above, something like below might suffice.
            //String token = jwtTokenProvider.generateToken(loginRequest.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", user.getUserUsername());
            response.put("userId", user.getUserId());
            response.put("authenticated", true);
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", "24 hours"); // Match your jwtExpirationMs

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials", "authenticated", false));
        }
    }

    @PostMapping("/register")
    @RequiresPermission("USER_CREATE") // Only users with USER_CREATE can register new users
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Check if username already exists
            if (userRepository.findByUserUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username already exists"));
            }

            // Create new user
            User user = new User();
            user.setUserUsername(registerRequest.getUsername());

            // Hash password with salt
            String salt = UUID.randomUUID().toString();
            String hashedPassword = passwordEncoder.encode(registerRequest.getPassword() + salt);

            user.setUserPasswordHash(hashedPassword);
            user.setUserSalt(salt);

            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "User registered successfully", "userId", user.getUserId()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // With JWT, logout is client-side - just discard the token
        // If you need server-side logout (token blacklisting), you'd need additional logic
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logout successful - please discard your token", "authenticated", false));
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        String username = authentication.getName();
        Optional<User> user = userRepository.findByUserUsername(username);

        if (user.isPresent()) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("authenticated", true);
            userInfo.put("username", user.get().getUserUsername());
            userInfo.put("userId", user.get().getUserId());
            userInfo.put("roles", user.get().getRoles());
            // Don't expose password hash and salt in response

            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.ok(Map.of("authenticated", false));
    }

    // Request DTOs
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}