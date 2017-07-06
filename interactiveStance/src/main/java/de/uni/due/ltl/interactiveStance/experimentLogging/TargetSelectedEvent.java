package de.uni.due.ltl.interactiveStance.experimentLogging;

public class TargetSelectedEvent extends LoggingEvent {

	private String polarity;
	private String target;
	private boolean isSelected;
	
	public TargetSelectedEvent(ExperimentLogging logging, String polarity, String target, boolean isSelected) {
		super(logging);
		this.polarity=polarity;
		this.target=target;
		this.isSelected=isSelected;
	}
	

	@Override
	protected String eventToString() {
		if(isSelected){
			return "TARGET SELECTED\t"+"TARGET:\t"+target+ "\tPOLARIYT:"+polarity;
		}else{
			return "TARGET DESELECTED\t"+"TARGET:\t"+target+ "\tPOLARIYT:"+polarity;
		}
	}

}
