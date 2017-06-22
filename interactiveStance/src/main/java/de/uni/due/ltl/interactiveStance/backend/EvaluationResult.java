package de.uni.due.ltl.interactiveStance.backend;

import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class EvaluationResult {

	private double semEval;
	private double microF;
	private double macroF;
	private double fFAVOR;
	private double fAGAINST;
	private double fNONE;
	private Fscore<String> fscore;

	public EvaluationResult(EvaluationData<String> data) {
		this.fscore = new Fscore<>(data);
		Accuracy<String> accuracy = new Accuracy<>(data);
		this.fNONE = fscore.getScoreForLabel("NONE");
		this.fAGAINST = fscore.getScoreForLabel("AGAINST");
		this.fFAVOR = fscore.getScoreForLabel("FAVOR");
		this.microF = fscore.getMicroFscore();
		this.macroF = fscore.getMacroFscore();
		this.semEval = getSemEvalMeasure(fscore);
	}

	public double getSemEval() {
		return semEval;
	}

	public double getMicroF() {
		return microF;
	}

	private double getSemEvalMeasure(Fscore<String> fscore) {
		return (fscore.getScoreForLabel("AGAINST") + fscore.getScoreForLabel("FAVOR")) / 2;
	}

	public double getMacroF() {
		return macroF;
	}

	public double getfFAVOR() {
		return fFAVOR;
	}

	public double getfAGAINST() {
		return fAGAINST;
	}

	public double getfNONE() {
		return fNONE;
	}

}
