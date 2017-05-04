package de.uni_due.ltl.catalanStanceDetection.assembly;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;

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
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.ml.uima.TcAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader;

public class SVM_Prediction {
	
	static String LANGUAGE_CODE = "es";
	
	public static void main(String[] args) throws ResourceInitializationException, IOException, AnalysisEngineProcessException {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(CatalanStanceReader.class,
				CatalanStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE, CatalanStanceReader.PARAM_SOURCE_LOCATION,
				baseDir +"/IberEval/test/test_tweets_"+LANGUAGE_CODE+".txt", CatalanStanceReader.PARAM_LABEL_FILE,
				baseDir + "/IberEval/test_truth_" + LANGUAGE_CODE + ".txt", CatalanStanceReader.PARAM_IST_TRAIN, false);

		AnalysisEngine engine = getPredictionEngiEngine(
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt", "src/main/resources/trainedModels/"+LANGUAGE_CODE+"/"+LANGUAGE_CODE+"_embeddings_3_3000_words_3000_char");
		
	
		makePredictions(reader,engine);
	}
	
	private static void makePredictions(CollectionReaderDescription reader, AnalysisEngine engine) throws AnalysisEngineProcessException, IOException {
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			DocumentMetaData md= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
			TextClassificationOutcome outcome= JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next();
			String result= md.getDocumentId()+","+outcome.getOutcome()+","+"DUMMY";
			FileUtils.write(new File("src/main/resources/"+LANGUAGE_CODE+"_svm_prediction_id2Outcome.txt"), result+System.lineSeparator(), "UTF-8", true);
//			FileUtils.write(new File("src/main/resources/result/"+LANGUAGE_CODE+"_svm_prediction"), result+System.lineSeparator(), "UTF-8", true);
		}
		
	}

	private static AnalysisEngine getPredictionEngiEngine(String string, String modelFolder) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(ArktweetTokenizer.class),
					createEngineDescription(
					TcAnnotator.class,
					TcAnnotator.PARAM_TC_MODEL_LOCATION, modelFolder
			)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
	
	
}
