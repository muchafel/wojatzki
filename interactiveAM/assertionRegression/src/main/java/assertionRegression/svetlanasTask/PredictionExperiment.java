package assertionRegression.svetlanasTask;

import java.util.HashMap;
import java.util.Map;

import assertionRegression.similarity.OpinionSummarizationData;

public class PredictionExperiment {
	private OpinionSummarizationData judgments_other;
	private Map<String, Double> judgments_toTest;
	private Map<String, Double> nonZeroJudgments_toTest;
	

	public PredictionExperiment(OpinionSummarizationData judgments_other, Map<String, Double> judgments_toTest) {
		this.judgments_other = judgments_other;
		this.judgments_toTest = judgments_toTest;
		this.nonZeroJudgments_toTest=calcNonZeroJudgments(judgments_toTest);
	}


	private Map<String, Double> calcNonZeroJudgments(Map<String, Double> judgments_toTest) {
		Map<String, Double> result=new HashMap<>();
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


	public Map<String, Double> getNonZeroJudgments_toTest() {
		return nonZeroJudgments_toTest;
	}



}
