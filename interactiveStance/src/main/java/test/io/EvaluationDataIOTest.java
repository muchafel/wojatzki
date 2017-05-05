package test.io;

import java.io.File;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import de.uni.due.ltl.interactiveStance.io.EvaluationData;
import de.uni.due.ltl.interactiveStance.io.TaskATweetReader;

public class EvaluationDataIOTest {
	@Test
	public void simpleReadTest() throws Exception {
		String path = "src/main/resources/test_data/testSet/targets/Atheism";
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, path, TaskATweetReader.PARAM_PATTERNS,
				"*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",TaskATweetReader.PARAM_MEMORIZE_RESOURCE,true);
		
		int i=0;
		for (JCas jcas : new JCasIterable(reader)) {
//			System.out.println(jcas.getDocumentText()+ " "+ JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance());
			i++;
		}
		System.out.println(i);
		int numOfTweets= new File(path).list().length;
		Assert.assertEquals(i,numOfTweets);
	}
	
	@Test
	public void dataSetTest() throws Exception {
		
		EvaluationData data = new EvaluationData("Atheism");
		
		System.out.println(data.getTrainData().getNumberOfInstances());
		
		Assert.assertEquals(513,data.getTrainData().getNumberOfInstances());
		Assert.assertEquals(220,data.getTestData().getNumberOfInstances());
		
	}
}
