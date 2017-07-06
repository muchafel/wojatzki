package de.uni.due.ltl.interactiveStance.experimentLogging;

public class TargetSelectedEvent extends LoggingEvent {

	private String polarity;
	private String target;
	
	public TargetSelectedEvent(ExperimentLogging logging, String polarity, String target) {
		super(logging);
		this.polarity=polarity;
		this.target=target;
	}

	@Override
	protected String eventToString() {
		return "TARGET SELECTED\t"+"POLARITY:\t"+target+ "\tPOLARIYT:"+polarity;
	}

}
