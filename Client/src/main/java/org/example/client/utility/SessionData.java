package org.example.client.utility;

public class SessionData {
    private static String loggedInUserEmail;

    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }

}