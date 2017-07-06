package de.uni.due.ltl.interactiveStance.experimentLogging;

public class ConfigurationEvent extends LoggingEvent {

	private String scenario;
	private String mode;
	
	public ConfigurationEvent(ExperimentLogging logging, String scenario, String mode) {
		super(logging);
		this.scenario=scenario;
		this.mode=mode;
	}

	@Override
	protected String eventToString() {
		return "CONFIGURATION\t"+this.scenario+"\t"+this.mode;
	}

}
