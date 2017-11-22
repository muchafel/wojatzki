package assertionRegression.judgmentPrediction;

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
import java.util.Random;

import org.apache.commons.io.FileUtils;

import assertionRegression.similarity.OpinionSummarizationData;
import assertionRegression.similarity.Participant;
import lombok.val;

public class ParticipanJudgmentPredictionExperiment {
	private Participant participantToTest;
	private OpinionSummarizationData data;
	private Map<String,Double> assertion2TrueScore;
	private double[] judgmentsOfParticipant;
	private List<String> nonZeroGivenAssertions;
	private List<String> assertionsToTest;
	private static int numberOfGivenJudgments=3;
	private String path;
	private int numberOfParticipants;
	
	public ParticipanJudgmentPredictionExperiment(String path) throws IOException {
		this.path=path;
		this.numberOfParticipants=getNumberOfParticipants();
	}


	
	public int getNumberOfParticipants() throws IOException {
		return FileUtils.readLines(new File(this.path),"UTF-8").size();
	}
	

	public void setUpExperiment(int idToTest, List<Integer> order) throws IOException {
		List<Participant> participants= new ArrayList();
		List<String> assertions=null;
		String issue= getIssueFromPath(path);
		double[][] valueMatrixInput=  null;
		
		boolean firstLine=true;
		int i=0;
		int j=0;
		
		List<String> lines= FileUtils.readLines(new File(this.path),"UTF-8");
		
		for(String line: lines) {
			if(firstLine) {
				firstLine=false;
				assertions=getAssertionsFromLine(line);
				valueMatrixInput=new double[lines.size()-1][assertions.size()];
			}else {
				if(i==idToTest) {
					this.participantToTest=getParticipantFromLine(line,assertions.size());
					this.judgmentsOfParticipant=getJudgmentOfParticipant(line,new double[assertions.size()]);
				}else {
					participants.add(getParticipantFromLine(line,assertions.size()));
					double[] judgmentsOfParticipant=getJudgmentOfParticipant(line,new double[assertions.size()]);
					valueMatrixInput[j]=judgmentsOfParticipant;
					j++;
				}
				i++;
			}
		}
		this.data=new OpinionSummarizationData(participants, assertions, valueMatrixInput, issue);
		
//		try {
//			printExperiment(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println();
	}

	private void printExperiment(ParticipanJudgmentPredictionExperiment participanJudgmentPredictionExperiment) throws Exception {
		for(Participant p: data.getParticipants()) {
			System.out.println(p.getId()+ " "+data.getStringValuesForParticipant(p));
		}
		
	}



	private List<String> reorder(List<String> lines, List<Integer> order) {
		List<String> newLines= new ArrayList();
		String fistLine= lines.get(0);
		newLines.add(fistLine);
		lines.remove(0);
		for(int i: order) {
			newLines.add(lines.get(i));
		}
//		System.out.println(order);
		return newLines;
	}


	private static double[] getJudgmentOfParticipant(String line, double[] ds) {
		int i=0;
		for(String part: line.split("\t")){
			if(i>0 && i< ds.length) {
				ds[i-1]=Double.parseDouble(part);
			}
			i++;	
		}
		return ds;	}

	private static Participant getParticipantFromLine(String line, int i) {
		i=i-1;
		String[] parts= line.split("\t");
		Participant p= new Participant(Integer.parseInt(parts[0]));
		if(!parts[i+1].isEmpty()){
			p.setAge(Integer.parseInt(parts[i+1]));
			p.setAffiliation(parts[i+3]);
			p.setEducation(parts[i+4]);
			p.setFamilyStatus(parts[i+5]);
			p.setGender(parts[i+2]);
			p.setProfession(parts[i+6]);
			p.setRace(parts[i+7]);
			p.setReligion(parts[i+8]);
			p.setTies2overseas(parts[i+9]);
			p.setUSCitizen(parts[i+10]);
		}
		return p;
	}

	private static List<String> getAssertionsFromLine(String line) {
		
		List<String> assertions=new ArrayList();
		for(String part: line.split("\t")){
			if(!part.equals("participant id")) {
				assertions.add(part);
				if(part.equals("age")) {
					return assertions;
				}
			}
		}
		return null;
	}

	private static String getIssueFromPath(String path) {
		String[] parts= path.split("/");
		return parts[parts.length-1].split("\\.")[0];
	}

	public Map<Integer, List<Double>> runExperiment(List<Integer> order, int maxGivenJudgments,
			Map<Integer, List<Double>> resultsPermutation, NextJudgmentPredictor predictor,boolean predictOnlyNext) throws Exception {
		for (int j = 1; j <= maxGivenJudgments; j++) {
			double avg = 0.0;
			int validInstances = 0;
			for (int i = 0; i < numberOfParticipants-1 ; i++) {
				
				this.setUpExperiment(i, order);

				double score=predictor.predict(j, predictOnlyNext,this);
				
				if (!Double.isNaN(score)) {
					validInstances++;
					// System.out.println("\t"+score);
					avg += score;
				}
				// System.out.println(exp.predictBasedOnUserSimilarity(40));
				// System.out.println(avg);
			}
			System.out.println(j + "\t" + avg / validInstances + "\t" + validInstances);
			if (resultsPermutation.containsKey(j)) {
				resultsPermutation.get(j).add(avg / validInstances);
			} else {
				List<Double> scores = new ArrayList();
				scores.add(avg / validInstances);
				resultsPermutation.put(j, scores);
			}

		}
		return resultsPermutation;
	}

	public List<String> getNonZeroGivenAssertions() {
		return nonZeroGivenAssertions;
	}

	public void setNonZeroGivenAssertions(List<String> nonZeroGivenAssertions) {
		this.nonZeroGivenAssertions = nonZeroGivenAssertions;
	}

	public Participant getParticipantToTest() {
		return participantToTest;
	}

	public OpinionSummarizationData getData() {
		return data;
	}

	public Map<String, Double> getAssertion2TrueScore() {
		return assertion2TrueScore;
	}

	public double[] getJudgmentsOfParticipant() {
		return judgmentsOfParticipant;
	}

	public List<String> getAssertionsToTest() {
		return assertionsToTest;
	}

	public String getPath() {
		return path;
	}

	public void setParticipantToTest(Participant participantToTest) {
		this.participantToTest = participantToTest;
	}

	public void setAssertion2TrueScore(Map<String, Double> assertion2TrueScore) {
		this.assertion2TrueScore = assertion2TrueScore;
	}
	

	public void setAssertionsToTest(List<String> assertionsToTest) {
		this.assertionsToTest = assertionsToTest;
	}
}
