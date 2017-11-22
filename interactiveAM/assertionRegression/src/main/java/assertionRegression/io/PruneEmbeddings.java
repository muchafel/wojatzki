package assertionRegression.io;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class PruneEmbeddings {
	
	public static void main(String[] args) throws IOException, ResourceInitializationException, AnalysisEngineProcessException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(AssertionReader.class,
				AssertionReader.PARAM_SOURCE_LOCATION, "src/main/resources/data/data.tsv", AssertionReader.PARAM_LANGUAGE, "en",
				AssertionReader.PARAM_TARGETCLASS, "Agreement");
		

		AnalysisEngine tokenizerEngine = getTokenizerEngine();
		
		Set<String> dataTokens=getTokens(reader,tokenizerEngine);
		
		
		System.out.println(dataTokens.size());
		
//		String line = null;
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/data/wiki.en.vec"));
//			while ((line = br.readLine()) != null) { 
//				String word=line.split(" ")[0];
//				if(dataTokens.contains(word)||particelContained(dataTokens,word)){
//					dataTokens.remove(word);
//					FileUtils.write(new File("src/main/resources/data/pruned/wiki.en.vec"), line+System.lineSeparator(), true);
//				}
//			} 
//		} 
//		catch (IOException e) {
//			System.err.println("Error: " + e);
//		}
//		System.out.println(dataTokens);
//		System.out.println(dataTokens.size());

	}
	
	private static boolean particelContained(Set<String> vocab, String word) {
		if(vocab.contains(word.split("'")[0])){
			return true;
		}
		if(vocab.contains(word.toLowerCase())){
			return true;
		}
		return false;
	}
	
	
	private static Set<String> getTokens(CollectionReaderDescription reader, AnalysisEngine tokenizerEngine) throws AnalysisEngineProcessException {
		Set<String> result= new HashSet<>();
		for (JCas jCas : new JCasIterable(reader)) {
			tokenizerEngine.process(jCas);
			result.addAll(JCasUtil.toText(JCasUtil.select(jCas, Token.class)));
		}
		
		return result;
	}


	private static AnalysisEngine getTokenizerEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

}
