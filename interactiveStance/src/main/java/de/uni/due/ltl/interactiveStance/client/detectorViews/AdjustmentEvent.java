package de.uni.due.ltl.interactiveStance.client.detectorViews;

import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.experimentLogging.LoggingEvent;

public class AdjustmentEvent extends LoggingEvent {

	private int adjustment;
	
	public AdjustmentEvent(ExperimentLogging logging, int value) {
		super(logging);
		this.adjustment=value;
	}

	@Override
	protected String eventToString() {
		return "DETECTOR_ADJUSTED_TO\t"+adjustment;
	}

	public int getAdjustment() {
		return adjustment;
	}

}
