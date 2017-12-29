package assertionRegression.svetlanasTask;

import java.util.LinkedHashMap;

public class MeanHistoryPredictor extends Predictor {

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception {
		double prediction= meanHistory(experiment.getNonZeroJudgments_toTest());
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	private double meanHistory(LinkedHashMap<String, Double> previousJudgments) {
		double sum=0;
		for(String key: previousJudgments.keySet()) {
			sum+=previousJudgments.get(key);
		}
		if(sum/previousJudgments.size()>=0.0) {
			return 1.0;
		}else {
//			System.out.println("-1 "+previousJudgments.size());
			return -1.0;
		}
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize) throws Exception {
		LinkedHashMap<String,Double> subMap= getSubMap(historySize,experiment.getNonZeroJudgments_toTest());
		double prediction= meanHistory(subMap);
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));

	}
	
	private LinkedHashMap<String, Double> getSubMap(int historySize,
			LinkedHashMap<String, Double> nonZeroJudgments_toTest) {
		LinkedHashMap<String,Double> subMap = new LinkedHashMap<>();
		int i=0;
		for(String key: nonZeroJudgments_toTest.keySet()) {
			i++;
			if(i==historySize) {
				return subMap;
			}
			subMap.put(key, nonZeroJudgments_toTest.get(key));
		}
		
		
		return null;
	}

}
