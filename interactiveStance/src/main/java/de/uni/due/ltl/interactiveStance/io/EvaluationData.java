package de.uni.due.ltl.interactiveStance.io;

import org.apache.uima.resource.ResourceInitializationException;

public class EvaluationData {

	private EvaluationDataSet trainData;
	private EvaluationDataSet testData;
	
	
	public EvaluationData(String target) throws ResourceInitializationException {
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
