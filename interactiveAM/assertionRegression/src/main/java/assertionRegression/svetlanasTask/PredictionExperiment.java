package assertionRegression.svetlanasTask;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import assertionRegression.similarity.OpinionSummarizationData;

public class PredictionExperiment {
	private OpinionSummarizationData judgments_other;
	private LinkedHashMap<String, Double> judgments_toTest;
	private LinkedHashMap<String, Double> nonZeroJudgments_toTest;
	

	public PredictionExperiment(OpinionSummarizationData judgments_other, LinkedHashMap<String, Double> judgments_toTest) {
		this.judgments_other = judgments_other;
		this.judgments_toTest = judgments_toTest;
		this.nonZeroJudgments_toTest=calcNonZeroJudgments(judgments_toTest);
	}


	private LinkedHashMap<String, Double> calcNonZeroJudgments(LinkedHashMap<String, Double> judgments_toTest) {
		LinkedHashMap<String, Double> result=new LinkedHashMap<>();
		for(String assertion: judgments_toTest.keySet()) {
			if(judgments_toTest.get(assertion)!=0.0) {
				result.put(assertion, judgments_toTest.get(assertion));
			}
		}
		return result;
	}


	public OpinionSummarizationData getJudgments_other() {
		return judgments_other;
	}


	public Map<String, Double> getJudgments_toTest() {
		return judgments_toTest;
	}


	public LinkedHashMap<String, Double> getNonZeroJudgments_toTest() {
		return nonZeroJudgments_toTest;
	}



}
