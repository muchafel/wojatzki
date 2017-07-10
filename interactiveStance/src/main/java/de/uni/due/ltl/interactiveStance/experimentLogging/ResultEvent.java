package de.uni.due.ltl.interactiveStance.experimentLogging;

import java.util.ArrayList;
import java.util.List;

import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;

public class ResultEvent extends LoggingEvent {

	private List<String> favorTargets = new ArrayList<String>();
	private List<String> againstTargets = new ArrayList<String>();
	private EvaluationResult result;

	public ResultEvent(ExperimentLogging logging, EvaluationResult result, List<ExplicitTarget> favorList, List<ExplicitTarget> againstList) {
		super(logging);
		this.result=result;
		
		for(ExplicitTarget target: favorList){
			favorTargets.add(target.getTargetName());
		}
		for(ExplicitTarget target: againstList){
			againstTargets.add(target.getTargetName());
		}
	}

	@Override
	protected String eventToString() {
		return "RESULT\tMICRO_F1\t"+result.getMicroF()+"\tMACRO_F1\t"+result.getMacroF();
	}

	public List<String> getFavorTargets() {
		return favorTargets;
	}

	public List<String> getAgainstTargets() {
		return againstTargets;
	}

	public EvaluationResult getResult() {
		return result;
	}



}
