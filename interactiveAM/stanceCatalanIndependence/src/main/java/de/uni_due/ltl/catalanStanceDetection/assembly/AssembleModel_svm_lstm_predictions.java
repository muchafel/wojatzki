package de.uni_due.ltl.catalanStanceDetection.assembly;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.CohenKappaAgreement;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;

import de.uni_due.ltl.catalanStanceDetection.dl_Util.Id2OutcomeUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.io.TcId2OutcomeReader;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class AssembleModel_svm_lstm_predictions {
	
	private static int lstmCounter=0;
	private static String language="es";
	
	public static void main(String[] args) throws Exception {

		assembleCV();
	}

	private static void assembleCV() throws Exception {
		
		File typePredictionFile_lstm= new File("src/main/resources/id2outcome/typePrediction_"+language+"_lstm.txt");
		File typePredictionFile_svm= new File("src/main/resources/id2outcome/typePrediction_"+language+"_svm.txt");

		File svmPredictionFile= new File("src/main/resources/id2outcome/"+language+"_char_word_embeddings_id2homogenizedOutcome.txt");
		File lstmPredictionFile= new File("src/main/resources/id2outcome/"+language+"_sparse10_id2Outcome.txt");
		
		Map<String,String> typePrediction_ltsm=Id2OutcomeUtil.getId2OutcomeMap_String(typePredictionFile_lstm.getAbsolutePath(),true);
		Map<String,String> typePrediction_svm=Id2OutcomeUtil.getId2OutcomeMap_String(typePredictionFile_svm.getAbsolutePath(),true);

		Map<String,String> lstmPrediction=Id2OutcomeUtil.getId2OutcomeMap_String(lstmPredictionFile.getAbsolutePath());
		Map<String,String> svmPrediction=Id2OutcomeUtil.getId2OutcomeMap_String(svmPredictionFile.getAbsolutePath(),true);
		Map<String,String> assemledPrediction=new HashMap<String, String>();
		Map<String,String> gold=Id2OutcomeUtil.getId2GoldMap_String("src/main/resources/id2outcome/"+language+"_sparse10_id2Outcome.txt");
		
		EvaluationData<String> assembledData= new EvaluationData<>();
		for(String id:typePrediction_ltsm.keySet()){
			String label=ressolvePrediction(typePrediction_ltsm.get(id),typePrediction_svm.get(id),lstmPrediction.get(id),svmPrediction.get(id));
			assembledData.register(gold.get(id), label);
			assemledPrediction.put(id, label);

		}
		System.out.println("Type Prediction");
		System.out.println("LSTM:");
		evaluateType(TcId2OutcomeReader.convertFile(typePredictionFile_lstm));
		System.out.println("SVM:");
		evaluateType(TcId2OutcomeReader.convertFile(typePredictionFile_svm));
		System.out.println("-------");
		System.out.println("Assemled");
		evaluate(assembledData);
		System.out.println(lstmCounter);
		
		System.out.println("SVM");
		evaluate(TcId2OutcomeReader.convertFile(svmPredictionFile));
		
		System.out.println("LSTM");
		evaluate(TcId2OutcomeReader.convertFile(lstmPredictionFile));
		
		System.out.println("******");
		compare(lstmPrediction, svmPrediction);
		compare(assemledPrediction, svmPrediction);
	}

	private static void compare(Map<String,String> result_LSTM, Map<String,String>result_SVM) {
		int agree=0;
		int disagree=0;
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		for(String id: result_LSTM.keySet()){
//			System.out.println(result_LSTM.get(id)+" "+ result_SVM.get(id));
			study.addItem(result_LSTM.get(id), result_SVM.get(id));
			if(result_LSTM.get(id).equals(result_SVM.get(id))){
				agree++;
			}else{
				disagree++;
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		
		System.out.println("PA "+ pa.calculateAgreement());
		System.out.println("KAPPA "+ kappa.calculateAgreement());
		System.out.println("# agree: "+agree+ " #disagree: "+disagree);
	}
	
	private static String ressolvePrediction(String typePrediction_lstm, String typePrediction_svm , String lstmPrediction, String svmPredcition) {
		if (typePrediction_lstm.equals("LSTM") &&  typePrediction_svm.equals("UNK") ) {
			lstmCounter++;
			return lstmPrediction;
		} else if (typePrediction_svm.equals("SVM") && typePrediction_lstm.equals("UNK")) {
			return svmPredcition;
		}else if(typePrediction_lstm.equals("LSTM") && typePrediction_svm.equals("SVM")){
			return svmPredcition;
		}
		else{
//			return lstmPrediction;
			return svmPredcition;
		}
	}

	private static void evaluate(EvaluationData<String> evaluationData) throws Exception {

		Fscore<String> fscore = new Fscore<>(evaluationData);
		Accuracy<String> acc = new Accuracy<>(evaluationData);

		System.out.println("Accuracy " + acc.getAccuracy());
		System.out.println("MACRO_F1 " + fscore.getMacroFscore());
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());
		System.out.println("Weighted F "+fscore.getWeightedFscore());
		System.out.println("F1 (FAVOR) " + fscore.getScoreForLabel("FAVOR"));
		System.out.println("F1 (AGAINST) " + fscore.getScoreForLabel("AGAINST"));
		System.out.println("F1 (NEUTRAL) " + fscore.getScoreForLabel("NEUTRAL"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluationData);

		System.out.println(matrix.toString());

	}
	
	private static void evaluateType(EvaluationData<String> evaluationData) throws Exception {

		Fscore<String> fscore = new Fscore<>(evaluationData);
		Accuracy<String> acc = new Accuracy<>(evaluationData);

		System.out.println("Accuracy " + acc.getAccuracy());
		System.out.println("MACRO_F1 " + fscore.getMacroFscore());
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());
		
//		System.out.println("F1 (SVM) " + fscore.getScoreForLabel("SVM"));
//		System.out.println("F1 (LSTM) " + fscore.getScoreForLabel("LSTM"));
//		System.out.println("F1 (BOTH) " + fscore.getScoreForLabel("BOTH"));
//		System.out.println("F1 (UNK) " + fscore.getScoreForLabel("UNK"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluationData);

		System.out.println(matrix.toString());

	}
}
