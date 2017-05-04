package test.analyzer;

import java.util.HashMap;

import org.junit.Test;

import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer;
import de.uni.due.ltl.interactiveStance.analyzer.TargetSearcher;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class CollocationNgramAnalyzerTest {

	@Test
	public void lexiconTest() throws Exception {
		
		//set up analyzer with some targets from the searcher
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		HashMap<String, ExplicitTarget> selectedTargets = new HashMap<>();
		TargetSearcher searcher= new TargetSearcher();
		searcher.SetUp(db,5);
		
		for(ExplicitTarget t :searcher.search("atheism",true)){
			selectedTargets.put(t.getId(), t);
		}
		
		
		CollocationNgramAnalyzer analyzer = new CollocationNgramAnalyzer(db);
		
		analyzer.analyze(selectedTargets,1);
		
	}
}
