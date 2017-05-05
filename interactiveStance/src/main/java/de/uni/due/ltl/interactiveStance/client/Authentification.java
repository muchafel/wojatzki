package de.uni.due.ltl.interactiveStance.client;

public class Authentification {

    // Authentificate user
    public static boolean authenticate(String username, String password) {
        if (username.equals("test") && password.equals("test")) {
            return true;
        } else {
            return false;
        }
    }
}
