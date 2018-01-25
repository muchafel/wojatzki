package assertionRegression.svetlanasTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import assertionRegression.judgmentPrediction.SimilarityHelper;
import dkpro.similarity.algorithms.api.SimilarityException;

public class AssertionJudgmentSimilarityPredictor_averaged extends Predictor {
	
	
	 private SimilarityHelper similarityHelper;
	 private int numberOfAveragedAssertions;
	 
	 
	 public AssertionJudgmentSimilarityPredictor_averaged(int i) {
		 similarityHelper= new SimilarityHelper();
		 this.numberOfAveragedAssertions=i;
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception {
		double prediction= predictionOfMostSimilarAssertion(experiment.getNonZeroJudgments_toTest(),assertion,experiment);
//		System.out.println(prediction+" "+experiment.getNonZeroJudgments_toTest().get(assertion));
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	

	private double predictionOfMostSimilarAssertion(LinkedHashMap<String, Double> nonZeroJudgments_toTest, String assertion, PredictionExperiment experiment) {
		
		String mostSimilar="";
		double bestSimScore=0.0;
		System.out.println("--"+nonZeroJudgments_toTest.size());
		Map<String,Double> assertion2Sim= new LinkedHashMap<>();
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			double sim=0;
			try {
				sim = similarity(previousAssertion,assertion,experiment);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(Double.isNaN(sim)) {
				assertion2Sim.put(previousAssertion, -1.0);
			}else {
				assertion2Sim.put(previousAssertion, sim);
			}
			
		}
		assertion2Sim=sort(assertion2Sim);
		int j=0;
		double sum=0.0;
		
		
//		System.out.println("---");
//		for(String assertion_it: assertion2Sim.keySet()) {
//			System.out.println(assertion_it+" "+assertion2Sim.get(assertion_it));
//		}
		
		double avg=1.0;
		for(String assertion_it: assertion2Sim.keySet()) {
			j++;
			System.out.println(j);
			if(nonZeroJudgments_toTest.get(assertion_it)!= -1.0 && nonZeroJudgments_toTest.get(assertion_it)!=1.0) {
				System.out.println(nonZeroJudgments_toTest.get(assertion_it));
			}
			sum+=nonZeroJudgments_toTest.get(assertion_it);
//			System.out.println(assertion+"_"+assertion_it);
			if(j==numberOfAveragedAssertions) {
//				System.out.println(assertion+"->"+assertion_it+":"+sum+ " "+sum/i);
				avg= sum/(double)numberOfAveragedAssertions;
				break;
			}
		}
		if(avg>=0.5) {
			return 1.0;
		}else {
			return -1.0;
		}
	}
	
	
	private Map<String, Double> sort(Map<String, Double> assertion2Sim) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(assertion2Sim.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private double similarity(String previousAssertion, String assertion, PredictionExperiment experiment) throws Exception {
		double[] vectorA= experiment.getJudgments_other().getRatingsForAssertion(assertion);
		double[] vectorB= experiment.getJudgments_other().getRatingsForAssertion(previousAssertion);
		
//		System.out.println(Arrays.toString(vectorA));
//		System.out.println(Arrays.toString(vectorB));
		
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


	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize) throws Exception {
		LinkedHashMap<String,Double> subMap= getSubMap(historySize,experiment.getNonZeroJudgments_toTest());
		double prediction= predictionOfMostSimilarAssertion(subMap,assertion,experiment);
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
