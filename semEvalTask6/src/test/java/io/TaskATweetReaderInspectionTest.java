package io;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import dataInspection.PreprocessingTokenizationInspector;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import types.StanceAnnotation;

public class TaskATweetReaderInspectionTest {

	@Test
    public void tweetReaderTest() throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskA", TaskATweetReader.PARAM_PATTERNS,
				"*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",TaskATweetReader.PARAM_MEMORIZE_RESOURCE,true);
		
		int i=0;
		for (JCas jcas : new JCasIterable(reader)) {
			String target = JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget();
			System.out.println(target+ " ("+JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()+") : "+jcas.getDocumentText());
			i++;
		}
		int numOfTweets= new File(baseDir + "/semevalTask6/tweetsTaskA").list().length;
		Assert.assertEquals(i,numOfTweets);
	}
}
