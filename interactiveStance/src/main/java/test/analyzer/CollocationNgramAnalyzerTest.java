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
		HashMap<String, ExplicitTarget> selectedTargetsFavor = new HashMap<>();
		HashMap<String, ExplicitTarget> selectedTargetsAgainst = new HashMap<>();
		TargetSearcher searcher= new TargetSearcher();
		searcher.SetUp(db,3);
		
		for(ExplicitTarget t :searcher.search("evolution",true)){
			selectedTargetsFavor.put(t.getId(), t);
		}
		
		for(ExplicitTarget t :searcher.search("god",true)){
			selectedTargetsAgainst.put(t.getId(), t);
		}
		
		CollocationNgramAnalyzer analyzer = new CollocationNgramAnalyzer(db);
		
		analyzer.analyze(selectedTargetsFavor,selectedTargetsAgainst,1);
		
	}
}