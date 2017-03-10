package de.uni.due.ltl.interactiveStance.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StanceDB {
	
	private String user;
	private String pw;
	private String dbPath;
	
	public void addDataSet(DataSet dataSet) throws SQLException{
		Connection connection = connection = DriverManager
				.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		dataSet.serialize(connection);
		connection.close();
	}
	
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
	
	public String printConnection() throws SQLException{
		Connection connection = connection = DriverManager
				.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		
		DatabaseMetaData metaData = connection.getMetaData();
		StringBuilder sb= new StringBuilder();
		sb.append(metaData.getURL()+System.lineSeparator());
		sb.append(metaData.getUserName()+System.lineSeparator());
		connection.close();
		return sb.toString();
		
	}
	
	
	public StanceDB(String user, String pw, String dbPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	            this.user= user;
	            this.pw=pw;
	            this.dbPath=dbPath;
	}

	public DataSet getDataByNameAndOrigin(String name, String website) throws SQLException {
		Connection connection = connection = DriverManager.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM Data_Set WHERE Name='"+name+"' AND Website='"+website+"'");
		while (resultSet.next()) {
			List<String> keywords=new ArrayList<String>(Arrays.asList(resultSet.getString("KeyWords").split(" ")));
			DataSet result= new DataSet(resultSet.getString("Url"), resultSet.getString("Name"), resultSet.getString("Website"), keywords, resultSet.getInt("#FAVOR"), resultSet.getInt("#AGAINST"));
			result.setID(resultSet.getInt("ID"));
			statement.close();
			connection.close();
			return result;
		}
		statement.close();
		connection.close();
		return null;
	}

	public void deleteDataSet(DataSet dataSetRetrieved) throws Exception {
		Connection connection = connection = DriverManager.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		dataSetRetrieved.delete(connection);
	}

	public void addDataPoint(DataPoint dataPoint) throws SQLException {
		Connection connection = connection = DriverManager
				.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		dataPoint.serialize(connection);
		connection.close();
		
	}

	public DataPoint getDataPointById(int id) throws SQLException {
		Connection connection = connection = DriverManager.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM Data_Point WHERE ID="+id);
		while (resultSet.next()) {
			DataPoint result= new DataPoint(resultSet.getInt("Data_Set_ID"), resultSet.getString("Text"), resultSet.getString("Label"));
			result.setId(resultSet.getInt("ID"));
			statement.close();
			connection.close();
			return result;
		}
		statement.close();
		connection.close();
		return null;
	}

	public void deleteDataPoint(DataPoint dataPointRetrieved) throws Exception {
		Connection connection = connection = DriverManager.getConnection(dbPath+"?user=" + user + "&password=" + pw);
		dataPointRetrieved.delete(connection);
		connection.close();
	}


}
