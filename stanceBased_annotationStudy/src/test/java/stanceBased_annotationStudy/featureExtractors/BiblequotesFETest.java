package stanceBased_annotationStudy.featureExtractors;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import featureExtractors.BiblequotesFE;
import io.StanceReader;
import io.StanceReader_AddsOriginal;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;

import org.dkpro.tc.testing.FeatureTestUtil;

public class BiblequotesFETest extends StanceTestBase {

	private static final Object LANGUAGE_CODE = "en";
	Boolean[] list = new Boolean[5];
	int i=0;

	@Test
	public void exampleTest() throws Exception {
		// AnalysisEngine engine = getPreprocessing();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(StanceReader.class,
				StanceReader.PARAM_SOURCE_LOCATION, "/Users/NiklasM/Documents/Stance Anno Neu/stanceBased_annotationStudy/src/test/resources/",
				StanceReader.PARAM_LANGUAGE, LANGUAGE_CODE, StanceReader.PARAM_PATTERNS, "*.xml.bin",
				StanceReader.PARAM_TARGET_LABEL, "ATHEISM");
		for (JCas jcas : new JCasIterable(reader)) {

			BiblequotesFE extractor = new BiblequotesFE();

			Set<Feature> features = extractor.extract(jcas, TextClassificationTarget.get(jcas));
			Assert.assertEquals(1, features.size());
			Iterator<Feature> iter = features.iterator();
			
			list[i]=((Boolean) iter.next().getValue());
			i++;
		}
		Assert.assertEquals(list[0], true);
		Assert.assertEquals(list[1], false);
		Assert.assertEquals(list[2], false);
	}
}
