package annotationStudy.evaluation;

import webanno.custom.Stance;

public class StanceContainer {
	public StanceContainer(Stance stance) {
		this.target= stance.getStance_Polarity();
		this.polarity= stance.getStance_Target();
		this.start=stance.getBegin();
		this.end=stance.getEnd();
	}
	private String target;
	
	private String polarity;
	private int start;

	private int end;
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getPolarity() {
		return polarity;
	}
	public void setPolarity(String polarity) {
		this.polarity = polarity;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
}
