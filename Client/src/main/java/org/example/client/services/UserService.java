package org.example.client.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.client.utility.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    public boolean registerUser(String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if email already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false; // Email already registered
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Insert the user into the database
            String insertQuery = "INSERT INTO users (email, password) VALUES (?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setString(1, email);
            insertStmt.setString(2, hashedPassword);
            insertStmt.executeUpdate();
            return true; // Registration successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Retrieve the hashed password from the database
            String query = "SELECT password FROM users WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword); // Verify password
            } else {
                return false; // Email not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
