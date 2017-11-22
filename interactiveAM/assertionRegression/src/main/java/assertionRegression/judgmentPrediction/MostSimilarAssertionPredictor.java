package assertionRegression.judgmentPrediction;

import java.util.Arrays;
import java.util.List;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;

public class MostSimilarAssertionPredictor extends NextJudgmentPredictor {

	TextSimilarityMeasure measure;
	
	public MostSimilarAssertionPredictor(TextSimilarityMeasure measure) {
		this.measure=measure;
	}
	
	@Override
	public double predict(int numberOfGivenJudgments, boolean predcitOnlyNext,
			ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		setUpPrediction(numberOfGivenJudgments, experiment);

		if (experiment.getAssertionsToTest().size() == 0) {
			return Double.NaN;
		}
		List<String> givenAssertions=experiment.getNonZeroGivenAssertions();

		if(predcitOnlyNext) {
//			System.out.println(experiment.getAssertionsToTest());
			String nextAssertion=experiment.getAssertionsToTest().get(0);
			double prediction = predictionOfMostSimilarAssertion(givenAssertions,nextAssertion,experiment,measure);
			if(prediction==experiment.getAssertion2TrueScore().get(nextAssertion)) {
				return 1.0;
			}else {
				return 0.0;
			}
		}else {
			int correct=0;
			for(String assertion: experiment.getAssertionsToTest()) {
				double prediction = predictionOfMostSimilarAssertion(givenAssertions,assertion,experiment,measure);
				if(prediction==experiment.getAssertion2TrueScore().get(assertion)) {
					correct++;
				}
			}
			System.out.println(correct+" "+experiment.getAssertionsToTest().size());
			return (double)correct/experiment.getAssertionsToTest().size();
		}
		
	}
	
	private double predictionOfMostSimilarAssertion(List<String> givenAssertions, String nextAssertion,
			ParticipanJudgmentPredictionExperiment experiment, TextSimilarityMeasure measure) throws SimilarityException {
		
		String[] wordsA = nextAssertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
//		System.out.println(Arrays.toString(wordsA));
		String mostSimilar="";
		double bestSimScore=0.0;
//		System.out.println(givenAssertions);
		for(String assertion:givenAssertions) {
			//TODO proper tokenization
			String[] wordsB = assertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			double sim =  measure.getSimilarity(wordsA, wordsB);
//			System.out.println(sim);
			if(sim>=bestSimScore) {
				mostSimilar=assertion;
				bestSimScore=sim;
			}
		}
//		System.out.println(bestSimScore);
		return experiment.getAssertion2TrueScore().get(mostSimilar);
	}

}
