package org.example.server.repositories;

import org.example.server.models.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
}
