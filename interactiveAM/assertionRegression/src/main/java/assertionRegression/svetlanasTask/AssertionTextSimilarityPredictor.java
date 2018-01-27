package assertionRegression.svetlanasTask;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bytedeco.javacpp.RealSense.context;

import assertionRegression.judgmentPrediction.ParticipanJudgmentPredictionExperiment;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;

public class AssertionTextSimilarityPredictor extends Predictor {

	TextSimilarityMeasure measure;
	
	public AssertionTextSimilarityPredictor(TextSimilarityMeasure measure) {
		this.measure=measure;
	}
	
	
	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception {
		double prediction= predictionOfMostSimilarAssertion(experiment.getNonZeroJudgments_toTest(),assertion);
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	private double predictionOfMostSimilarAssertion(Map<String, Double> nonZeroJudgments_toTest, String assertion) {
		String[] wordsA = assertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		
		String mostSimilar="";
		double bestSimScore=0.0;
//		System.out.println(assertion+ " "+nonZeroJudgments_toTest.size());
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			
			//TODO proper tokenization
			String[] wordsB = previousAssertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			double sim = 0;
			try {
				sim = measure.getSimilarity(wordsA, wordsB);
//				System.out.println(measure.getSimilarity(wordsA, wordsB)+ " "+measure.getSimilarity(assertion, previousAssertion));

			} catch (SimilarityException e) {
				e.printStackTrace();
			}
//			System.out.println(sim);
			if(sim>=bestSimScore) {
				mostSimilar=previousAssertion;
				bestSimScore=sim;
			}
		}
		if(bestSimScore==0.0) return 1.0;
//		System.out.println(assertion+" <most similar> "+mostSimilar+ " "+bestSimScore);
		return nonZeroJudgments_toTest.get(mostSimilar);
	}


	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize) throws Exception {
		LinkedHashMap<String,Double> subMap= getSubMap(historySize,experiment.getNonZeroJudgments_toTest());
		double prediction= predictionOfMostSimilarAssertion(subMap,assertion);
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));

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
