package de.uni.due.ltl.interactiveStance.db;

import com.mysql.cj.api.jdbc.Statement;
import de.uni.due.ltl.interactiveStance.client.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHelper {

    private Connection conn;

    public void addUser(User user) {
        if (isUserExisted(user.getUsername())) {
            System.out.println("User existed, please try another user name.");
            return ;
        }

        conn = DBUtil.getConnection();

        ResultSet rs = null;
        String query = "INSERT INTO user (username, password) VALUES (?, ?)";

        long id = -1;
        try {
            PreparedStatement stat = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, user.getUsername());
            stat.setString(2, user.getPassword());
            if (!stat.execute()) {
                System.out.println("Insert new user into database failed.");
            }
        } catch (SQLException e) {
            DBUtil.closeConnection();
            e.printStackTrace();
        }

        DBUtil.closeConnection();
    }

    public User getUser(String username) {
        conn = DBUtil.getConnection();

        User user = null;
        ResultSet rs = null;
        String query = "SELECT id, username, password FROM user WHERE username=?";
        try {
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, username);
            rs = stat.executeQuery();
            while (rs.next()) {
                user = new User(rs.getLong("id"), rs.getString("username"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBUtil.closeConnection();
            return null;
        }

        DBUtil.closeConnection();
        return user;
    }

    private boolean isUserExisted(String username) {
        if (getUser(username) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUser(String username, String password) {
        User user = getUser(username);
        if (user == null) {
            return false;
        } else {
            if (user.getPassword().equals(password)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String hashPassword(String pass) {
        String result = null;
        if (pass == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes());
            byte[] bytes = md.digest();
            result = new BigInteger(1, bytes).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

}
