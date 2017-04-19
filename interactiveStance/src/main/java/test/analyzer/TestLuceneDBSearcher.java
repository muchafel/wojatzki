package test.analyzer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uni.due.ltl.interactiveStance.analyzer.TargetSearcher;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class TestLuceneDBSearcher {
	
	
	
	
	@Test
	public void searchTest() throws Exception {
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		
		TargetSearcher searcher= new TargetSearcher();
		searcher.SetUp(db);
		
		searcher.search("atheism");
		searcher.search("hillary");
		searcher.search("trump");
		searcher.search("abortion");
		searcher.search("climate*change");
		searcher.search("feminism");
		
		
//		assertEquals(db.printConnection(), "jdbc:mysql://localhost/interactiveArgumentMining?user=root&password=&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
//				+ System.lineSeparator() + "root@localhost" + System.lineSeparator());
	}
}
