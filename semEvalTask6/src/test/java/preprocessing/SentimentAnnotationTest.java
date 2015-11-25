package preprocessing;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import annotators.ModalVerbAnnotator;
import dataInspection.ChunkingAnnotationInspector;
import dataInspection.FunctionalPartsAnnotationInspector;
import dataInspection.PreprocessingTokenizationInspector;
import dataInspection.PreprocessingTwitterSpecificAnnotatorInspector;
import dataInspection.SentimentInspector;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.berkeleyparser.BerkeleyParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import io.TaskATweetReader;
import util.PreprocessingPipeline;


public class SentimentAnnotationTest {
	
	@Test
    public void pipeLine() throws Exception {
		System.err.println("PIPELINE1");
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				TaskATweetReader.class,
				TaskATweetReader.PARAM_LANGUAGE,"en",
				TaskATweetReader.PARAM_SOURCE_LOCATION,"src/test/resources/taskA",
				TaskATweetReader.PARAM_PATTERNS, "*.xml"
        );
		AnalysisEngineDescription inspection=  createEngineDescription(SentimentInspector.class);
		
		SimplePipeline.runPipeline(reader, PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno(),inspection);
	}
	
}
