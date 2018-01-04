package assertionRegression.svetlanasTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.ibm.icu.impl.locale.XCldrStub.FileUtilities;

import assertionRegression.judgmentPrediction.SimilarityHelper;
import dkpro.similarity.algorithms.api.SimilarityException;

public class AssertionJudgmentSimilarityPredictor_learnedSim extends Predictor {
	
	
	 private SimilarityHelper similarityHelper;
	 private Map<String,Double> similarityMapping;
	 
	 public AssertionJudgmentSimilarityPredictor_learnedSim(String path, String labelPath) throws IOException {
		 similarityHelper= new SimilarityHelper();
		 List<String> orderedLabes= getLabes(labelPath);
		 similarityMapping= readMapping(path,orderedLabes);
		 
	}

	private List<String> getLabes(String labelPath) throws IOException {
		List<String> result= new ArrayList<String>();
		for(String line: FileUtils.readLines(new File(labelPath))) {
			//only process first line
			for(String part: line.split("\t")) {
				if(!part.isEmpty()) {
					result.add(part);
				}
			}
			 return result;
		}
		return null;
	}

	private Map<String,Double> readMapping(String path, List<String> orderedLabes) throws IOException {
		Map<String,Double> result= new HashMap<String, Double>();
		for(String line: FileUtils.readLines(new File(path))) {
			
			String[] parts= line.split(";")[0].split("=");
			String labelCombination=resolveLabes(parts[0],orderedLabes);
			result.put(labelCombination, Double.valueOf(parts[1]));
		}
		return result;
	}

	private String resolveLabes(String string, List<String> orderedLabes) {
		int id1= Integer.valueOf(string.split("_")[0]);
		int id2= Integer.valueOf(string.split("_")[1]);
		
		String assertion1=orderedLabes.get(id1);
		String assertion2=orderedLabes.get(id2);
		
		return assertion1+"_"+assertion2;
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
//		System.out.println(givenAssertions);
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			
			//TODO proper tokenization
			String[] wordsB = previousAssertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			double sim=0;
			try {
				sim = similarity(previousAssertion,assertion,experiment);
//				System.out.println(previousAssertion+" - "+assertion);
//				System.out.println(sim);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(sim>=bestSimScore) {
				mostSimilar=previousAssertion;
				bestSimScore=sim;
			}
		}
		if(bestSimScore<=0.0) return 1.0;
//		System.out.println(assertion+" most similar "+mostSimilar+ " "+bestSimScore);
		return nonZeroJudgments_toTest.get(mostSimilar);
	}

	private double similarity(String previousAssertion, String assertion, PredictionExperiment experiment) throws Exception {
		String merge1= previousAssertion+"_"+assertion;
		String merge2= assertion+"_"+previousAssertion;
		if(similarityMapping.containsKey(merge1)) {
			return similarityMapping.get(merge1);
		}else if(similarityMapping.containsKey(merge2)) {
			return similarityMapping.get(merge2);
		}else {
			throw new Exception(merge1+" not in sim mapping");
		}
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
