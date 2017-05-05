package test.io;

import java.io.File;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.uni.due.ltl.interactiveStance.io.TaskATweetReader;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;

public class xmiDataTest {
		
	@Test
	public void simpleReadTest() throws Exception {
		String path = "src/main/resources/test_data/testSet/targets/Atheism";
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, path, TaskATweetReader.PARAM_PATTERNS,
				"*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",TaskATweetReader.PARAM_MEMORIZE_RESOURCE,true);
		
		int i=0;
		for (JCas jcas : new JCasIterable(reader)) {
			System.out.println(jcas.getDocumentText()+ " "+ JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance());
			i++;
		}
		System.out.println(i);
		int numOfTweets= new File(path).list().length;
		Assert.assertEquals(i,numOfTweets);
	}
	
}
