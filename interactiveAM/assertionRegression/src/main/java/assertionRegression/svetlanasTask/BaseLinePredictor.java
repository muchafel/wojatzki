package assertionRegression.svetlanasTask;

public class BaseLinePredictor extends Predictor {

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception {
		return result(1.0,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize) throws Exception {
		return getPredictionForAssertion(assertion,experiment);
	}

	
	
	
}
