package util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.Id2Outcome;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.evaluator.EvaluatorBase;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.evaluator.EvaluatorFactory;

public class ComputeSemevalMeasure implements Constants{

	public static void main(String[] args) throws IOException, TextClassificationException {
//		String features="ngrams";
		String features="battery";

//		String target="FeministMovement";
//		String target="ClimateChangeisaRealConcern";
		String target="LegalizationofAbortion";
//		String target="HillaryClinton";
//		String target="Atheism";
		String path="src/main/resources/evaluation/semevalMeasure/"+features+"/"+target+"/id2homogenizedOutcome.txt";
		printSemevalMeasure(new File(path));
	}
	public static void printSemevalMeasure(File tempId2Outcome) throws IOException, TextClassificationException {
		Id2Outcome id2Outcome = new Id2Outcome(tempId2Outcome, LM_SINGLE_LABEL);
		EvaluatorBase evaluator = EvaluatorFactory.createEvaluator(id2Outcome, true, true);
		Map<String, Double> resultTempMap = evaluator.calculateEvaluationMeasures();
		Double favor = null;
		Double against = null;
		for (String key : resultTempMap.keySet()) {
			if(key.equals("MacroFScore_AGAINST"))against=resultTempMap.get(key);
			if(key.equals("MacroFScore_FAVOR"))favor=resultTempMap.get(key);
		}
		System.out.println("FAVOR: "+favor);
		System.out.println("AGAINST: "+against);
		System.out.println((favor+against)/2);
	}
	public static double getSemevalMeasure(File tempId2Outcome) throws IOException, TextClassificationException {
		Id2Outcome id2Outcome = new Id2Outcome(tempId2Outcome, LM_SINGLE_LABEL);
		EvaluatorBase evaluator = EvaluatorFactory.createEvaluator(id2Outcome, true, true);
		Map<String, Double> resultTempMap = evaluator.calculateEvaluationMeasures();
		Double favor = null;
		Double against = null;
		for (String key : resultTempMap.keySet()) {
			if(key.equals("MacroFScore_AGAINST"))against=resultTempMap.get(key);
			if(key.equals("MacroFScore_FAVOR"))favor=resultTempMap.get(key);
		}
		return (favor+against)/2;
	}
}
