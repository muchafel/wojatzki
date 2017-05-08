package de.uni.due.ltl.interactiveStance.backend;

import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class EvaluationResult {

	private double semEval=0.2;
	private double microF=0.4;
	
	public EvaluationResult(Fscore<String> fscore) {
		this.microF=fscore.getMicroFscore();
		this.semEval=getSemEvalMeasure(fscore);
	}
	public double getSemEval() {
		return semEval;
	}
	public double getMicroF() {
		return microF;
	}
	
	private double getSemEvalMeasure(Fscore<String> fscore) {
		return (fscore.getScoreForLabel("AGAINST")+fscore.getScoreForLabel("FAVOR"))/2;
	}
	
	
}
