package de.uni.due.ltl.interactiveStance.dev;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.uima.UIMAException;
import org.dkpro.tc.api.exception.TextClassificationException;

import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer_distributionDerived;
import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer_fixedThresholds;
import de.uni.due.ltl.interactiveStance.analyzer.TargetSearcher;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;

public class Testbed_PolarityThresholds_distributional {

	public static void main(String[] args) throws Exception {
		// set up DB and selection
		StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
		HashMap<String, ExplicitTarget> selectedTargetsFavor = new HashMap<>();
		HashMap<String, ExplicitTarget> selectedTargetsAgainst = new HashMap<>();
		TargetSearcher searcher = new TargetSearcher();
		searcher.SetUp(db, 1);

		System.out.println("adding favor targets");
		for (ExplicitTarget t : searcher.search("God_Does_Not_Exist", true)) {
			selectedTargetsFavor.put(t.getId(), t);
		}

		System.out.println("adding against targets");
		for (ExplicitTarget t : searcher.search("Jesus", true)) {
			selectedTargetsAgainst.put(t.getId(), t);
		}

		// load evaluation Data
		EvaluationScenario data = new EvaluationScenario("Atheism","");

		
		ExperimentLogging logging= new ExperimentLogging("DistributionalThresholdTestUser");
		
		// set up analyzer
		CollocationNgramAnalyzer_distributionDerived analyzer = new CollocationNgramAnalyzer_distributionDerived(db,data,0.95,logging);
		
		//test on test data 
		analyzer.analyze(selectedTargetsFavor, selectedTargetsAgainst, 1,true);
//		analyzer.analyze(selectedTargetsFavor, selectedTargetsAgainst, 1,false);
//		analyzer.analyzeOptimized(selectedTargetsFavor, selectedTargetsAgainst, 1,true);

	}

}
