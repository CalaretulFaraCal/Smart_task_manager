package org.example.client.services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.net.URLEncoder;

public class BackendService {

    private static final String BASE_URL = "http://localhost:8080";

    public boolean loginUser(String email, String password) {
        try {
            // Construct the URL with query parameters
            String urlString = BASE_URL + "/authentication/login?email=" + email + "&password=" + password;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Keep as GET
            connection.setRequestProperty("Content-Type", "application/json"); // Optional, for consistency

            // Check the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Debugging info

            return responseCode == 200; // Return true if successful (HTTP 200)
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if any exception occurs
        }
    }


    public boolean registerUser(String username, String email, String password) {
        try {
            URL url = new URL(BASE_URL + "/authetication/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInput = "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInput.getBytes());
                os.flush();
            }

            return connection.getResponseCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
