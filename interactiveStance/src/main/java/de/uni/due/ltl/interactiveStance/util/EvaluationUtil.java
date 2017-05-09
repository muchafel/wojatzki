package de.uni.due.ltl.interactiveStance.util;

import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class EvaluationUtil {

	public static double getSemEvalMeasure(Fscore<String> fscore) {
		return (fscore.getScoreForLabel("AGAINST")+fscore.getScoreForLabel("FAVOR"))/2;
	}
}
