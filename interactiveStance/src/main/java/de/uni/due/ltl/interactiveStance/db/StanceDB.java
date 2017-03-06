package de.uni.due.ltl.interactiveStance.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StanceDB {
	
	private String user;
	private String pw;
	
	public List<String> getModelNames() throws SQLException{
		List<String> result= new ArrayList<>();
		Connection connection = connection =DriverManager.getConnection("jdbc:mysql://localhost/test?user="+user+"&password="+pw);
		Statement statement = connection.createStatement();
		
		/**
		 * TODO SQLquery
		 */
		ResultSet resultSet = statement.executeQuery("SELECT * FROM Models");
		
		/**
		 * TODO SQLquery
		 */
		while (resultSet.next()) {
            result.add(resultSet.getString("NAME"));
		}

		terminateSQLArtitfacts(connection,statement,resultSet);
		return result;
	}
	
	private void terminateSQLArtitfacts(Connection connection, Statement statement, ResultSet resultset) throws SQLException {
		if (resultset != null) {
			resultset.close();
			resultset = null;
		}

		if (statement != null) {
			statement.close();
			statement = null;
		}
		if (connection != null) {
			connection.close();
			connection=null;
		}
		
	}

	public StanceDB(String user, String pw) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	            this.user= user;
	            this.pw=pw;
	}

}
