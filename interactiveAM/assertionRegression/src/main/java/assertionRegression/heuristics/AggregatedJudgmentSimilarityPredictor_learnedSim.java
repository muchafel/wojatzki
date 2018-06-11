package assertionRegression.heuristics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.ibm.icu.impl.locale.XCldrStub.FileUtilities;

import assertionRegression.judgmentPrediction.SimilarityHelper;
import dkpro.similarity.algorithms.api.SimilarityException;

public class AggregatedJudgmentSimilarityPredictor_learnedSim {
	
	
	 private SimilarityHelper similarityHelper;
	 private Map<String,Double> similarityMapping;
	 
	 public AggregatedJudgmentSimilarityPredictor_learnedSim(String path, String labelPath) throws IOException {
		 similarityHelper= new SimilarityHelper();
		 List<String> orderedLabes= getLabes(labelPath);
		 similarityMapping= readMapping(path,orderedLabes);
//		 System.out.println(similarityMapping.size());
		 
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
//		System.out.println(id1+ " "+id2);
		
		String assertion1=orderedLabes.get(id1);
		String assertion2=orderedLabes.get(id2);
//		System.out.println(assertion1+ " "+assertion2);
		
		return assertion1+"_"+assertion2;
	}


	public double predictionOfMostSimilarAssertion(LinkedHashMap<String, Double> nonZeroJudgments_toTest, String assertion) throws Exception {
		
		String mostSimilar="";
		double bestSimScore=0.0;
//		System.out.println(givenAssertions);
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			
			//TODO proper tokenization
			String[] wordsB = previousAssertion.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			double sim=0;
			
				sim = similarity(previousAssertion,assertion);
//				System.out.println(previousAssertion+" - "+assertion);
//				System.out.println(sim);
			
			if(sim>=bestSimScore) {
				mostSimilar=previousAssertion;
				bestSimScore=sim;
			}
		}
//		if(bestSimScore<=0.0) {
//			System.err.println("1.0");
//			return 1.0;
//		}
//		System.out.println(assertion+" most similar "+mostSimilar+ " "+bestSimScore);
		return nonZeroJudgments_toTest.get(mostSimilar);
	}

	private double similarity(String previousAssertion, String assertion) throws Exception {
		String merge1= previousAssertion+"_"+assertion;
		String merge2= assertion+"_"+previousAssertion;
		if(similarityMapping.containsKey(merge1)) {
			return similarityMapping.get(merge1);
		}else if(similarityMapping.containsKey(merge2)) {
			return similarityMapping.get(merge2);
		}else {
//			System.err.println(merge1+" not in sim mapping");
			return 0.0;
			// throw new Exception(merge1+" not in sim mapping");
		}
	}

	private List<Double> toList(double[] vectorA) {
		List<Double> result= new ArrayList<>();
		for(double d: vectorA) {
			result.add(d);
		}
		return result;
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

	public Double predictionOfMostSimilarAssertions(LinkedHashMap<String, Double> nonZeroJudgments_toTest,
			String assertion, int i) throws Exception {
		Map<String,Double> assertion2Sim= new LinkedHashMap<>();
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			double sim=0;
			sim = similarity(previousAssertion,assertion);
			
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
		
		for(String assertion_it: assertion2Sim.keySet()) {
			j++;
			sum+=nonZeroJudgments_toTest.get(assertion_it);
//			System.out.println(assertion+"_"+assertion_it);
			if(j==i) {
//				System.out.println(assertion_it+":"+sum+ " "+sum/i);
				return sum/(double)i;
			}
		}
		return null;
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


	
	
}
