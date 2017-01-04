package de.uni_due.subdebateInfluence;

import java.io.File;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;

import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.Evaluation;
import de.unidue.ltl.evaluation.EvaluationResult;
import de.unidue.ltl.evaluation.measure.util.CategorialMeasuresUtil;

public class Performance_RemovedSignal {
	public static void main(String[] args) throws ResourceInitializationException {
		File folder = new File("src/main/resources/id2outcome/wo_explicitTarget_svm");
		for (File file : folder.listFiles()) {
			Evaluation<String> evaluation = Filtereable_TcId2OutcomeReader.read(file);
			System.out.println(file.getName()+"***");
			printResult(evaluation);
		}
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
		System.out.println("+\t"+ "DROP "+ (getMicroSemEval(evaluation)));
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
