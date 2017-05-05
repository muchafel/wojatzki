package de.uni.due.ltl.interactiveStance.io;

import org.apache.uima.resource.ResourceInitializationException;

import de.uni.due.ltl.interactiveStance.util.EvaluationScenarioUtil;

public class EvaluationData {

	private EvaluationDataSet trainData;
	private EvaluationDataSet testData;
	
	
	public EvaluationData(String target) throws Exception {
		
	   if(!EvaluationScenarioUtil.targetIsValid(target)){
		   throw new Exception(target + " is not a valid target");
	   }
		
		this.trainData = new EvaluationDataSet( "src/main/resources/test_data/trainSet/targets/"+target);
		this.testData = new EvaluationDataSet("src/main/resources/test_data/testSet/targets/"+target);
	}


	public EvaluationDataSet getTrainData() {
		return trainData;
	}


	public EvaluationDataSet getTestData() {
		return testData;
	}

	
}
