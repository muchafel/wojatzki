package assertionRegression.judgmentPrediction;

import java.util.List;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;

public class MeanUserPredictor extends NextJudgmentPredictor {

	@Override
	public double predict(int numberOfGivenJudgments, boolean predcitOnlyNext,
			ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		setUpPrediction(numberOfGivenJudgments, experiment);

		if (experiment.getAssertionsToTest().size() == 0) {
			return Double.NaN;
		}

		if(predcitOnlyNext) {
//			System.out.println(experiment.getAssertionsToTest());
			String nextAssertion=experiment.getAssertionsToTest().get(0);
			double prediction = meanUserPrediction(nextAssertion,experiment);
			if(prediction==experiment.getAssertion2TrueScore().get(nextAssertion)) {
				return 1.0;
			}else {
				return 0.0;
			}
		}else {
			int correct=0;
			for(String assertion: experiment.getAssertionsToTest()) {
				double prediction = meanUserPrediction(assertion,experiment);
				if(prediction==experiment.getAssertion2TrueScore().get(assertion)) {
					correct++;
				}
			}
			System.out.println(correct+" "+experiment.getAssertionsToTest().size());
			return (double)correct/experiment.getAssertionsToTest().size();
		}
	}

	private double meanUserPrediction(String nextAssertion, ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		double avg= avg(experiment.getData().getRatingsForAssertion(nextAssertion));
		if(avg>0) {
			return 1.0;
		}else {
			return -1.0;
		}
	}
	
	private double avg(double[] list) {
		double score = 0.0;
		for (double d : list) {
			score += d;
		}
		return score / list.length;
	}

}
