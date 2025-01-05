package org.example.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.client.models.Subtask;
import org.example.client.models.Task;
import org.example.client.models.User;
import org.example.client.utility.SessionData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackendService {

    public static Long savedUserId;

    public static Long getSavedUserId() {
        return savedUserId;
    }
    public static void setSavedUserId(Long savedUserId) {}

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

                // Parse the tasks from JSON
                JSONArray jsonArray = new JSONArray(jsonResponse);
                List<Task> tasks = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject taskJson = jsonArray.getJSONObject(i);
                    Task task = new Task();
                    task.setId(taskJson.getLong("id"));
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

    public User getUserById(Long savedUserId) throws Exception {
        try {
            String urlString = BASE_URL + "/users/" + savedUserId;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Keep as GET
            connection.setRequestProperty("Content-Type", "application/json");

            // Use HttpClient for modern HTTP handling
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder(URI.create(url.toString()))
                            .GET()
                            .build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse JSON response into a User object
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.body(), User.class);
            } else {
                System.err.println("Error retrieving user: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            // Log error instead of using printStackTrace()
            Logger.getLogger(BackendService.class.getName()).log(Level.SEVERE, "Error in getUserById", e);
            return null;
        }
    }

    public boolean updateUser(Long userId, String username, String email, String password) {
        String url = BASE_URL + "/users/" + userId;

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode updateData = mapper.createObjectNode();

            if (username != null) {
                updateData.put("username", username);
            }
            if (email != null) {
                updateData.put("email", email);
            }
            if (password != null) {
                updateData.put("password", password); // Backend will handle hashing
            }

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(updateData)))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Subtask> getSubtasksForTask(Long taskId) {
        try {
            // Construct the URL
            String urlString = BASE_URL + "/subtask/" + taskId;
            URL url = new URL(urlString);

            // Open connection and set method to GET
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Parse the response
                try (InputStream is = connection.getInputStream()) {
                    String jsonResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                    // Use ObjectMapper to deserialize JSON into a list of Subtasks
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(jsonResponse, new TypeReference<List<Subtask>>() {});
                }
            } else {
                // Log any error responses
                try (InputStream errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        System.err.println("Failed to fetch subtasks: " + errorResponse);
                    }
                }
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean createSubtask(Long parentTaskId, String subtaskJson) {
        try {
            String urlString = BASE_URL + "/subtask/" + parentTaskId;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write JSON directly to the request body
            try (OutputStream os = connection.getOutputStream()) {
                os.write(subtaskJson.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_CREATED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSubtask(Long subtaskId, boolean completed) {
        try {
            String urlString = BASE_URL + "/subtask/" + subtaskId;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write JSON body
            JSONObject completionStatus = new JSONObject();
            completionStatus.put("completed", completed);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(completionStatus.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
