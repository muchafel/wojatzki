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
import io.StanceReader;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.Iterator;
import java.util.Set;

import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;

import org.dkpro.tc.testing.FeatureTestUtil;


public class ExampleTest extends StanceTestBase{
	
	@Test
	public void exampleTest() throws Exception {
		AnalysisEngine engine = getPreprocessing();
		
		/**
		 * we can also read some ressources by using 
		 * CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				StanceReader.class, StanceReader.PARAM_SOURCE_LOCATION, "/stanceBased_annotationStudy/src/test/resources/", StanceReader.PARAM_LANGUAGE,
				LANGUAGE_CODE, StanceReader.PARAM_PATTERNS, "*.bin", StanceReader.PARAM_TARGET_LABEL,subTarget));
				for (JCas jcas : new JCasIterable(reader)) { }
		 */
		
		JCas jcas = engine.newJCas();
		jcas.setDocumentLanguage("de");
		jcas.setDocumentText("Was ist das für 1 Leben? sagt Jesus 33:66");
		engine.process(jcas);

	
		/**
		 * 
		 */
//		MyFeatureExtractor extractor = FeatureUtil.createResource(
//				MyFeatureExtractor.class,
//				MyFeatureExtractor.PARAM, "xxx");
//		
//		Set<Feature> features = extractor.extract(jcas, TextClassificationTarget.get(jcas));
//
//		Assert.assertEquals(1, features.size());
//
//		Iterator<Feature> iter = features.iterator();
//		assertFeature("", 1, iter.next());
		
	}
}
