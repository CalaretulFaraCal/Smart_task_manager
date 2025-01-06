package org.example.server.services;

import java.util.List;
import org.example.server.models.User;
import org.example.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder; // This is injected by Spring

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId) // Map User to its ID
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Create a user with an encrypted password
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user"); // Default to "USER" role
        }

        // Hash the raw password
        String rawPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(hashedPassword); // Set the hashed password

        // Save user and verify
        User savedUser = userRepository.save(user); // Save the user to the database

        // Debug outputs
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Hashed Password (to be stored): " + hashedPassword);
        System.out.println("Hashed Password (from DB): " + savedUser.getPassword());

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    // Retrieve user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Update user details
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getRole() != null && !updatedUser.getRole().isEmpty()) {
            existingUser.setRole(updatedUser.getRole());
        }

        return userRepository.save(existingUser);  // Save the updated user
    }

    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}
