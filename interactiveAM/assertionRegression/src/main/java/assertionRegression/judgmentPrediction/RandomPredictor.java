package assertionRegression.judgmentPrediction;

import java.util.List;
import java.util.Random;

public class RandomPredictor extends NextJudgmentPredictor {

	@Override
	public double predict(int numberOfGivenJudgments, boolean predcitOnlyNext,
			ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		setUpPrediction(numberOfGivenJudgments,experiment);
		
		if(experiment.getAssertionsToTest().size()==0) {
//			System.err.println("no assertions to test left");
			return Double.NaN;
		}
		Random random= new Random();
		if(predcitOnlyNext) {
//			System.out.println(experiment.getAssertionsToTest());
			String nextAssertion=experiment.getAssertionsToTest().get(0);
//			System.out.println("toTest "+experiment.getAssertionsToTest());
//			System.out.println("next in data "+experiment.getData().getStatements().get(0));
			double prediction = 1.0;
			if(random.nextBoolean()) {
				prediction=-1.0;
			}
			if(experiment.getAssertion2TrueScore().get(nextAssertion)==0.0) return Double.NaN;
			System.out.println(experiment.getParticipantToTest().getId()+" "+nextAssertion+" "+prediction+ " "+experiment.getAssertion2TrueScore().get(nextAssertion));
			if(prediction==experiment.getAssertion2TrueScore().get(nextAssertion)) {
				return 1.0;
			}else {
				return 0.0;
			}
		}else {
			int correct=0;
			for(String assertion: experiment.getAssertionsToTest()) {
				double prediction = 1.0;
				if(random.nextBoolean()) {
					prediction=-1.0;
				}
				if(prediction==experiment.getAssertion2TrueScore().get(assertion)) {
					correct++;
				}
			}
			System.out.println(correct+" "+experiment.getAssertionsToTest().size());
			return (double)correct/experiment.getAssertionsToTest().size();
		}
	}

}
