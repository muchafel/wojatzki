package de.uni.due.ltl.interactiveStance.experimentLogging;

public class ThresholdEvent extends LoggingEvent {

	private String thresholdConfig;
	private String variant;
	
	public ThresholdEvent(ExperimentLogging logging, String thresholdConfig,String variant) {
		super(logging);
		this.thresholdConfig=thresholdConfig;
		this.variant=variant;
	}

	@Override
	protected String eventToString() {
		if(variant.equals("optimized")){
			return "OPTMIZED THRESHOLD\t"+thresholdConfig;
		}else if(variant.equals("fixed")){
			return "FIXED THRESHOLD\t"+thresholdConfig;
		}else{
			return "DISTRIBUTION_DERIVED THRESHOLD\t"+thresholdConfig;
		}
	}

}
