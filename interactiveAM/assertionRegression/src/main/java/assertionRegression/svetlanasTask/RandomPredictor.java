package assertionRegression.svetlanasTask;

import java.util.Random;


public class RandomPredictor extends Predictor {

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) {
		Random random = new Random();
		double prediction;
		if(random.nextBoolean()) {
			prediction= 1.0;
		}else {
			prediction= -1.0;
		}		
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

}
