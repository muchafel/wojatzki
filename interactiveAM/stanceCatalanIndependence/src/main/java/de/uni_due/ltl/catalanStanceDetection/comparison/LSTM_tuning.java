package de.uni_due.ltl.catalanStanceDetection.comparison;

import java.io.File;

import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.io.TcId2OutcomeReader;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class LSTM_tuning {

	public static void main(String[] args) throws Exception {
		File targetFileCA_LSTM= new File("src/main/resources/id2outcome/ca_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		File targetFileCA_LSTM_E10= new File("src/main/resources/id2outcome/ca_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs10_id2Outcome.txt");
		File targetFileCA_LSTM_R132= new File("src/main/resources/id2outcome/ca_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome_132.txt");
		File targetFileCA_LSTM_NADAM= new File("src/main/resources/id2outcome/ca_activation_tanh_opimizernadam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs10_id2Outcome.txt");

		
		
		
		System.out.println("---");
		System.out.println("REGULAR CA");
		evaluate(targetFileCA_LSTM);
		
		System.out.println("---");
		System.out.println("ES E10");
		evaluate(targetFileCA_LSTM_E10);
		
		System.out.println("---");
		System.out.println("CA R132");
		evaluate(targetFileCA_LSTM_R132);
		
		System.out.println("---");
		System.out.println("CA NADAM");
		evaluate(targetFileCA_LSTM_NADAM);

	}

	private static void evaluate(File targetFileCA_LSTM) throws Exception {
		EvaluationData<String> evaluationData= TcId2OutcomeReader.convertFile(targetFileCA_LSTM);
		Fscore<String> fscore= new Fscore<>(evaluationData);
		Accuracy<String> acc= new Accuracy<>(evaluationData);
		
		System.out.println("Accuracy "+ acc.getAccuracy());
		System.out.println("MACRO_F1 "+fscore.getMacroFscore());
		System.out.println("MICRO_F1 "+fscore.getMicroFscore());
		System.out.println("F1 (FAVOR) "+fscore.getScoreForLabel("FAVOR"));
		System.out.println("F1 (AGAINST) "+fscore.getScoreForLabel("AGAINST"));
		System.out.println("F1 (NEUTRAL) "+fscore.getScoreForLabel("NEUTRAL"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluationData);

        System.out.println(matrix.toString());
		
	}

}
