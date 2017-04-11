package de.uni_due.ltl.catalanStanceDetection.comparison;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceSVMorSVMTypeReader;



public class PlotLSTMvsSVM {
	
	static String LANGUAGE_CODE = "ca";
	public static void main(String[] args) throws IOException, ResourceInitializationException, AnalysisEngineProcessException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				CatalanStanceSVMorSVMTypeReader.class, CatalanStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE,
				CatalanStanceSVMorSVMTypeReader.PARAM_SOURCE_LOCATION,
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt", CatalanStanceSVMorSVMTypeReader.PARAM_LABEL_FILE,
				baseDir + "/IberEval/training_truth_" + LANGUAGE_CODE + ".txt",
				CatalanStanceSVMorSVMTypeReader.PARAM_LSTM_PREDICTION_FILE,
				"src/main/resources/id2outcome/" + LANGUAGE_CODE
						+ "_sparse10_id2Outcome.txt", CatalanStanceSVMorSVMTypeReader.PARAM_SVM_PREDICTION_FILE,"src/main/resources/id2outcome/"+LANGUAGE_CODE+"_char_word_embeddings_id2homogenizedOutcome.txt");

		AnalysisEngine tokenizerEngine = getTokenizerEngine(
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt");
		
		plotData(reader, tokenizerEngine);
		
	}
	
	private static void plotData(CollectionReaderDescription reader, AnalysisEngine tokenizerEngine)
			throws AnalysisEngineProcessException {
		for (JCas jCas : new JCasIterable(reader)) {
			tokenizerEngine.process(jCas);
			String outcome = getOutcome(jCas);
			System.out.println(jCas.getDocumentText()+"\t"+outcome);
		}

	}

	private static String getOutcome(JCas jCas) {
		for(TextClassificationOutcome outcome: JCasUtil.select(jCas, TextClassificationOutcome.class)){
			return outcome.getOutcome();
		}
		return null;
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
