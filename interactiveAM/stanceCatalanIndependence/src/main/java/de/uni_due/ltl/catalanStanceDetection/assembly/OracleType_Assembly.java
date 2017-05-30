package de.uni_due.ltl.catalanStanceDetection.assembly;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.ml.uima.TcAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.uni_due.ltl.catalanStanceDetection.dl_Util.Id2OutcomeUtil;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceSVMorSVMTypeReader;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class OracleType_Assembly {
static String LANGUAGE_CODE = "ca";
	
	public static void main(String[] args) throws Exception {

		File svmPredictionFile= new File("src/main/resources/id2outcome/"+LANGUAGE_CODE+"_char_word_embeddings_id2homogenizedOutcome.txt");
		File lstmPredictionFile= new File("src/main/resources/id2outcome/"+LANGUAGE_CODE+"_sparse10_id2Outcome.txt");
//		File lstmPredictionFile= new File("/Users/michael/git/ucsm_git/iberStance_lstm/result/cv_lr/"+language+"_activation_softmax_dropOut_0.3_sparse10_id2Outcome.txt");
		Map<String,String> gold=Id2OutcomeUtil.getId2GoldMap_String("src/main/resources/id2outcome/"+LANGUAGE_CODE+"_sparse10_id2Outcome.txt");
		
		Map<String,String> lstmPrediction=Id2OutcomeUtil.getId2OutcomeMap_String(lstmPredictionFile.getAbsolutePath());
		Map<String,String> svmPrediction=Id2OutcomeUtil.getId2OutcomeMap_String(svmPredictionFile.getAbsolutePath(),true);
		
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		String dir= baseDir + "/IberEval/";
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				CatalanStanceSVMorSVMTypeReader.class, CatalanStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE,
				CatalanStanceSVMorSVMTypeReader.PARAM_SOURCE_LOCATION,
				dir + "training_tweets_" + LANGUAGE_CODE + ".txt", CatalanStanceSVMorSVMTypeReader.PARAM_LABEL_FILE,
				dir + "training_truth_" + LANGUAGE_CODE + ".txt",
				CatalanStanceSVMorSVMTypeReader.PARAM_LSTM_PREDICTION_FILE,
				"src/main/resources/id2outcome/" + LANGUAGE_CODE
						+ "_sparse10_id2Outcome.txt", CatalanStanceSVMorSVMTypeReader.PARAM_SVM_PREDICTION_FILE,"src/main/resources/id2outcome/"+LANGUAGE_CODE+"_char_word_embeddings_id2homogenizedOutcome.txt");

		AnalysisEngine engine = getPredictionEngiEngine(
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt", "src/main/resources/trainedModels/"+LANGUAGE_CODE+"/"+LANGUAGE_CODE+"_SVMorLSTM_Tree");
		
	
		makePredictions(reader,engine,lstmPrediction,svmPrediction,gold);
	}
	
	private static void makePredictions(CollectionReaderDescription reader, AnalysisEngine engine, Map<String, String> lstmPrediction, Map<String, String> svmPrediction, Map<String, String> gold) throws Exception {
		EvaluationData<String> assembledData= new EvaluationData<>();
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			DocumentMetaData md= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
			TextClassificationOutcome outcome= JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next();
			TextClassificationTarget unit= JCasUtil.select(jcas, TextClassificationTarget.class).iterator().next();
			
			System.out.println(unit.getId());
			int unitId= unit.getId();
			if(outcome.getOutcome().equals("LSTM")){
				System.out.println(gold.get(String.valueOf(unitId))+" "+ lstmPrediction.get(String.valueOf(unitId)));
				assembledData.register(gold.get(String.valueOf(unitId)), lstmPrediction.get(String.valueOf(unitId)));
			}else{
				System.out.println(gold.get(String.valueOf(unitId))+" "+ svmPrediction.get(String.valueOf(unitId)));
				assembledData.register(gold.get(String.valueOf(unitId)), svmPrediction.get(String.valueOf(unitId)));
			}
			
		}
		evaluate(assembledData);
	}

	private static void evaluate(EvaluationData<String> evaluationData) throws Exception {

		Fscore<String> fscore = new Fscore<>(evaluationData);
		Accuracy<String> acc = new Accuracy<>(evaluationData);

		System.out.println("Accuracy " + acc.getAccuracy());
		System.out.println("MACRO_F1 " + fscore.getMacroFscore());
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());
		System.out.println("F1 (FAVOR) " + fscore.getScoreForLabel("FAVOR"));
		System.out.println("F1 (AGAINST) " + fscore.getScoreForLabel("AGAINST"));
		System.out.println("F1 (NEUTRAL) " + fscore.getScoreForLabel("NEUTRAL"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluationData);

		System.out.println(matrix.toString());

	}
	
	
	private static AnalysisEngine getPredictionEngiEngine(String string, String modelFolder) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(ArktweetTokenizer.class)
					));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
}
