package org.example.server.controllers;

import org.example.server.models.User;
import org.example.server.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            // Encode the password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.createUser(user);
    
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again.");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        try {
            if (email == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password are required.");
            }

            User user = userService.getUserByEmail(email);

            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok("Login successful!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok("User updated successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again.");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again.");
        }
    }
}
