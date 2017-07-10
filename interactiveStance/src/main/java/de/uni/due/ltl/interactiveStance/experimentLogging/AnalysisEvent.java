package de.uni.due.ltl.interactiveStance.client;

import java.util.ArrayList;
import java.util.List;

import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.experimentLogging.LoggingEvent;

public class AnalysisEvent extends LoggingEvent {

	List<String> favorTargets = new ArrayList<String>();
	List<String> againstTargets = new ArrayList<String>();
	
	public AnalysisEvent(ExperimentLogging logging, List<ExplicitTarget> favorList, List<ExplicitTarget> againstList) {
		super(logging);
		for(ExplicitTarget target: favorList){
			favorTargets.add(target.getTargetName());
		}
		for(ExplicitTarget target: againstList){
			againstTargets.add(target.getTargetName());
		}
	}

	@Override
	protected String eventToString() {
		return "ANALYSIS"+"\tfavorTargets\t"+favorTargets+"\tagainstTargets\t"+againstTargets;
	}

}
