package de.uni.due.ltl.interactiveStance.client;

import de.uni.due.ltl.interactiveStance.db.UserHelper;

public class Authentification {

    // Authentificate user
    public static boolean authenticate(String username, String password) {
        UserHelper userHelper = new UserHelper();
        if (userHelper.checkUser(username, password)) {
            return true;
        } else {
            return false;
        }
    }
}
