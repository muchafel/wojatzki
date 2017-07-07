package de.uni.due.ltl.interactiveStance.experimentLogging;

import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;

public class ResultEvent extends LoggingEvent {

	private EvaluationResult result;

	public ResultEvent(ExperimentLogging logging, EvaluationResult result) {
		super(logging);
		this.result=result;
	}

	@Override
	protected String eventToString() {
		return "RESULT\tMICRO_F1\t"+result.getMicroF()+"\tMACRO_F1\t"+result.getMacroF();
	}

}
