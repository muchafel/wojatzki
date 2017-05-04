package de.uni_due.ltl.catalanStanceDetection.dl_Util;

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
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader;

public class PruneEmbeddingsTrainTest {
	static String LANGUAGE_CODE = "es";
	
	public static void main(String[] args) throws IOException, ResourceInitializationException, AnalysisEngineProcessException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(CatalanStanceReader.class,
				CatalanStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE, CatalanStanceReader.PARAM_SOURCE_LOCATION,
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt", CatalanStanceReader.PARAM_LABEL_FILE,
				baseDir + "/IberEval/training_truth_" + LANGUAGE_CODE + ".txt");
		
		CollectionReaderDescription reader2 = CollectionReaderFactory.createReaderDescription(CatalanStanceReader.class,
				CatalanStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE, CatalanStanceReader.PARAM_SOURCE_LOCATION,
				baseDir +"/IberEval/test/test_tweets_"+LANGUAGE_CODE+".txt", CatalanStanceReader.PARAM_LABEL_FILE,
				baseDir + "/IberEval/test_truth_" + LANGUAGE_CODE + ".txt", CatalanStanceReader.PARAM_IST_TRAIN, false);

		AnalysisEngine tokenizerEngine = getTokenizerEngine(
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt");
		
		Set<String> dataTokens=getTokens(reader,tokenizerEngine);
		dataTokens.addAll(getTokens(reader2,tokenizerEngine));
		
		System.out.println(dataTokens.size());
		
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("/Users/michael/Downloads/wiki."+LANGUAGE_CODE+".vec"));
			while ((line = br.readLine()) != null) { 
				String word=line.split(" ")[0];
				if(dataTokens.contains(word)||particelContained(dataTokens,word)){
					dataTokens.remove(word);
					FileUtils.write(new File("src/main/resources/prunedEmbeddings_test_wiki"
							+ "."+LANGUAGE_CODE+".vec"), line+System.lineSeparator(), true);
				}
			} 
		} 
		catch (IOException e) {
			System.err.println("Error: " + e);
		}
		System.out.println(dataTokens);
		System.out.println(dataTokens.size());

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


	private static AnalysisEngine getTokenizerEngine(String string) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(createEngineDescription(ArktweetTokenizer.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

}
