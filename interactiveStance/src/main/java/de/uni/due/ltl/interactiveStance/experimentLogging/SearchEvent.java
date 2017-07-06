package de.uni.due.ltl.interactiveStance.experimentLogging;

public class SearchEvent extends LoggingEvent {

private String query;
	
	public SearchEvent(ExperimentLogging logging, String query) {
		super(logging);
		this.query=query;
	}

	@Override
	protected String eventToString() {
		return "SEARCH\t"+query;
	}

}
