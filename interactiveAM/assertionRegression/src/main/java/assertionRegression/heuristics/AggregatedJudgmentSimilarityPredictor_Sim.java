package assertionRegression.heuristics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import assertionRegression.similarity.OpinionSummarizationData;
import assertionRegression.similarity.Participant;
import assertionRegression.svetlanasTask.PredictionExperiment;
import assertionRegression.util.SimilarityHelper;

public class AggregatedJudgmentSimilarityPredictor_Sim {
	private SimilarityHelper similarityHelper;
	 private Map<String,Double> similarityMapping;
	 private OpinionSummarizationData data;
	 
	 
	 public AggregatedJudgmentSimilarityPredictor_Sim(String path,List<String> assertions, String issue) throws IOException {
		 similarityHelper= new SimilarityHelper();
		 List<Participant> participants= new ArrayList<>();
		 boolean firstLine=true;
		 double[][] valueMatrixInput=  null;
		 int j=0;
		 List<String> lines= FileUtils.readLines(new File(path),"UTF-8");
			
			for(String line: lines) {
				if(firstLine) {
					firstLine=false;
					valueMatrixInput=new double[lines.size()-1][assertions.size()];
				}else {
					
					int idFromLine=getIdFromLine(line);
						participants.add(getParticipantFromLine(line,assertions.size()));
						double[] judgmentsOfOtherParticipant=getJudgmentOfParticipant(line,new double[assertions.size()]);
						valueMatrixInput[j]=judgmentsOfOtherParticipant;
						j++;
				}
		 
		 this.data= new OpinionSummarizationData(participants, assertions, valueMatrixInput, issue);
		}	 
	}

	
	
	
	private int getIdFromLine(String line) {
		String[] parts= line.split("\t");
		return Integer.parseInt(parts[0]);
	}


	private double[] getJudgmentOfParticipant(String line, double[] ds) {
		int i=0;
		for(String part: line.split("\t")){
			if(i>0 && i< ds.length) {
				ds[i-1]=Double.parseDouble(part);
			}
			i++;	
		}
		return ds;	
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
		double[] vectorA= getVector(assertion);
		double[] vectorB= getVector(previousAssertion);
		
//		System.out.println(Arrays.toString(vectorA));
//		System.out.println(Arrays.toString(vectorB));
		
		List<Double> listA = toList(vectorA);
		List<Double> listB = toList(vectorB);
		
		return similarityHelper.getCosineSimilarity(listA,listB);
	}

	private double[] getVector(String assertion) throws Exception {
		// TODO Auto-generated method stub
		return data.getRatingsForAssertion(assertion);
	}

	private List<Double> toList(double[] vectorA) {
		List<Double> result= new ArrayList<>();
		for(double d: vectorA) {
			result.add(d);
		}
		return result;
	}


	public Double predictionOfMostSimilarAssertions(LinkedHashMap<String, Double> nonZeroJudgments_toTest,
			String assertion, int i) throws Exception {
		Map<String,Double> assertion2Sim= new LinkedHashMap<>();
		for(String previousAssertion:nonZeroJudgments_toTest.keySet()) {
			
			if(previousAssertion.equals(assertion))continue;
			double sim=0;
			sim = similarity(previousAssertion,assertion);
			if(Double.isNaN(sim)) {
//				System.out.println(previousAssertion+" "+assertion);
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
//				System.out.println(assertion+"->"+assertion_it+":"+sum+ " "+sum/i);
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

	private static Participant getParticipantFromLine(String line, int i) {
		i=i+1;
		String[] parts= line.split("\t");
//		System.out.println(parts[0]);
		Participant p= new Participant(Integer.parseInt(parts[0]));
		
//		if(parts.length>i+1 && !parts[i+1].isEmpty() ){
//			p.setAge(Integer.parseInt(parts[i+1]));
//			p.setAffiliation(parts[i+3]);
//			p.setEducation(parts[i+4]);
//			p.setFamilyStatus(parts[i+5]);
//			p.setGender(parts[i+2]);
//			p.setProfession(parts[i+6]);
//			p.setRace(parts[i+7]);
//			p.setReligion(parts[i+8]);
//			p.setTies2overseas(parts[i+9]);
//			p.setUSCitizen(parts[i+10]);
//		}
		return p;
	}
	
}
