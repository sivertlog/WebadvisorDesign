package edu.redwoows.cis18.WebadvisorDesign.tools;

import edu.redwoows.cis18.WebadvisorDesign.models.User;
import edu.redwoows.cis18.WebadvisorDesign.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@Component
public class UserCreator implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only run if --create-user flag is passed
        if (args.length > 0) {
            switch (args[0]) {
                case "--change-password":
                    changePassword();
                    System.exit(0);
                    break;
                case "--create-user":
                    createUserInteractively();
                    System.exit(0);
                    break;
            }
        }
    }

    private String requestPassword(Scanner scanner) throws Exception {
        // Get password
        System.out.print("Enter NEW password: ");
        String password = scanner.nextLine().trim();

        // Confirm password
        System.out.print("Confirm NEW password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            throw new Exception("Error: passwords do not match!");
        }

        if (password.length() < 8) {
            throw new Exception("Error: Password must be at least 8 characters long!");
        }
        return password;
    }

    private void changePassword() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== User Change Password Tool ===");

        // Get username
        System.out.print("Enter user email to locate: ");
        String user_email = scanner.nextLine().trim();

        // Check if user already exists
        Optional<User> user = userRepository.findByUserEmail(user_email);
        if (!user.isPresent()) {
            System.out.println("Error: failed to find '" + user_email + "'!");
            return;
        }

        // Create user
        try {
            String password = requestPassword(scanner);
            User u = user.get();
            setPasswordHash(u, password);
            // TODO: refactor create user
            userRepository.save(u);
            System.out.println("✅ User '" + u.getUserUsername() + "' updated successfully!");
            System.out.println("User ID: " + u.getUserId());
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        scanner.close();
    }

    private void createUserInteractively() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== User Creation Tool ===");

        // Get username
        System.out.print("Enter new username: ");
        String username = scanner.nextLine().trim();

        // Check if user already exists
        if (userRepository.findByUserUsername(username).isPresent()) {
            System.out.println("Error: User '" + username + "' already exists!");
            return;
        }

        // Get user email
        System.out.print("Enter email for new user: ");
        String email = scanner.nextLine().trim();

        // Check if email already exists
        if (userRepository.findByUserEmail(email).isPresent()) {
            System.out.println("Error: User Email '" + email + "' already exists!");
            return;
        }

        // Create user
        try {
            String password = requestPassword(scanner);
            User user = new User();
            user.setUserUsername(username);
            user.setUserEmail(email);
            setPasswordHash(user, password);
            userRepository.save(user);
            System.out.println("✅ User '" + username + "' created successfully!");
            System.out.println("User ID: " + user.getUserId());
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        scanner.close();
    }

    private void setPasswordHash(User user, String password) {
        // Generate salt and hash password
        String salt = UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(password + salt);

        user.setUserPasswordHash(hashedPassword);
        user.setUserSalt(salt);
    }
}