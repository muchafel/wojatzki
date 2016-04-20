package annotationStudy.evaluation;

import webanno.custom.Central_Target;
import webanno.custom.Ground_Attitudes;
import webanno.custom.Stance;

public class StanceContainer {
	/**
	 * constructor from Stance Anno
	 * @param stance
	 */
	public StanceContainer(Stance stance) {
		this.target= stance.getStance_Polarity();
		this.polarity= stance.getStance_Target();
		this.start=stance.getBegin();
		this.end=stance.getEnd();
	}
	/**
	 * constructor from Central_Target Anno
	 * @param stance
	 * @param mappedTarget
	 */
	public StanceContainer(Central_Target stance, String mappedTarget) {
		this.target= mappedTarget;
		if(stance.getPolarity()==null){
			this.polarity="NONE";
		}else{
			this.polarity= stance.getPolarity();
		}
		this.start=stance.getBegin();
		this.end=stance.getEnd();
	}
	/**
	 * constructor from Ground_Attitudes Anno
	 * @param stance
	 * @param mappedTarget
	 */
	public StanceContainer(Ground_Attitudes stance, String mappedTarget) {
		this.target= mappedTarget;
		if(stance.getPolarity()==null){
			this.polarity="NONE";
		}else{
			this.polarity= stance.getPolarity();
		}
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
