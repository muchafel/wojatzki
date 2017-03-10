package de.uni.due.ltl.interactiveStance.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DataSet {
	private int ID;
	private String url;
	private String name;
	private String website;
	private List<String> keyWords;
	
	public DataSet(String url, String name, String website, List<String> keyWords, int numberOfFavorInstances,int numberOfAgainstInstances) {
		this.url = url;
		this.name = name;
		this.website = website;
		this.keyWords = keyWords;
	}
	

	public int getID() throws Exception{
		if(this.ID==0){
			throw new Exception("DataSet has no ID, possibly you did not retrieve if from the database?");
		}
		return ID;
	}
	/**
	 * should only be called when instance is taken from the DB
	 * @param iD
	 */
	public void setID(int iD) {
		ID = iD;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public List<String> getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(List<String> keyWords) {
		this.keyWords = keyWords;
	}

	public void serialize(Connection connection) throws SQLException {
		Statement st = connection.createStatement();
		String query="INSERT INTO `interactiveArgumentMining`.`Data_Set` (`ID`, `Url`, `Name`, `Website`, `KeyWords`) " + "VALUES (Null,'" + this.url
				+ "', '" + this.name + "', '" + this.website + "', '" + StringUtils.join(keyWords, " ") +  ")";
		st.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
		
		ResultSet rs = st.getGeneratedKeys();
        if (rs.next()){
            this.ID=rs.getInt(1);
        }
        rs.close();
		st.close();
	}

	public void delete(Connection connection) throws Exception {
		Statement st = connection.createStatement();
		st.executeUpdate("DELETE FROM Data_Set WHERE ID = "+this.getID());
		st.close();
	}

}
