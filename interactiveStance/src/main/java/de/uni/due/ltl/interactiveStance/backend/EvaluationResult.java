package de.uni.due.ltl.interactiveStance.backend;

import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.CategoricalAccuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;
import de.unidue.ltl.evaluation.measure.categorial.CategoricalMeasure;

public class EvaluationResult {

	private double semEval;
	private double microF;
	private double macroF;
	private double accuracyFAVOR;
	private double accuracyAGAINST;
	private double accuracyNONE;
	private Fscore<String> fscore;
	
	public EvaluationResult(EvaluationData<String> data) {
		this.fscore= new Fscore<>(data);
		Accuracy<String> accuracy= new Accuracy<>(data);
		CategoricalAccuracy<String> categoricalAccuracy= new CategoricalAccuracy<>(data);
		this.accuracyNONE= categoricalAccuracy.getScoreForLabel("NONE");
		this.accuracyAGAINST= categoricalAccuracy.getScoreForLabel("AGAINST");
		this.accuracyAGAINST= categoricalAccuracy.getScoreForLabel("FAVOR");
		this.microF=fscore.getMicroFscore();
		this.macroF= fscore.getMacroFscore();
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
