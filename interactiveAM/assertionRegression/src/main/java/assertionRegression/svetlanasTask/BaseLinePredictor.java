package assertionRegression.svetlanasTask;

public class BaseLinePredictor extends Predictor {

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) {
		return result(1.0,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	
	
	
}
