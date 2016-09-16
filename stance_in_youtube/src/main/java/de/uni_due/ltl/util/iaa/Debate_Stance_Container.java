package de.uni_due.ltl.util.iaa;

public class Debate_Stance_Container {

	private String polarity;
	private int begin;
	private int end;
	private String coveredText;
	
	public Debate_Stance_Container(String polarity, int begin, int end, String coveredText) {
		this.polarity = polarity;
		this.begin = begin;
		this.end = end;
		this.coveredText = coveredText;
	}

	public String getPolarity() {
		return polarity;
	}

	public void setPolarity(String polarity) {
		this.polarity = polarity;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getCoveredText() {
		return coveredText;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}

}
