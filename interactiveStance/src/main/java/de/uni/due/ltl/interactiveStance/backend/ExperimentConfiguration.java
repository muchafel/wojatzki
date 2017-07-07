package de.uni.due.ltl.interactiveStance.backend;

public class ExperimentConfiguration {

	private boolean simpleMode;
	private String scenario, mode;
	
	public ExperimentConfiguration(boolean simpleMode, String scenario, String mode) {
		this.simpleMode=simpleMode;
		this.scenario=scenario;
		this.mode=mode;
	}

	public boolean isSimpleMode() {
		return simpleMode;
	}

	public String getScenario() {
		return scenario;
	}

	public String getExperimentMode() {
		return mode;
	}

}
