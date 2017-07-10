package de.uni.due.ltl.interactiveStance.experimentLogging;

public class LogOutEvent extends LoggingEvent {

	public LogOutEvent(ExperimentLogging logging) {
		super(logging);
	}

	@Override
	protected String eventToString() {
		return "LOGOUT";
	}

}
