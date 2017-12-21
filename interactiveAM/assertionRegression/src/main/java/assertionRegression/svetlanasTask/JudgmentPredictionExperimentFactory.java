package assertionRegression.svetlanasTask;

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

public class JudgmentPredictionExperimentFactory {
	private String path;
	private List<String> assertions;
	private List<Participant> participants;
	
	public JudgmentPredictionExperimentFactory(String path) throws IOException {
		this.path=path;
		this.assertions=getAssertionsFromFile();
		this.participants=getParticipantsFromFile();
	}





	private List<Participant> getParticipantsFromFile() throws IOException {
		List<Participant> participants = new ArrayList();

		boolean firstLine = true;
		List<String> lines = FileUtils.readLines(new File(this.path), "UTF-8");

		for (String line : lines) {
			if (firstLine) {
				firstLine = false;
			} else {
				participants.add(getParticipantFromLine(line, assertions.size()));
			}
		}
		return participants;
	}

	

	public PredictionExperiment setUpExperiment(int idToTest) throws IOException {
		List<Participant> other_participants= new ArrayList();
		Map<String,Double> judgments_toTest =null;
		
		//here we could introduce some ordering/exlsuioon of assertions etc....
		List<String> assertions=this.assertions;
		String issue= getIssueFromPath(path);
		double[][] valueMatrixInput=  null;
		
		boolean firstLine=true;
		int i=0;
		int j=0;
		
		List<String> lines= FileUtils.readLines(new File(this.path),"UTF-8");
		
		for(String line: lines) {
			if(firstLine) {
				firstLine=false;
				valueMatrixInput=new double[lines.size()-1][assertions.size()];
			}else {
				
				int idFromLine=getIdFromLine(line);
				
				if(idFromLine==idToTest) {
					judgments_toTest= getJudgmentMappingOfParticipant(line,new double[assertions.size()]);
				}else {
					other_participants.add(getParticipantFromLine(line,assertions.size()));
					double[] judgmentsOfOtherParticipant=getJudgmentOfParticipant(line,new double[assertions.size()]);
					valueMatrixInput[j]=judgmentsOfOtherParticipant;
					j++;
				}
				i++;
			}
		}
		
		OpinionSummarizationData judgments_other=new OpinionSummarizationData(other_participants, assertions, valueMatrixInput, issue);
		PredictionExperiment experiment= new PredictionExperiment(judgments_other,judgments_toTest);
		return experiment;
	}

	private int getIdFromLine(String line) {
		String[] parts= line.split("\t");
		return Integer.parseInt(parts[0]);
	}





	private double[] reorder(double[] judgmentsOfParticipant, List<Integer> columnOrder) {
		
		double[] result= new double[judgmentsOfParticipant.length];
		
		for(int i =0; i< judgmentsOfParticipant.length; i++) {
			result[i]=judgmentsOfParticipant[columnOrder.get(i)];
		}
		return result;
	}






	private List<String> reorder(List<String> lines, List<Integer> order) {
		List<String> newLines= new ArrayList();
		for(int i: order) {
			newLines.add(lines.get(i));
		}
//		System.out.println(newLines);
		return newLines;
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
	
	
	private Map<String,Double> getJudgmentMappingOfParticipant(String line, double[] ds) {
		Map<String,Double> assertionToJudgment= new LinkedHashMap<String,Double>();
		
		int i=0;
		for(String part: line.split("\t")){
			if(i>0 && i< ds.length) {
				assertionToJudgment.put(assertions.get(i-1), Double.parseDouble(part));
			}
			i++;	
		}
		return assertionToJudgment;	
	}

	private static Participant getParticipantFromLine(String line, int i) {
		i=i+1;
		String[] parts= line.split("\t");
		Participant p= new Participant(Integer.parseInt(parts[0]));
		
		if(parts.length>i+1 && !parts[i+1].isEmpty() ){
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

	private List<String> getAssertionsFromLine(String line) {
		
		List<String> assertions=new ArrayList();
		for(String part: line.split("\t")){
			if(!part.equals("participant id")) {
				
				if(part.equals("age")) {
					return assertions;
				}
				assertions.add(part);
			}
		}
		return null;
	}

	private String getIssueFromPath(String path) {
		String[] parts= path.split("/");
		return parts[parts.length-1].split("\\.")[0];
	}





	private List<String> getAssertionsFromFile() throws IOException {
		List<String> assertions=null;
		List<String> lines= FileUtils.readLines(new File(this.path),"UTF-8");
		String firstLine= lines.get(0);
		return getAssertionsFromLine(firstLine);
		
	}



	public List<String> getAssertions() {
		return assertions;
	}


	public List<Participant> getParticipants() {
		return participants;
	}







}
