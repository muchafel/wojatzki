package de.unidue.ltl.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.util.MathUtils;

import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;
import de.unidue.ltl.util.SimilarityHelper;

public class DecisionTree {

	public static void main(String[] args) throws Exception {
		
//		OpinionSummarizationData data= getData("src/main/resources/matricesForTSNE/Black Lives Matter.tsv");
//		OpinionSummarizationData data= getData("src/main/resources/matricesForTSNE/Climate Change.tsv");
		OpinionSummarizationData data= getData("src/main/resources/matricesForTSNE/Gun Rights.tsv");
		System.out.println(data.getStatements());
		
		mostDivisiveAssertion(data,data.getParticipants(),0);
		

	}

	private static void mostDivisiveAssertion(OpinionSummarizationData data, List<Participant> participants, int i) throws Exception {
		if(i==3) return;
		
		List<String> assertions=data.getStatements();
		Map<String,Double> assertion2Score= new TreeMap<>();
		
		double bestScore=0;
		List<Participant> bestSplit_agreeingParticipants= new ArrayList<>();
		List<Participant> bestSplit_disAgreeingParticipants= new ArrayList<>();
		
		for(String assertion:data.getStatements() ) {
			
			List<Participant> agreeingParticipants= new ArrayList<>();
			List<Participant> disAgreeingParticipants= new ArrayList<>();
			
			for(Participant p: participants) {
				double value=data.getValue(p.getId(), assertion);
				if(value==1.0) {
					agreeingParticipants.add(p);
				}else if(value==-1.0) {
					disAgreeingParticipants.add(p);
				}
			}
			
//			int pIndex=0;
//			for(double value:data.getRatingsForAssertion(assertion)) {
//				if(value==1.0) {
//					agreeingParticipants.add(data.getParticipants().get(pIndex));
//				}else if(value==-1.0) {
//					disAgreeingParticipants.add(data.getParticipants().get(pIndex));
//				}
//				pIndex++;
//			}
			double[] centroidVectorAgree=getCentroidVector(agreeingParticipants,data);
			double[] centroidVectorDisAgree=getCentroidVector(disAgreeingParticipants,data);
			
			if(centroidVectorAgree==null || centroidVectorDisAgree==null) {
				continue;
			}
			
			double meanCosineAgree=meanCosine(centroidVectorAgree,agreeingParticipants,data);
			double meanCosineDisAgree=meanCosine(centroidVectorDisAgree,disAgreeingParticipants,data);
					
//			System.out.println(assertion+ " "+meanCosineAgree+" "+Arrays.toString(centroidVectorAgree));
//			System.out.println(assertion+ " "+meanCosineDisAgree+" "+Arrays.toString(centroidVectorDisAgree));
//			System.out.println(SimilarityHelper.getCosineSimilarity(centroidVectorAgree, centroidVectorDisAgree, false));
			double distance=1-SimilarityHelper.getCosineSimilarity(centroidVectorAgree, centroidVectorDisAgree, false);
			double sizeFactorAgree=(double)agreeingParticipants.size()/(double)data.getParticipants().size();
			double sizeFactorDisAgree=(double)disAgreeingParticipants.size()/(double)data.getParticipants().size();
			
			double score= meanCosineAgree*meanCosineDisAgree*distance*sizeFactorAgree*sizeFactorDisAgree;
//			double score= meanCosineAgree*meanCosineDisAgree*distance;
			if(score>bestScore) {
				bestScore=score;
				bestSplit_agreeingParticipants=agreeingParticipants;
				bestSplit_disAgreeingParticipants=disAgreeingParticipants;
			}
		
			
//			System.out.println(assertion+ " "+score);
			assertion2Score.put(assertion, score);
		}
		
		assertion2Score=sortByValue(assertion2Score);
//		for(String assertion: assertion2Score.keySet()) {
//			System.out.println(assertion+ " "+assertion2Score.get(assertion));
//		}
		Iterator<String> iter=assertion2Score.keySet().iterator();
		System.out.println("best split level "+i+" :"+iter.next());
		System.out.println("2nd best split level "+i+" :"+iter.next());
		System.out.println("3rd best split level "+i+" :"+iter.next());
		System.out.println("4rd best split level "+i+" :"+iter.next());
		System.out.println("5rd best split level "+i+" :"+iter.next());
		
		System.out.println(bestSplit_agreeingParticipants.size()+" "+bestSplit_disAgreeingParticipants.size());
		mostDivisiveAssertion(data,bestSplit_agreeingParticipants,i+1);
		mostDivisiveAssertion(data,bestSplit_disAgreeingParticipants,i+1);
		
		
	}

	private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

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
	
	
	private static double meanCosine(double[] centroidVectorAgree, List<Participant> agreeingParticipants, OpinionSummarizationData data) throws Exception {
		double mean=0.0;
		for(Participant p: agreeingParticipants) {
			
			mean+=SimilarityHelper.getCosineSimilarity(centroidVectorAgree, data.getRatingsOfParticipant(p), false);
		}
		return mean/agreeingParticipants.size();
	}

	private static double[] getCentroidVector(List<Participant> participants, OpinionSummarizationData data) throws Exception {
		List<double[]>toAverage= new ArrayList<>();
		for(Participant p: participants) {
			toAverage.add(data.getRatingsOfParticipant(p));
		}
		return average(toAverage);
	}


	private static double[] average(List<double[]> toAverage) {
		if(toAverage.isEmpty())return null;
		
		double[] averaged= new double[toAverage.get(0).length];
		//for each dimension
		for(int i=0; i<toAverage.get(0).length;i++) {
			double dimensionValue = 0;
			//for each participant 
			for(int j=0; j<toAverage.size();j++) {
				dimensionValue+=toAverage.get(j)[i];
			}
			dimensionValue=dimensionValue/toAverage.size();
			averaged[i]=dimensionValue;
		}
		return averaged;
	}

	private static double mean(double[] m) {
		double sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum / m.length;
	}

	private static OpinionSummarizationData getData(String string) throws IOException {
		List<String> lines= FileUtils.readLines(new File(string),"UTF-8");
		boolean firstLine= true;
		double valueMatrix[][] = null;
		List<String> assertions = null;
		List<Participant> participants = new ArrayList<>();
		int i=0;
		for(String line: lines) {
			if(firstLine) {
				assertions=Arrays.asList(Arrays.copyOfRange(line.split("\t"), 1, 147));
				valueMatrix = new double[lines.size()][assertions.size()];
				firstLine=false;
			}else {
				String[] assertionValues=line.split("\t");
//				if(assertionValues[138] != null) {
//					Participant p = getParticipant(Arrays.copyOfRange(line.split("\t"), 136, 149));
//				}else {
//					Participant p=new Participant(Integer.valueOf(assertionValues[0]));
//				}
				Participant p=new Participant(Integer.valueOf(assertionValues[0]));
				participants.add(p);
				
				int j=0;
//				System.out.println(Arrays.asList(Arrays.copyOfRange(line.split("\t"), 136, 149)));
				for(String s: Arrays.copyOfRange(line.split("\t"), 1, 147)){
//					System.out.println(s);
					valueMatrix[i][j]=Double.parseDouble(s);
					j++;
				}
				i++;
			}
			
		}
		 return new OpinionSummarizationData(participants,assertions,valueMatrix, null);
	}

	private static Participant getParticipant(String[] pString) {
		// TODO Auto-generated method stub
		return null;
	}

}
