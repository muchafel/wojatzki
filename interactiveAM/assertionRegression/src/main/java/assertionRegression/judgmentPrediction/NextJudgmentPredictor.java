package assertionRegression.judgmentPrediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class NextJudgmentPredictor {

	public abstract double predict(int numberOfGivenJudgments, boolean predcitOnlyNext, ParticipanJudgmentPredictionExperiment experiment) throws Exception ;
	public void setUpPrediction(int givenJudgments, ParticipanJudgmentPredictionExperiment experiment) {
//		System.out.println("predictions for "+participantToTest.print());
//		System.out.println("judgments "+Arrays.toString(judgmentsOfParticipant));
		experiment.setAssertion2TrueScore(new HashMap());
		experiment.setNonZeroGivenAssertions(new ArrayList<>());
		experiment.setAssertionsToTest(new ArrayList<>());
		int counterA=0;
		int counterB=0;
		for(double d: experiment.getJudgmentsOfParticipant()) {
//			if(d!=0.0) {
				if(counterA>=givenJudgments) {
					experiment.getAssertionsToTest().add(experiment.getData().getStatements().get(counterB));
				}else {
					experiment.getNonZeroGivenAssertions().add(experiment.getData().getStatements().get(counterB));
				}
				counterA++;
//			}
			experiment.getAssertion2TrueScore().put(experiment.getData().getStatements().get(counterB), d);
			counterB++;
		}
//		System.out.println("Given: "+experiment.getNonZeroGivenAssertions());
//		System.out.println("ToTest: "+experiment.getAssertionsToTest());
	}
}
