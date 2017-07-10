package de.uni.due.ltl.interactiveStance.experimentLogging;

import java.util.ArrayList;
import java.util.List;

import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;

public class AnalysisEvent extends LoggingEvent {

	List<String> favorTargets = new ArrayList<String>();
	List<String> againstTargets = new ArrayList<String>();
	
	/**
	 * this event is used to log the time difference to the results
	 * @param logging
	 * @param favorList
	 * @param againstList
	 */
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
