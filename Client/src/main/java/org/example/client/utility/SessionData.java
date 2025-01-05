package org.example.client.utility;

public class SessionData {
    private static String loggedInUserEmail;
    private static String loggedInPassword;

    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }

    public static String getLoggedInPassword() {
        return loggedInPassword;
    }

    public static void setLoggedInPassword(String password) {
        loggedInPassword = password;
    }
}
