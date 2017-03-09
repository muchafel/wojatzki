package de.uni.due.ltl.interactiveStance.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

public class DataPoint {
	private int id;
	private int dataSet_id;
	private String text;
	private String label;
	
	public DataPoint(DataSet dataSet, String text, String label) throws Exception {
		this.dataSet_id = dataSet.getID();
		this.text = text;
		this.label = label;
	}
	
	public DataPoint(int dataSet_ID, String text, String label) {
		this.dataSet_id = dataSet_ID;
		this.text = text;
		this.label = label;
	}
	
	public int getId() throws Exception {
		if(this.id==0){
			throw new Exception("DataPoint has no ID, possibly you did not retrieve if from the database?");
		}
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDataSet_id() {
		return dataSet_id;
	}
	public void setDataSet_id(int dataSet_id) {
		this.dataSet_id = dataSet_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void serialize(Connection connection) throws SQLException {
		Statement st = connection.createStatement();
		String query="INSERT INTO `interactiveArgumentMining`.`Data_Point` (`ID`, `Data_Set_ID`, `Text`, `Label`) " + "VALUES (Null, " + this.dataSet_id
				+ ", '" + this.text + "', '" + this.label + "')";
		st.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = st.getGeneratedKeys();
        if (rs.next()){
            this.id=rs.getInt(1);
        }
        rs.close();
		st.close();
	}

	public void delete(Connection connection) throws Exception {
		Statement st = connection.createStatement();
		st.executeUpdate("DELETE FROM `interactiveArgumentMining`.`Data_Point` WHERE ID = "+this.getId());
		st.close();
	}
}
