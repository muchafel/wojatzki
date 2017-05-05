package de.uni.due.ltl.interactiveStance.io;

import org.apache.uima.resource.ResourceInitializationException;

public class EvaluationData {

	private EvaluationDataSet trainData;
	
	
	public EvaluationData(String target) throws ResourceInitializationException {
		this.trainData = new EvaluationDataSet( "src/main/resources/test_data/testSet/targets/"+target);
		this.testData = new EvaluationDataSet("src/main/resources/test_data/testSet/targets/"+target);
	}
	private EvaluationDataSet testData;
	
	
}
