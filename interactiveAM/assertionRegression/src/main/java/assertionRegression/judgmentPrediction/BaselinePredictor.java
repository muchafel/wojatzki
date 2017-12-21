package assertionRegression.judgmentPrediction;

import java.io.IOException;

public class BaselinePredictor extends NextJudgmentPredictor {


	@Override
	public double predict(int givenJudgments, boolean predcitOnlyNext,
			ParticipanJudgmentPredictionExperiment experiment) throws IOException {
		setUpPrediction(givenJudgments,experiment);

		if(experiment.getAssertionsToTest().size()==0) {
			return Double.NaN;
		}
		
		if(predcitOnlyNext) {
			String nextAssertion=experiment.getAssertionsToTest().get(0);
			double prediction=1.0;
			if(experiment.getAssertion2TrueScore().get(nextAssertion)==0.0) return Double.NaN;
			if(prediction==experiment.getAssertion2TrueScore().get(nextAssertion)) {
				return 1.0;
			}else {
				return 0.0;
			}
		}else {
			int correct=0;
			for(String assertion: experiment.getAssertionsToTest()) {
				double prediction=1.0;
				if(prediction==experiment.getAssertion2TrueScore().get(assertion)) {
					correct++;
				}
			}
//			System.out.println((double)correct/assertionsToTest.size());
			return (double)correct/experiment.getAssertionsToTest().size();
		}
	}
	

}
