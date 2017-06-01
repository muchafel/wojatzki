package de.uni.due.ltl.interactiveStance.io;

import java.io.Serializable;

import org.apache.uima.resource.ResourceInitializationException;

import de.uni.due.ltl.interactiveStance.util.EvaluationScenarioUtil;

public class EvaluationScenario implements Serializable, Cloneable{

	private EvaluationDataSet trainData;
	private EvaluationDataSet testData;
	private String target;
	private String mode;
	
	
	public EvaluationScenario(String target, String experimentalMode) throws Exception {
		
	   if(!EvaluationScenarioUtil.targetIsValid(target)){
		   throw new Exception(target + " is not a valid target");
	   }
	   this.setTarget(target);
	   
	   this.mode=experimentalMode;
		
		//FIXME: proper way of deploying resources (DKPRO_HOME ?)
		this.trainData = new EvaluationDataSet( "/Users/michael/git/ucsm_git/interactiveStance/src/main/resources/test_data/trainSet/targets/"+target);
		this.testData = new EvaluationDataSet( "/Users/michael/git/ucsm_git/interactiveStance/src/main/resources/test_data/testSet/targets/"+target);
//		this.trainData = new EvaluationDataSet( "src/main/resources/test_data/trainSet/targets/"+target);
//		this.testData = new EvaluationDataSet("src/main/resources/test_data/testSet/targets/"+target);
	}


	public EvaluationDataSet getTrainData() {
		return trainData;
	}


	public EvaluationDataSet getTestData() {
		return testData;
	}


	public String getTarget() {
		return target;
	}


	public void setTarget(String target) {
		this.target = target;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}

	
}
