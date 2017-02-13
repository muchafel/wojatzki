package de.uni_due.subdebateInfluence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

import de.uni_due.ltl.util.TargetSets;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.Evaluation;
import de.unidue.ltl.evaluation.EvaluationResult;
import de.unidue.ltl.evaluation.evaluationComparison.McNemarTest;
import de.unidue.ltl.evaluation.io.TcId2OutcomeReader;
import de.unidue.ltl.evaluation.measure.util.CategorialMeasuresUtil;

public class StancePerformanceOnSubdebates {
//Für ein Signifikanzniveau von alpha = 5% ergibt sich ein kritischer Wert von 3,84
	public static void main(String[] args) throws Exception {
//		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/ngrams_embeddings_sentiment.txt");
		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/ngrams_sentiment.txt");
//		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/majorityBaseline.txt");
//		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random.txt");
//		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/ngrams_embeddings_sentiment.txt");
//		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/ngrams_embeddings.txt");
//		File targetFile= new File("src/main/resources/id2outcome/debateStance/curated/test.txt");
		Evaluation<String> evaluation = Filtereable_TcId2OutcomeReader.read(targetFile);
		printResult(evaluation);
		
		System.out.println("----");
		List<String> explicitInstances= new ArrayList<>();
		for(String target: TargetSets.targets_Set1){
			System.out.println(target);
			explicitInstances.addAll(getSubdebateInstances(target,"1"));
			Evaluation<String> evaluation_filtered = Filtereable_TcId2OutcomeReader.read_only(targetFile, getSubdebateInstances(target,"1"));
			printResult(evaluation_filtered);
			chiSquare(evaluation,evaluation_filtered);
		}
//		for(String target: TargetSets.targets_Set2){
//			System.out.println(target);
//			explicitInstances.addAll(getSubdebateInstances(target,"2"));
//			Evaluation<String> evaluation_filtered = Filtereable_TcId2OutcomeReader.read_only(targetFile, getSubdebateInstances(target,"2"));
//			printResult(evaluation_filtered);
//			chiSquare(evaluation,evaluation_filtered);
//		}
		System.out.println("-----------ALL EXCLUDED-------");
		Evaluation<String> evaluation_exluded = Filtereable_TcId2OutcomeReader.read_butExclude(targetFile, explicitInstances);
		printResult(evaluation_exluded);
		chiSquare(evaluation,evaluation_exluded);
		
		System.out.println("-----------ONLY EXPLICIT-------");
		Evaluation<String> evaluation_included = Filtereable_TcId2OutcomeReader.read_only(targetFile, explicitInstances);
		printResult(evaluation_included);
		chiSquare(evaluation,evaluation_included);
		
	}

	private static void chiSquare(Evaluation<String> eval1, Evaluation<String> eval2) {
		ChiSquareTest chiSquare= new ChiSquareTest();
		long[] observed1 ={getPositivesSEMEVAL(eval1),getNegatives(eval1)};
		long[] observed2 ={getPositivesSEMEVAL(eval2),getNegatives(eval2)};
		System.out.println("	CHI SQUARE "+chiSquare.chiSquareTestDataSetsComparison(observed1, observed2));
		
	}

	private static long getFalseNegatives(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long positives=confusionMatrix.getFalseNegative("FAVOR");
		positives+=confusionMatrix.getFalseNegative("AGAINST");
		return positives;
	}

	private static long getFalsePositives(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long positives=confusionMatrix.getFalsePositives("FAVOR");
		positives+=confusionMatrix.getFalsePositives("AGAINST");
		return positives;
	}

	private static long getTrueNegatives(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long positives=confusionMatrix.getTruePositives("FAVOR");
		positives+=confusionMatrix.getTruePositives("AGAINST");
		return positives;
	}

	private static long getTruePositivesSEMEVAL(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long positives=confusionMatrix.getTrueNegatives("FAVOR");
		positives+=confusionMatrix.getTrueNegatives("AGAINST");
		return positives;
	}

	private static long getNegatives(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long negatives=confusionMatrix.getFalseNegative("FAVOR");
		negatives+=confusionMatrix.getFalsePositives("FAVOR");
		negatives+=confusionMatrix.getFalseNegative("AGAINST");
		negatives+=confusionMatrix.getFalsePositives("AGAINST");
		return negatives;
}

	private static long getPositivesSEMEVAL(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long positives=confusionMatrix.getTrueNegatives("FAVOR");
		positives+=confusionMatrix.getTruePositives("FAVOR");
		positives+=confusionMatrix.getTrueNegatives("AGAINST");
		positives+=confusionMatrix.getTruePositives("AGAINST");
		return positives;
}

	private static List<String> getSubdebateInstances(String target, String set) throws IOException {
		List<String> instances= new ArrayList<>();
		File targetFile= new File("src/main/resources/explicitStancesLocators"+"/"+set+"/"+target+".txt");
		for(String line : FileUtils.readLines(targetFile)){
			instances.add(line.split(":")[0]);
		}
		return instances;
	}

	private static void printResult(Evaluation<String> evaluation) {
		Map<String, EvaluationResult> results = CategorialMeasuresUtil
				.computeCategorialResults(evaluation.getEntries());
		
		double semeval=0;
		for (String measure : results.keySet()) {
			if(measure.equals("Fscore_FAVOR")){
				System.out.println("\t"+measure + " " + results.get(measure).getResult());
				semeval+=results.get(measure).getResult();
			}
			if(measure.equals("Fscore_AGAINST")){
				System.out.println("\t"+measure + " " + results.get(measure).getResult());
				semeval+=results.get(measure).getResult();
			}
			if(measure.equals("Fscore_MICRO")){
				System.out.println("\t"+measure + " " + results.get(measure).getResult());
			}
			if(measure.equals("Fscore_NONE")){
				System.out.println("\t"+measure + " " + results.get(measure).getResult());
			}
		}
		System.out.println("\t"+"SEMEVAL "+semeval/2);
		System.out.println("\t"+"MICRO SEMEVAL "+getMicroSemEval(evaluation));
		
	}

	private static double getMicroSemEval(Evaluation<String> evaluation) {
		ConfusionMatrix<String> confusionMatrix= evaluation.getConfusionMatrix();
		long microTP=confusionMatrix.getTruePositives("FAVOR");
		microTP+=confusionMatrix.getTruePositives("AGAINST");
		
		long microFP=confusionMatrix.getFalsePositives("FAVOR");
		microFP+=confusionMatrix.getFalsePositives("AGAINST");
		
		long microFN=confusionMatrix.getFalseNegative("FAVOR");
		microFN+=confusionMatrix.getFalseNegative("AGAINST");
		
		//MicroPrecision = (TP1+TP2)/(TP1+TP2+FP1+FP2)
		//MicroRecall = (TP1+TP2)/(TP1+TP2+FN1+FN2)
		double microPrecision= microTP/((double)microTP+(double)microFP);
		double microRecall= microTP/((double)microTP+(double)microFN);
		
		return 2*microRecall*microPrecision/(microRecall+microPrecision);
	}

}
