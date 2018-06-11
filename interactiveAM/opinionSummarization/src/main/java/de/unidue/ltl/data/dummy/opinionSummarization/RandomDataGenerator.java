package de.unidue.ltl.data.dummy.opinionSummarization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.FileChooserUI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import de.unidue.ltl.core.data.opinionSummarization.CanabisParticipant;
import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;

public class RandomDataGenerator {

	private Random random;
	private int numberOfParticpants,  numberOfStatements;
	
	public RandomDataGenerator(Random random, int numberOfParticpants, int numberOfStatements) {
		this.random= random;
		this.numberOfParticpants=numberOfParticpants;
		this.numberOfStatements= numberOfStatements;
	}

	public OpinionSummarizationData generateOpinionSummarizationData() {
		List<Participant> participants=getRandomParticipants_Canabis(numberOfParticpants);
		List<String> statements=null;
		try {
			statements=getFirstNStatements(numberOfStatements);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double valueMatrix[][] = new double[participants.size()][statements.size()];
		int i=0;
		for(Participant p: participants){
			int j=0;
			for(String s: statements){
				valueMatrix[i][j]=randomValue();
				j++;
			}
			i++;
		}
		System.out.println(statements.size());
		return new OpinionSummarizationData(participants,statements,valueMatrix, null);
	}

	private double randomValue() {
		if(random.nextBoolean()){
			return 0.0;
		}
		return 1.0;
	}

	private List<String> getFirstNStatements(int numberofStatements) throws IOException {
		 List<String> result= new ArrayList<String>();
		int i =0;
		for (String line: FileUtils.readLines(new File("src/main/resources/crawl.txt"))){
			if(i>=numberofStatements)return result;
			result.add(line);
			i++;
		}
		return result;
	}

	private List<Participant> getRandomParticipants_Canabis(int numberOfParticpants2) {
		List<Participant> participants= new ArrayList<Participant>();
		for (int i = 0; i < numberOfParticpants; i++) {
			participants.add(getNewCanabisParticipant(i));
//			System.out.println(getNewCanabisParticipant(i).print());
		}
		return participants;
	}


	private Participant getNewCanabisParticipant(int i) {
		int genderInt=random.nextInt(3);
		int professionInt=random.nextInt(6);
		int age= random.nextInt(100);
		int otherInt=random.nextInt(5);
		return new CanabisParticipant(i, getGender(genderInt), age, getProfession(professionInt), random);
	}



	private String getProfession(int professionInt) {
		 switch(professionInt){ 
	        case 0: 
	        	return "student (highschool)"; 
	        case 1: 
	            return "student (university)"; 
	        case 2: 
	        	return "employee";  
	        case 3: 
	        	return "self-employed"; 
	        case 4: 
	        	return "unemployed/seeking employment"; 
	        case 5: 
	        	return "other"; 
	        default: 
	        	return "other";  
	        } 
	}

	private String getGender(int genderInt) {
		 switch(genderInt){ 
	        case 0: 
	        	return "female"; 
	        case 1: 
	            return "male"; 
	        case 2: 
	        	return "other";  
	        default: 
	        	return "other";  
	        } 
	}

}
