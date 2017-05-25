package de.uni.due.ltl.interactiveStance.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static String user = "root";
    private static String password = "Mysql.sowies0";
    private static String dbName = "jdbc:mysql://localhost/interactiveArgumentMining";
    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
    private static Connection conn = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(dbName + "?user=" + user + "&password=" + password
                        + "&useSSL=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            threadLocal.set(conn);
        }

        return conn;
    }

    public static void closeConnection() {
        Connection conn_tmp = (Connection) threadLocal.get();
        threadLocal.set(null);
        conn = null;

        if (conn_tmp != null) {
            try {
                conn_tmp.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
