package org.example.server.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication() != null ?
                        SecurityContextHolder.getContext().getAuthentication().getName() : null
        );
    }

    @SpringBootApplication
    @EnableAsync
    public class TaskManagementApplication {
        public static void main(String[] args) {
            SpringApplication.run(TaskManagementApplication.class, args);
        }
    }
}

