package de.uni_due.ltl.ensemble;

import java.io.File;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;

import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.subdebateInfluence.Filtereable_TcId2OutcomeReader;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.CategoricalAccuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class ModelSwitchEvaluation {

	public static void main(String[] args) throws ResourceInitializationException {
		Map<String,String> typePrediction= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/typePrediction/id2homogenizedOutcome.txt");
		Map<String,String> id2Gold =  Id2OutcomeUtil.getId2GoldMap_String("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt");
		Map<String,String> lstm_prediction = Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt");
		Map<String,String> svm_prediction = Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/debateStance/curated/ngrams_sentiment.txt");
		
		EvaluationData<String> evaluation= new EvaluationData<>();
		
		for(String key: typePrediction.keySet()){
			String type=typePrediction.get(key);
			if(type.equals("SVM")){
				evaluation.register(String.valueOf(id2Gold.get(key)), String.valueOf(svm_prediction.get(key)));
			}else if(type.equals("LSTM")){
				evaluation.register(String.valueOf(id2Gold.get(key)), String.valueOf(lstm_prediction.get(key)));
			}else if (type.equals("NOPREF")){
				evaluation.register(String.valueOf(id2Gold.get(key)), String.valueOf(svm_prediction.get(key)));
			}
		}
		System.out.println("//// Mixed Model comments /////");
		printResult(evaluation);
//		System.out.println("//// Mixed Model targets /////");
//		EvaluationData<String> evaluationTargets= new EvaluationData<>();
//		Map<String,String> typePredictionTargets= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/typePrediction/basedOnTargets.txt");
//		for(String key: typePredictionTargets.keySet()){
//			String type=typePredictionTargets.get(key);
//			if(type.equals("SVM")){
//				evaluationTargets.register(String.valueOf(id2Gold.get(key)), String.valueOf(svm_prediction.get(key)));
//			}else if(type.equals("LSTM")){
//				evaluationTargets.register(String.valueOf(id2Gold.get(key)), String.valueOf(lstm_prediction.get(key)));
//			}else{
//				evaluationTargets.register(String.valueOf(id2Gold.get(key)), String.valueOf(svm_prediction.get(key)));
//			}
//		}
//		printResult(evaluationTargets);
		System.out.println("//// LSTM /////");
		File targetFile_lstm= new File("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt");
		EvaluationData<String> evaluation_lstm = Filtereable_TcId2OutcomeReader.read(targetFile_lstm);
		printResult(evaluation_lstm);
		System.out.println("//// SVM /////");
		File targetFile_svm= new File("src/main/resources/id2outcome/debateStance/curated/ngrams_sentiment.txt");
		EvaluationData<String> evaluation_svm = Filtereable_TcId2OutcomeReader.read(targetFile_svm);
		printResult(evaluation_svm);
		System.out.println("//// Pred /////");
		EvaluationData<String> predEval = Filtereable_TcId2OutcomeReader.read(new File("src/main/resources/id2outcome/typePrediction/ensemble.txt"));
		printResult(predEval);
		System.out.println("//// Oracle /////");
		EvaluationData<String> oracle = Filtereable_TcId2OutcomeReader.read(new File("src/main/resources/id2outcome/typePrediction/ngrams_types.txt"));
		printResult(oracle);
		
		
	}
	private static void printPredResult(EvaluationData<String> evaluation) {
		Fscore<String> fscore = new Fscore<>(evaluation);
		Accuracy<String> acc = new Accuracy<>(evaluation);

		System.out.println("Accuracy " + acc.getAccuracy());
		System.out.println("MACRO_F1 " + fscore.getMacroFscore());
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());
//		System.out.println("F1 (FAVOR) " + fscore.getScoreForLabel("FAVOR"));
//		System.out.println("F1 (AGAINST) " + fscore.getScoreForLabel("AGAINST"));
//		System.out.println("F1 (NONE) " + fscore.getScoreForLabel("NONE"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluation);
		System.out.println(matrix.toString());
		
	}
	private static void printResult(EvaluationData<String> evaluation) {
		Fscore<String> fscore = new Fscore<>(evaluation);
		Accuracy<String> acc = new Accuracy<>(evaluation);

//		System.out.println("Accuracy " + acc.getAccuracy());
//		System.out.println("MACRO_F1 " + fscore.getMacroFscore());
		System.out.println("MICRO_F1 " + fscore.getMicroFscore());
//		System.out.println("F1 (FAVOR) " + fscore.getScoreForLabel("FAVOR"));
//		System.out.println("F1 (AGAINST) " + fscore.getScoreForLabel("AGAINST"));
//		System.out.println("F1 (NONE) " + fscore.getScoreForLabel("NONE"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluation);
		System.out.println(matrix.toString());
		System.out.println("\t" + "MICRO SEMEVAL " + getMicroSemEval(evaluation));
	}
	
	
	private static double getMicroSemEval(EvaluationData<String> evaluation) {
		CategoricalAccuracy<String> categoricalAccuracy = new CategoricalAccuracy<>(evaluation);
		double microTP = categoricalAccuracy.getTP("FAVOR");
		microTP += categoricalAccuracy.getTP("AGAINST");

		double microFP = categoricalAccuracy.getFP("FAVOR");
		microFP += categoricalAccuracy.getFP("AGAINST");

		double microFN = categoricalAccuracy.getFN("FAVOR");
		microFN += categoricalAccuracy.getFN("AGAINST");

		// MicroPrecision = (TP1+TP2)/(TP1+TP2+FP1+FP2)
		// MicroRecall = (TP1+TP2)/(TP1+TP2+FN1+FN2)
		double microPrecision = microTP / ((double) microTP + (double) microFP);
		double microRecall = microTP / ((double) microTP + (double) microFN);

		return 2 * microRecall * microPrecision / (microRecall + microPrecision);
	}
}
