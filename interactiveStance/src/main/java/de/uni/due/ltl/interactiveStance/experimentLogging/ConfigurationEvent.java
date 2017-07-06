package de.uni.due.ltl.interactiveStance.experimentLogging;

public class ConfigurationEvent extends LoggingEvent {

	private String scenario;
	private String mode;
	private boolean simpleMode;
	
	public ConfigurationEvent(ExperimentLogging logging, String scenario, String mode, boolean simpleMode) {
		super(logging);
		this.scenario=scenario;
		this.mode=mode;
		this.simpleMode=simpleMode;
	}

	@Override
	protected String eventToString() {
		return "CONFIGURATION\tSCENARIO\t"+this.scenario+"\tMODE:\t"+this.mode+"\tSIMPLEMODE:\t"+simpleMode;
	}

}
