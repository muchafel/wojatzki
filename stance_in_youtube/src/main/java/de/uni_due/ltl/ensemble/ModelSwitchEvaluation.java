package de.uni_due.ltl.ensemble;

import java.io.File;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;

import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.subdebateInfluence.Filtereable_TcId2OutcomeReader;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.Evaluation;
import de.unidue.ltl.evaluation.EvaluationResult;
import de.unidue.ltl.evaluation.measure.util.CategorialMeasuresUtil;

public class ModelSwitchEvaluation {

	public static void main(String[] args) throws ResourceInitializationException {
		Map<String,String> typePrediction= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/typePrediction/ngrams.txt");
		Map<String,String> id2Gold =  Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt");
		Map<String,String> lstm_prediction = Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt");
		Map<String,String> svm_prediction = Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/debateStance/curated/ngrams_sentiment.txt");
		
		Evaluation<String> evaluation= new Evaluation<>();
		
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
		System.out.println("//// Mixed Model comments/////");
		printResult(evaluation);
		System.out.println("//// Mixed Model targets /////");
		Evaluation<String> evaluationTargets= new Evaluation<>();
		Map<String,String> typePredictionTargets= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/typePrediction/basedOnTargets.txt");
		for(String key: typePredictionTargets.keySet()){
			String type=typePredictionTargets.get(key);
			if(type.equals("SVM")){
				evaluationTargets.register(String.valueOf(id2Gold.get(key)), String.valueOf(svm_prediction.get(key)));
			}else if(type.equals("LSTM")){
				evaluationTargets.register(String.valueOf(id2Gold.get(key)), String.valueOf(lstm_prediction.get(key)));
			}else{
				evaluationTargets.register(String.valueOf(id2Gold.get(key)), String.valueOf(svm_prediction.get(key)));
			}
		}
		printResult(evaluationTargets);
		System.out.println("//// LSTM /////");
		File targetFile_lstm= new File("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt");
		Evaluation<String> evaluation_lstm = Filtereable_TcId2OutcomeReader.read(targetFile_lstm);
		printResult(evaluation_lstm);
		System.out.println("//// SVM /////");
		File targetFile_svm= new File("src/main/resources/id2outcome/debateStance/curated/ngrams_sentiment.txt");
		Evaluation<String> evaluation_svm = Filtereable_TcId2OutcomeReader.read(targetFile_svm);
		printResult(evaluation_svm);
		
		
		
	}
	private static void printResult(Evaluation<String> evaluation) {
		Map<String, EvaluationResult> results = CategorialMeasuresUtil
				.computeCategorialResults(evaluation.getEntries());

		double semeval = 0;
		for (String measure : results.keySet()) {
			if (measure.equals("Fscore_FAVOR")) {
				System.out.println("\t" + measure + " " + results.get(measure).getResult());
				semeval += results.get(measure).getResult();
			}
			if (measure.equals("Fscore_AGAINST")) {
				System.out.println("\t" + measure + " " + results.get(measure).getResult());
				semeval += results.get(measure).getResult();
			}
			if (measure.equals("Fscore_MICRO")) {
				System.out.println("\t" + measure + " " + results.get(measure).getResult());
			}
			if (measure.equals("Fscore_NONE")) {
				System.out.println("\t" + measure + " " + results.get(measure).getResult());
			}
		}
		System.out.println("\t" + "SEMEVAL " + semeval / 2);
		System.out.println("\t" + "MICRO SEMEVAL " + getMicroSemEval(evaluation));
	}
	private static double getMicroSemEval(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix = evaluation.getConfusionMatrix();
		long microTP = confusionMatrix.getTruePositives("FAVOR");
		microTP += confusionMatrix.getTruePositives("AGAINST");

		long microFP = confusionMatrix.getFalsePositives("FAVOR");
		microFP += confusionMatrix.getFalsePositives("AGAINST");

		long microFN = confusionMatrix.getFalseNegative("FAVOR");
		microFN += confusionMatrix.getFalseNegative("AGAINST");

		// MicroPrecision = (TP1+TP2)/(TP1+TP2+FP1+FP2)
		// MicroRecall = (TP1+TP2)/(TP1+TP2+FN1+FN2)
		double microPrecision = microTP / ((double) microTP + (double) microFP);
		double microRecall = microTP / ((double) microTP + (double) microFN);

		return 2 * microRecall * microPrecision / (microRecall + microPrecision);
	}
}
