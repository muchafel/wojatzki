package de.unidue.ltl.core.data.opinionSummarization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class OpinionSummarizationData {

	private List<Participant> participants;
	private List<String> assertions;
	private double[][] valueMatrixRaw;
	private RealMatrix valueMatrix;
	private String issue;
	private Map<Integer, Map<String, Integer>> id2AssertionToBest;
	private Map<Integer, Map<String, Integer>> id2AssertionToWorst;
	private Map<String, Integer> assertion2Id;
	
	
	public OpinionSummarizationData(List<Participant> participants, List<String> statements, double[][] valueMatrixInput, String issue2, Map<Integer, Map<String, Integer>> best, Map<Integer, Map<String, Integer>> worst, Map<String, Integer> assertion2Id) {
		this.participants = participants;
		this.assertions = statements;
		this.valueMatrix = new Array2DRowRealMatrix(valueMatrixInput);
		this.valueMatrixRaw = valueMatrix.getData();
		this.issue=issue2;
		this.id2AssertionToBest=best;
		this.id2AssertionToWorst=worst;
		this.setAssertion2Id(assertion2Id);
	}
	
	public OpinionSummarizationData(List<Participant> participants, List<String> statements, double[][] valueMatrixInput,String issue) {
		this.participants = participants;
		this.assertions = statements;
		this.valueMatrix = new Array2DRowRealMatrix(valueMatrixInput);
		this.valueMatrixRaw = valueMatrix.getData();
		this.setIssue(issue);
	}

	public List<Participant> getParticipants() {
		return participants;
	}


	public List<String> getStatements() {
		return assertions;
	}


	public double[][] getValueMatrix() {
		return valueMatrixRaw;
	}

	public String getStringValuesForParticipant(Participant particpant) throws Exception{
		return join(getRatingsOfParticipant(particpant));
	}

	//returns the vector for a particular paricipant
	public double[] getRatingsOfParticipant(Participant particpant) throws Exception{
		if(!participants.contains(particpant)){
			throw new Exception(particpant.getId()+" not conatined in list");
		}
		int index=participants.indexOf(particpant);
		return valueMatrix.getRow(index);
	}
	
	public double[] getRatingsForAssertion(String statement) throws Exception{
		if(!assertions.contains(statement)){
			throw new Exception(statement+" not conatined in list");
		}
		int index=assertions.indexOf(statement);
		return valueMatrix.getColumn(index);
	}
	
	
	private String join(double[] ds) {
		StringBuilder sb=new StringBuilder();
		int i=0;
		for(double value : ds){
			 if (i>0)sb.append("\t");
			 sb.append(value);
			 i++;
		}
		return sb.toString();
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public Map<Integer, Map<String, Integer>> getId2AssertionToBest() {
		return id2AssertionToBest;
	}

	public Map<Integer, Map<String, Integer>> getId2AssertionToWorst() {
		return id2AssertionToWorst;
	}

	public double getValue(int id, String assertion) throws Exception {
		int participant=getParticipantIndex(id);
		return getRatingsForAssertion(assertion)[participant];
	}

	private int getAssertionIndex(String assertion) {
		int i=0;
		for(String a: assertions){
			if(assertion.equals(a)){
				return i;
			}
			i++;
		}
		return -1;

	}

	private int getParticipantIndex(int id) {
		int i=0;
		for(Participant p: participants){
			if(p.getId()==id){
				return i;
			}
			i++;
		}
		return -1;
	}

	public List<String> getParticipantStrings() {
		List<String> result= new ArrayList<>();
		for(Participant p: participants){
			result.add(String.valueOf(p.getId()));
		}
		return result;
	}

	public Map<String, Integer> getAssertion2Id() {
		return assertion2Id;
	}

	public void setAssertion2Id(Map<String, Integer> assertion2Id) {
		this.assertion2Id = assertion2Id;
	}
	
	
}
