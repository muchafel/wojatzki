package de.uni_due.ltl.util.iaa;

public class InsultContainer {
	private int begin;
	private int end;
	private String coveredText;
	private String tag;
	
	public int getBegin() {
		return begin;
	}
	public InsultContainer(int begin, int end, String coveredText, String tag) {
		this.begin = begin;
		this.end = end;
		this.coveredText = coveredText;
		this.tag = tag;
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
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
