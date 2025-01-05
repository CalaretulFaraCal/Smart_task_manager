package org.example.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.client.models.Task;
import org.example.client.utility.SessionData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BackendService {

    public static Long savedUserId;

    private static final String BASE_URL = "http://localhost:8080";

    public boolean loginUser(String email, String password) {
        try {
            // Construct the URL with query parameters
            String urlString = BASE_URL + "/authentication/login?email=" + email + "&password=" + password;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Keep as GET
            connection.setRequestProperty("Content-Type", "application/json");

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {

                return true;
            }
            else {
                System.err.println("Login failed. HTTP Code: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if any exception occurs
        }
    }

    public boolean registerUser(String username, String email, String password) {
        try {
            URL url = new URL(BASE_URL + "/authentication/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInput = "{\n" +
                    "    \"username\": \"" + username + "\",\n" +
                    "    \"email\": \"" + email + "\",\n" +
                    "    \"password\": \"" + password + "\"\n" +
                    "}";

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInput.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 201) {
                return true;
            } else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Task> getTasksForLoggedInUser() throws Exception {
        // Retrieve logged-in user's email from SessionData
        String email = SessionData.getLoggedInUserEmail(); // Assuming SessionData has this method
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("User email is not available. Cannot fetch tasks.");
        }

        // Fetch user ID from the backend
        String userIdUrl = BASE_URL + "/users/by-email/" + URLEncoder.encode(email, StandardCharsets.UTF_8);
        URL userIdEndpoint = new URL(userIdUrl);
        HttpURLConnection userIdConnection = (HttpURLConnection) userIdEndpoint.openConnection();
        userIdConnection.setRequestMethod("GET");
        userIdConnection.setRequestProperty("Content-Type", "application/json");

        int userIdResponseCode = userIdConnection.getResponseCode();
        if (userIdResponseCode != 200) {
            throw new Exception("Failed to fetch user ID. HTTP Code: " + userIdResponseCode);
        }

        Long loggedInUserId;
        try (InputStream is = userIdConnection.getInputStream()) {
            String jsonResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            loggedInUserId = Long.parseLong(jsonResponse); // Assuming the backend returns just the ID as plain text
            savedUserId = loggedInUserId;
            System.out.println("Fetched user ID: " + loggedInUserId);
        }

        // Fetch tasks for the user
        String urlString = BASE_URL + "/task/users/" + loggedInUserId;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);
        if (responseCode == 200) {
            try (InputStream is = connection.getInputStream()) {
                String jsonResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                // Log raw API response
                System.out.println("Raw API Response: " + jsonResponse);

                // Parse the tasks from JSON
                JSONArray jsonArray = new JSONArray(jsonResponse);
                List<Task> tasks = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject taskJson = jsonArray.getJSONObject(i);
                    Task task = new Task();
                    task.setTitle(taskJson.getString("title"));
                    task.setDescription(taskJson.optString("description", ""));
                    task.setPriority(taskJson.getString("priority"));
                    task.setDeadline(taskJson.getString("deadline"));
                    task.setCompleted(taskJson.getBoolean("completed"));
                    task.setCategory(taskJson.getString("category"));
                    task.setParentTaskId(taskJson.optLong("parentTaskId", 0L));
                    tasks.add(task);
                }

                // Log parsed tasks
                System.out.println("Parsed Tasks: " + tasks);
                return tasks;
            }
        } else {
            throw new Exception("Failed to fetch tasks. HTTP Code: " + responseCode);
        }

    }

    public static boolean updateTask(Long id, Task task) throws Exception {
        try {
            String urlString = BASE_URL + "/task/" + id; // Backend API endpoint
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Convert Task to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String taskJson = objectMapper.writeValueAsString(task);

            // Send JSON data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = taskJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK; // Return true for successful update
        } catch (Exception e) {
            throw new Exception("Error updating task: " + e.getMessage(), e);
        }
    }

    public boolean deleteTask(Long id) throws Exception {
        try {
            String urlString = BASE_URL + "/task/" + id; // Backend API endpoint
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            // Check response code
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_NO_CONTENT; // Return true if deleted successfully
        } catch (Exception e) {
            throw new Exception("Error deleting task: " + e.getMessage(), e);
        }
    }

    public boolean addTask(Task task, Long savedUserId) throws Exception {
        String urlString = BASE_URL + "/task/" + savedUserId; // Include userId in the URL
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Convert task object to JSON
        JSONObject taskJson = new JSONObject();
        taskJson.put("title", task.getTitle());
        taskJson.put("description", task.getDescription());
        taskJson.put("category", task.getCategory());
        taskJson.put("priority", task.getPriority());
        taskJson.put("deadline", task.getDeadline());
        taskJson.put("completed", task.isCompleted());

        // Write JSON to request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = taskJson.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 201) {
            System.out.println("Task created successfully!");
            return true;
        } else {
            System.err.println("Failed to create task. HTTP Code: " + responseCode);
            try (InputStream is = connection.getErrorStream()) {
                String errorResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.err.println("Error details: " + errorResponse);
            }
            return false;
        }
    }
}