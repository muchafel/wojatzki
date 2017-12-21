package assertionRegression.svetlanasTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import assertionRegression.judgmentPrediction.SimilarityHelper;
import dkpro.similarity.algorithms.api.SimilarityException;

public class AssertionJudgmentSimilarityPredictor extends Predictor {
	
	
	 private SimilarityHelper similarityHelper;
	 
	 public AssertionJudgmentSimilarityPredictor() {
		 similarityHelper= new SimilarityHelper();
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) {
		double prediction= predictionOfMostSimilarAssertion(experiment.getNonZeroJudgments_toTest(),assertion,experiment);
//		System.out.println(prediction+" "+experiment.getNonZeroJudgments_toTest().get(assertion));
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	

	private double predictionOfMostSimilarAssertion(Map<String, Double> nonZeroJudgments_toTest, String assertion, PredictionExperiment experiment) {
		
		String mostSimilar="";
		double bestSimScore=0.0;
//		System.out.println(givenAssertions);
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			
			//TODO proper tokenization
			String[] wordsB = previousAssertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			double sim=0;
			try {
				sim = similarity(previousAssertion,assertion,experiment);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(sim>=bestSimScore) {
				mostSimilar=previousAssertion;
				bestSimScore=sim;
			}
		}
//		System.out.println(assertion+" most similar "+mostSimilar+ " "+bestSimScore);
		return nonZeroJudgments_toTest.get(mostSimilar);
	}

	private double similarity(String previousAssertion, String assertion, PredictionExperiment experiment) throws Exception {
		double[] vectorA= experiment.getJudgments_other().getRatingsForAssertion(previousAssertion);
		double[] vectorB= experiment.getJudgments_other().getRatingsForAssertion(previousAssertion);
		
		List<Double> listA = toList(vectorA);
		List<Double> listB = toList(vectorB);
		
		return similarityHelper.getCosineSimilarity(listA,listB);
	}

	private List<Double> toList(double[] vectorA) {
		List<Double> result= new ArrayList<>();
		for(double d: vectorA) {
			result.add(d);
		}
		return result;
	}

}
