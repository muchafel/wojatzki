package de.uni.due.ltl.interactiveStance.experimentLogging;

import de.uni.due.ltl.interactiveStance.backend.ExperimentConfiguration;

public class ConfigurationEvent extends LoggingEvent {

	private ExperimentConfiguration config;
	
	public ConfigurationEvent(ExperimentLogging logging, ExperimentConfiguration config) {
		super(logging);
		this.config=config;
	}

	@Override
	protected String eventToString() {
		return "CONFIGURATION\tSCENARIO\t"+config.valuesToString();
	}

	public ExperimentConfiguration getConfig() {
		return config;
	}

}
