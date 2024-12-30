package org.example.client.services;

import org.example.client.models.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private Connection connection; // Assume this is initialized elsewhere

    public List<Task> getTasksForUser(String email) throws Exception {
        List<Task> tasks = new ArrayList<>();

        // Fetch userId based on email
        String userIdQuery = "SELECT id FROM users WHERE email = ?";
        PreparedStatement userIdStmt = connection.prepareStatement(userIdQuery);
        userIdStmt.setString(1, email);
        ResultSet userIdResult = userIdStmt.executeQuery();

        if (userIdResult.next()) {
            int userId = userIdResult.getInt("id");

            // Fetch taskIds for the user
            String taskIdsQuery = "SELECT taskId FROM task_users WHERE userId = ?";
            PreparedStatement taskIdsStmt = connection.prepareStatement(taskIdsQuery);
            taskIdsStmt.setInt(1, userId);
            ResultSet taskIdsResult = taskIdsStmt.executeQuery();

            while (taskIdsResult.next()) {
                int taskId = taskIdsResult.getInt("taskId");

                // Fetch task details for each taskId
                String taskQuery = "SELECT title, deadline FROM tasks WHERE id = ?";
                PreparedStatement taskStmt = connection.prepareStatement(taskQuery);
                taskStmt.setInt(1, taskId);
                ResultSet taskResult = taskStmt.executeQuery();

                if (taskResult.next()) {
                    String title = taskResult.getString("title");
                    String deadline = taskResult.getString("deadline");

                    Task task = new Task(title, deadline);
                    tasks.add(task);
                }
            }
        } else {
            throw new Exception("No user found for the given email.");
        }

        return tasks;
    }
}
