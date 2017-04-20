package de.uni.due.ltl.interactiveStance.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.LegacyNumericTokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LegacyIntField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;

public class StanceDB {
	
	private String user;
	private String pw;
	private String dbPath;
	
	public void addDataSet(DataSet dataSet) throws SQLException{
		Connection connection  = getConnection();
		dataSet.serialize(connection);
		connection.close();
	}
	
	public List<String> getModelNames() throws SQLException{
		List<String> result= new ArrayList<>();
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		
		
		ResultSet resultSet = statement.executeQuery("SELECT `Name` FROM `Data_Set`");
		
		while (resultSet.next()) {
            result.add(resultSet.getString("Name"));
		}

		terminateSQLArtitfacts(connection,statement,resultSet);
		return result;
	}
	
	/**
	 * equpis a lucene index with all datasets in the DB
	 * @param w 
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public void setUpIndex(IndexWriter w) throws SQLException, IOException{
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		
		ResultSet resultSet = statement.executeQuery("Select  a.ID, a.Name, a.Website, fav, ag from (	SELECT a.ID, Name,Website, COUNT(b.Label) as fav FROM Data_Set as a, data_point as b WHERE a.ID = b.Data_Set_ID AND b.Label='FAVOR' GROUP By ID ) as a inner join (	SELECT a.ID, Name,Website, COUNT(b.Label) as ag FROM Data_Set as a, data_point as b WHERE a.ID = b.Data_Set_ID AND b.Label='AGAINST' GROUP By ID) as b on a.ID = b.ID;");
//		ResultSet resultSet = statement.executeQuery("SELECT `Data_Set`.`ID`,`Name`,`Website`, COUNT(data_point.Label) FROM `Data_Set`,`data_point` WHERE `Data_Set`.`ID` = `data_point`.`Data_Set_ID`  GROUP By `Name`");
		
		while (resultSet.next()) {
            addTarget(w, resultSet.getString("a.ID"),resultSet.getString("a.Name"),resultSet.getString("a.Website"),resultSet.getInt("fav"),resultSet.getInt("ag"));
		}
		terminateSQLArtitfacts(connection,statement,resultSet);
	}
	
	private void addTarget(IndexWriter w,String id, String name, String website, Integer favor, Integer against) throws IOException {
		int count= favor+against;
		Document doc = new Document();
		doc.add(new TextField("id", id, Field.Store.YES));
        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("website", website, Field.Store.YES));
        
        doc.add(new IntPoint("instanceCount", count));
        doc.add(new StoredField("instanceCount", count));
        doc.add(new NumericDocValuesField("instanceCount", count));
        doc.add(new IntPoint("favorCount", favor));
        doc.add(new StoredField("favorCount", favor));
        doc.add(new NumericDocValuesField("favorCount", favor));
        
        doc.add(new IntPoint("againstCount", against));
        doc.add(new StoredField("againstCount", against));
        doc.add(new NumericDocValuesField("againstCount", against));
        
        w.addDocument(doc);
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
		Connection connection = getConnection();
		
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
	            //register driver as we cannot be sure whether it is in catalina
	            Class.forName("com.mysql.jdbc.Driver");
	}

	public DataSet getDataByNameAndOrigin(String name, String website) throws SQLException {
		DataSet result=null;
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM Data_Set WHERE Name='"+name+"' AND Website='"+website+"'");
		while (resultSet.next()) {
			List<String> keywords=new ArrayList<String>(Arrays.asList(resultSet.getString("KeyWords").split(" ")));
			result= new DataSet(resultSet.getString("Url"), resultSet.getString("Name"), resultSet.getString("Website"), keywords);
			result.setID(resultSet.getInt("ID"));
			statement.close();
			connection.close();
			return result;
		}
		statement.close();
		connection.close();
		return result;
	}

	public void deleteDataSet(DataSet dataSetRetrieved) throws Exception {
		Connection connection  = getConnection();
		dataSetRetrieved.delete(connection);
	}

	public void addDataPoint(DataPoint dataPoint) throws SQLException {
		Connection connection= getConnection();
		dataPoint.serialize(connection);
		connection.close();
		
	}

	public DataPoint getDataPointById(int id) throws SQLException {
		Connection connection= getConnection();
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

	private Connection getConnection() throws SQLException {
		return  DriverManager.getConnection(dbPath+"?user=" + user + "&password=" + pw+"&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
	}

	public void deleteDataPoint(DataPoint dataPointRetrieved) throws Exception {
		Connection connection = getConnection();
		dataPointRetrieved.delete(connection);
		connection.close();
	}


}
