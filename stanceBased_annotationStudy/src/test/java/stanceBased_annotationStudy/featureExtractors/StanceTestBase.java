package stanceBased_annotationStudy.featureExtractors;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public abstract class StanceTestBase  {
	public AnalysisEngine getPreprocessing()
			throws ResourceInitializationException
	{
		AnalysisEngineDescription description = createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class)
				// other preprocessing
				);
		
		return createEngine(description);
	}
}
