package org.example.server.controllers;

import org.example.server.models.User;
import org.example.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AuthenticationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            // Pass raw password to the service layer
            System.out.println("Raw Password: " + user.getPassword());
            userService.createUser(user); // Hashing will be handled in the service

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again.");
        }
    }


    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam("email") String email, @RequestParam("password") String password) {
        try {
            if (email == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password are required.");
            }

            // Debug the entered password
            System.out.println("Entered password (from request): " + password);

            User user = userService.getUserByEmail(email);
            if (user != null) {
                System.out.println("User found with email: " + email);
                System.out.println("Entered password (from request): " + password);
                System.out.println("Hashed Password (from DB): " + user.getPassword());

                // Check password matching
                boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
                System.out.println("Password matches: " + passwordMatches);

                if (passwordMatches) {
                    return ResponseEntity.ok("Login successful!");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
                }
            } else {
                System.out.println("No user found with email: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}
