package assertionRegression.svetlanasTask;

import java.util.LinkedHashMap;

public class MeanOtherPredictor extends Predictor {

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception {
		double prediction= meanPredictionOfOthers(assertion,experiment);
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));

	}

	private double meanPredictionOfOthers(String assertion,PredictionExperiment experiment) {
		double[] jugments = null;
		try {
			jugments = experiment.getJudgments_other().getRatingsForAssertion(assertion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double sum=0.0;
		double nonZero=0.0;
		for(double d: jugments) {
			if(d!=0.0) {
				sum+=d;
				nonZero++;
			}
		}
		if(sum/nonZero>=0.0) {
			return 1.0;
		}else {
			return -1.0;
		}
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize) throws Exception {
		return getPredictionForAssertion(assertion,experiment);

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
