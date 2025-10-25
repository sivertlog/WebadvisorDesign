package edu.redwoows.cis18.WebadvisorDesign.security;

import edu.redwoows.cis18.WebadvisorDesign.models.User;
import edu.redwoows.cis18.WebadvisorDesign.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        // Find user
        User user = userRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Use your PermissionService to verify password (this WILL be called!)
        boolean passwordValid = verifyPassword(rawPassword, user);

        if (passwordValid) {
            // Load user authorities/permissions from roles
            var authorities = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> new SimpleGrantedAuthority(permission.getPermName()))
                    .collect(Collectors.toList());
            // If no authorities, add a default one
            if (authorities.isEmpty()) {
                authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            }
            // Create successful authentication
            return new UsernamePasswordAuthenticationToken(
                    username,
                    rawPassword,
                    authorities
            );
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public boolean verifyPassword(String rawPassword, User user) {
        // Combine raw password with stored salt and verify against stored hash
        String fullPassword = rawPassword + user.getUserSalt();
        return passwordEncoder.matches(fullPassword, user.getUserPasswordHash());
    }
}