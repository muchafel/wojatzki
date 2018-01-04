package de.unidue.ltl.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;

public class DataBuilder {
	private List<List<Integer>> adData = new ArrayList<List<Integer>>();
	private Map<Integer,Map<String,Integer>> best = new HashMap<Integer,Map<String,Integer>>();
	private Map<Integer, Map<String, Integer>> worst = new HashMap<Integer,Map<String,Integer>>();
	private List<Demographics> demographics = new ArrayList<>();
	private List<Participant> participants= new ArrayList<>();
	private List<String> assertions;
	private Map<String,Integer> assertion2Id;
	private String issue;
	
	public DataBuilder(String issue) {
		this.issue=issue;
	}
	public List<List<Integer>> getAdData() {
		return adData;
	}
	public void setAdData(List<List<Integer>> adData) {
		this.adData = adData;
	}
	public Map<Integer,Map<String,Integer>> getBestData() {
		return best;
	}
	public void setBestData(Map<Integer,Map<String,Integer>> best) {
		this.best = best;
	}
	public List<Demographics> getDemographics() {
		return demographics;
	}
	public void setDemographics(List<Demographics> demographics) {
		this.demographics = demographics;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public OpinionSummarizationData createData() {
		double[][] valueMatrixRaw= new double[participants.size()][assertions.size()];
		int i=0;
		for(Participant p: participants){
			int j=0;
			for(String s: assertions){
				valueMatrixRaw[i][j]=(double)adData.get(i).get(j);
				j++;
			}
			i++;
		}
		return new OpinionSummarizationData(participants, assertions, valueMatrixRaw,issue,best,worst,assertion2Id);
	}
	public void addDemograhic(Demographics demographics, int id) {
		if(demographics != null){
			this.participants.add(new Participant(demographics.getId(), demographics.getGender(), demographics.getAge(),
					demographics.getProfession(), demographics.getAffiliation(), demographics.getEductaion(),
					demographics.getFamilyStauts(), demographics.getRace(), demographics.getReligion(),
					demographics.getTies2Overseas(), demographics.getUsCitizen()));
		}else{
			participants.add(new Participant(id));
		}
		
	}
	public List<String> getAssertions() {
		return assertions;
	}
	public void setAssertions(List<String> assertions,Map<String,Integer> assertion2Id) {
		this.assertions = assertions;
		this.assertion2Id=assertion2Id;
		int i=assertion2Id.size()+1;
		for(String assertion: assertions){
			if(!assertion2Id.containsKey(assertion+"\t"+issue)){
				assertion2Id.put(assertion+"\t"+issue, i++);
			}
		}
	}
	public void addBWS(Map<String, Integer> best2, Map<String, Integer> worst2, int id) {
		this.best.put(id,best2);
		this.worst.put(id,worst2);
	}
	public Map<Integer,Map<String,Integer>> getWorst() {
		return worst;
	}
	public void setWorst(Map<Integer,Map<String,Integer>> worst) {
		this.worst = worst;
	}
	public Map<String, Integer> getAssertion2Id() {
		return assertion2Id;
	}
	public void setAssertion2Id(Map<String, Integer> assertion2Id) {
		this.assertion2Id = assertion2Id;
	}

}
