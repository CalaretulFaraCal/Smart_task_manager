package org.example.server.config;

import org.example.server.models.User;
import org.example.server.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    // Constructor injection of UserRepository
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // PasswordEncoder bean for BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCryptPasswordEncoder bean
    }

    // Define the SecurityFilterChain bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Disable CSRF for testing
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authentication/**").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/task/**").permitAll()// Allow all user endpoints
                        .requestMatchers("/task/**").permitAll()// Allow unauthenticated access
                        .requestMatchers("/task/{taskId}/**").permitAll()
                        .anyRequest().authenticated() // All other requests require authentication
                );

        return http.build();
    }

    // Define a UserDetailsService bean for Spring Security
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Load user details directly from the UserRepository
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())  // Use email as username
                    .password(user.getPassword())   // Use encrypted password
                    .roles(user.getRole())  // Assign roles (USER, ADMIN)
                    .build();
        };
    }

    // Define the DaoAuthenticationProvider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());  // Set the custom UserDetailsService
        provider.setPasswordEncoder(passwordEncoder());  // Use the BCryptPasswordEncoder
        return provider;
    }
}
