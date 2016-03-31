package util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import dataInspection.PreprocessingTokenizationInspector;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import types.StanceAnnotation;

public class CollococationMeasureHelperTest {
	private String target= "Atheism";

	@Test
    public void pipeLine3() throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskA",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

			// create favor and against fds foreach target
			FrequencyDistribution<String> favour = new FrequencyDistribution<String>();
			FrequencyDistribution<String> against = new FrequencyDistribution<String>();
			for (JCas jcas : new JCasIterable(reader)) {
				if (JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget().equals(target)) {
					if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
							.equals("FAVOR")) {
						favour = incAll(favour, jcas);
					}
					if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
							.equals("AGAINST")) {
						against = incAll(against, jcas);
					}
				}
			}
		CollocationMeasureHelper helper= new CollocationMeasureHelper(favour, against);
		for(String pro: favour.getKeys()){
			System.out.println(pro+ " g-mean: "+helper.getDiffOfGMeans(pro)+" dice: "+helper.getDiffOfDice(pro)+ " chi: "+helper.getDiffOfChi(pro));
		}
	}

	private FrequencyDistribution<String> incAll(FrequencyDistribution<String> freq, JCas jcas) {
		for(String word: jcas.getDocumentText().split(" ")){
			freq.inc(word);
		}
		return freq;
	}
}
