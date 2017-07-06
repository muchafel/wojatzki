package de.uni.due.ltl.interactiveStance.experimentLogging;

public class FilterEvent extends LoggingEvent {

	private String filterTerm;
	
	public FilterEvent(ExperimentLogging logging, String filterTerm) {
		super(logging);
		this.filterTerm=filterTerm;
	}

	@Override
	protected String eventToString() {
		return "FILTER\t"+filterTerm;
	}

}
