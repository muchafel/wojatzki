package de.uni_due.ltl.catalanStanceDetection.comparison;

import java.io.File;

import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.io.TcId2OutcomeReader;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class Id2OutcomePerformance {

	public static void main(String[] args) throws Exception {

		File targetFileCA_LSTM= new File("src/main/resources/id2outcome/ca_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		File targetFileES_LSTM= new File("src/main/resources/id2outcome/es_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		File targetFileCA_LSTM_sparse= new File("src/main/resources/id2outcome/ca_sparse10_id2Outcome.txt");
		File targetFileES_LSTM_sparse= new File("src/main/resources/id2outcome/es_sparse10_id2Outcome.txt");
		File targetFileCA_SVM= new File("src/main/resources/id2outcome/ca_char_word_id2homogenizedOutcome.txt");
		File targetFileCA_SVM_embeddings= new File("src/main/resources/id2outcome/ca_char_word_embeddings_id2homogenizedOutcome.txt");
		File targetFileES_SVM= new File("src/main/resources/id2outcome/es_char_word_id2homogenizedOutcome.txt");
		File targetFileES_SVM_embeddings= new File("src/main/resources/id2outcome/es_char_word_embeddings_id2homogenizedOutcome.txt");
		
		System.out.println("---");
		System.out.println("CA LSTM");
		evaluate(targetFileCA_LSTM);
		
		System.out.println("---");
		System.out.println("CA LSTM sparse");
		evaluate(targetFileCA_LSTM_sparse);
		
		System.out.println("---");
		System.out.println("ES LSTM");
		evaluate(targetFileES_LSTM);
		
		System.out.println("---");
		System.out.println("ES LSTM sparse");
		evaluate(targetFileES_LSTM_sparse);
		
		System.out.println("---");
		System.out.println("CA SVM");
		evaluate(targetFileCA_SVM);
		
		System.out.println("---");
		System.out.println("CA SVM Embeddings");
		evaluate(targetFileCA_SVM_embeddings);
		
		System.out.println("---");
		System.out.println("ES SVM");
		evaluate(targetFileES_SVM);
		
		System.out.println("---");
		System.out.println("ES SVM Embeddings");
		evaluate(targetFileES_SVM_embeddings);

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
