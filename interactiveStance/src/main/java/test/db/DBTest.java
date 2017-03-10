package test.db;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.DataSet;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class DBTest {

	@Test
	public void connectionTest() throws Exception {
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		System.out.println(db.printConnection());
		assertEquals(db.printConnection(), "jdbc:mysql://localhost/interactiveArgumentMining?user=root&password="
				+ System.lineSeparator() + "root@localhost" + System.lineSeparator());
	}

	@Test
	public void insertDeleteDataSetTest() throws Exception {
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		DataSet dataSet = new DataSet("pokekazan.de/kevkev_data", "kevkev_data", "pokekazan.de",
				new ArrayList<String>(Arrays.asList("Pika", "Pika", "Chu")), 100, 200);
		db.addDataSet(dataSet);
		DataSet dataSetRetrieved = db.getDataByNameAndOrigin("kevkev_data", "pokekazan.de");
		assertEquals(dataSetRetrieved.getName(),dataSet.getName());
		System.out.println(dataSetRetrieved.getName());
		assertEquals(dataSetRetrieved.getUrl(),dataSet.getUrl());
		assertEquals(dataSetRetrieved.getWebsite(),dataSet.getWebsite());
		assertEquals(dataSetRetrieved.getKeyWords(),dataSet.getKeyWords());
		db.deleteDataSet(dataSetRetrieved);
	}
	
	@Test
	public void insertDeleteDataPointTest() throws Exception {
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		DataSet dataSet = new DataSet("pokekazan.de/kevkev_data", "kevkev_data", "pokekazan.de",
				new ArrayList<String>(Arrays.asList("Pika", "Pika", "Chu")), 100, 200);
		db.addDataSet(dataSet);
		
		DataPoint datapoint= new DataPoint(dataSet, "This is a long stance-taking ngram collection in favor of some target", "FAVOR");
		db.addDataPoint(datapoint);
		
		System.out.println(datapoint.getId());
		DataPoint dataPointRetrieved= db.getDataPointById(datapoint.getId());
		System.out.println(dataPointRetrieved.getId());
		
		assertEquals(dataPointRetrieved.getDataSet_id(),datapoint.getDataSet_id());
		assertEquals(dataPointRetrieved.getId(),datapoint.getId());
		assertEquals(dataPointRetrieved.getLabel(),datapoint.getLabel());
		assertEquals(dataPointRetrieved.getText(),datapoint.getText());
		System.out.println(dataPointRetrieved.getText());
		
		db.deleteDataPoint(dataPointRetrieved);
		DataSet dataSetRetrieved = db.getDataByNameAndOrigin("kevkev_data", "pokekazan.de");
		db.deleteDataSet(dataSetRetrieved);
	}
	
	@Test
	public void complexInsertDeleteTest() throws Exception {
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		DataSet dataSet = new DataSet("pokekazan.de/kevkev_data", "kevkev_data", "pokekazan.de",
				new ArrayList<String>(Arrays.asList("Pika", "Pika", "Chu")), 100, 200);
		DataSet dataSet2 = new DataSet("medocafe.ch/melonpan", "melonpan", "medocafe.ch",
				new ArrayList<String>(Arrays.asList("ru", "xu", "nu")), 1000, 2000);
		db.addDataSet(dataSet);
		db.addDataSet(dataSet2);
		
		for(int i=0; i<20;i++){
			DataPoint datapoint = null;
			if(i% 2 == 0){
				 datapoint= new DataPoint(dataSet, String.valueOf(i+i), "FAVOR");
			}else{
				datapoint= new DataPoint(dataSet2, String.valueOf(i+2), "AGAINST");
			}
			
			db.addDataPoint(datapoint);
			
			DataPoint dataPointRetrieved= db.getDataPointById(datapoint.getId());
			assertEquals(dataPointRetrieved.getDataSet_id(),datapoint.getDataSet_id());
			assertEquals(dataPointRetrieved.getId(),datapoint.getId());
			assertEquals(dataPointRetrieved.getLabel(),datapoint.getLabel());
			assertEquals(dataPointRetrieved.getText(),datapoint.getText());
			db.deleteDataPoint(dataPointRetrieved);
		}
		
		db.deleteDataSet(dataSet);
		db.deleteDataSet(dataSet2);
	}
	
}
