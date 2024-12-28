package org.example.server.services;

import java.util.List;
import org.example.server.models.User;
import org.example.server.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // This is injected by Spring

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a user with an encrypted password
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // Default to "USER" role
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encrypt the password
        return userRepository.save(user);  // Save the user to the database
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
