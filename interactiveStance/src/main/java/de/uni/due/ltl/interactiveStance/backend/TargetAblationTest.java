package de.uni.due.ltl.interactiveStance.backend;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.dkpro.tc.api.exception.TextClassificationException;

import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzerBase;

/**
 * performs an ablation-like test on the selected targets (evaluates performance when excluding a target)
 * @author michael
 *
 */
public class TargetAblationTest {

	private HashMap<String, ExplicitTarget> selectedFavorTargets;
	private HashMap<String, ExplicitTarget> selectedAgainstTargets;
	private CollocationNgramAnalyzerBase analyzer;
	private boolean evaluateTrain; 
	
	
	public TargetAblationTest(HashMap<String, ExplicitTarget> selectedFavorTargets, HashMap<String, ExplicitTarget> selectedAgainstTargets,
			CollocationNgramAnalyzerBase analyzer,  boolean evaluateTrain) {
		this.selectedFavorTargets=selectedFavorTargets;
		this.selectedAgainstTargets= selectedAgainstTargets;
		this.analyzer= analyzer;
		this.evaluateTrain=evaluateTrain;
	}


	public Map<String, Double> ablationTest(boolean evaluateFavor) throws NumberFormatException, UIMAException, SQLException, TextClassificationException {
		Map<String, Double> result= new HashMap<>();
		if(evaluateFavor){
			for(String target:selectedFavorTargets.keySet()){
				result.put(selectedFavorTargets.get(target).getTargetName(), analyzer.analyze(getMapWithoutTarget(selectedFavorTargets,target),selectedAgainstTargets, 1, evaluateTrain).getMicroF());
			}
		}else{
			for(String target:selectedAgainstTargets.keySet()){
				result.put(selectedAgainstTargets.get(target).getTargetName(), analyzer.analyze(selectedFavorTargets,getMapWithoutTarget(selectedAgainstTargets,target), 1, evaluateTrain).getMicroF());
			}
			
		}
		return result;
	}


	private HashMap<String, ExplicitTarget> getMapWithoutTarget(HashMap<String, ExplicitTarget> selectedTargets,
			String target) {
		HashMap<String, ExplicitTarget> result= new HashMap<>();
		for(String targetToCompare:selectedTargets.keySet()){
			if(!target.equals(targetToCompare)){
				result.put(targetToCompare, selectedTargets.get(targetToCompare));
			}
		}
		return result;
	}


}
