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
		searcher.SetUp(db,100);
		
		searcher.search("atheism",true);
		searcher.search("hillary",true);
		searcher.search("trump",true);
		searcher.search("abortion",true);
		searcher.search("climate*change",true);
		searcher.search("feminism",true);
		
		
//		assertEquals(db.printConnection(), "jdbc:mysql://localhost/interactiveArgumentMining?user=root&password=&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
//				+ System.lineSeparator() + "root@localhost" + System.lineSeparator());
	}
}
