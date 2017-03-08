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
		assertEquals(dataSetRetrieved.getUrl(),dataSet.getUrl());
		assertEquals(dataSetRetrieved.getWebsite(),dataSet.getWebsite());
		assertEquals(dataSetRetrieved.getNumberOfAgainstInstances(),dataSet.getNumberOfAgainstInstances());
		assertEquals(dataSetRetrieved.getKeyWords(),dataSet.getKeyWords());
		assertEquals(dataSetRetrieved.getNumberOfFavorInstances(),dataSet.getNumberOfFavorInstances());
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
		
		DataPoint dataPointRetrieved= db.getDataByNameAndOrigin
		
		DataSet dataSetRetrieved = db.getDataByNameAndOrigin("kevkev_data", "pokekazan.de");
		db.deleteDataSet(dataSetRetrieved);
	}
}
