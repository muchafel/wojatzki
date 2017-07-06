package de.uni.due.ltl.interactiveStance.experimentLogging;

public class CoverageEvent extends LoggingEvent {

	private double coverage;
	public CoverageEvent(ExperimentLogging logging, double coverage) {
		super(logging);
		this.coverage=coverage;
	}

	@Override
	protected String eventToString() {
		return "COVERAGE\t"+String.valueOf(coverage);
	}

}
