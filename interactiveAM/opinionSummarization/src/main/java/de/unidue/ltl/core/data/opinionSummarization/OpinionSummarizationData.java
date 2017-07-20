package de.unidue.ltl.core.data.opinionSummarization;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class OpinionSummarizationData {

	private List<Participant> participants;
	private List<String> statements;
	private double[][] valueMatrixRaw;
	private RealMatrix valueMatrix;
	
	public OpinionSummarizationData(List<Participant> participants, List<String> statements, double[][] valueMatrixInput) {
		this.participants = participants;
		this.statements = statements;
		this.valueMatrix = new Array2DRowRealMatrix(valueMatrixInput);
		this.valueMatrixRaw = valueMatrix.getData();
	}

	public List<Participant> getParticipants() {
		return participants;
	}


	public List<String> getStatements() {
		return statements;
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
	
	public double[] getRatingsForStatement(String statement) throws Exception{
		if(!statements.contains(statement)){
			throw new Exception(statement+" not conatined in list");
		}
		int index=statements.indexOf(statement);
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
	
	
}
