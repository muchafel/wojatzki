package assertionRegression.corpusProcessing;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.io.text.TokenizedTextWriter;

public class CleanCorpus {
	public static void main(String[] args) throws UIMAException, IOException {
		 CollectionReaderDescription reader = createReaderDescription(IssueTweetReader.class,
				 IssueTweetReader.PARAM_SOURCE_LOCATION, "/Users/michael/Desktop/statuses/data",
				 IssueTweetReader.PARAM_LANGUAGE, "en");
	        AnalysisEngineDescription segmenter = createEngineDescription(NonSentenceArktweetTokenizer.class);
	        AnalysisEngineDescription linebreaker = createEngineDescription(LineBreaker.class);
	        AnalysisEngineDescription writer = createEngineDescription(TweetTokenizedTextWriter.class);
//	        AnalysisEngineDescription writer = createEngineDescription(TokenizedTextWriter.class,
//	                TokenizedTextWriter.PARAM_TARGET_LOCATION, "/Users/michael/Desktop/statuses/cleaned/text.txt",
//	                TokenizedTextWriter.PARAM_SINGULAR_TARGET, false);

	        SimplePipeline.runPipeline(reader, segmenter,linebreaker, writer);
	}
}
