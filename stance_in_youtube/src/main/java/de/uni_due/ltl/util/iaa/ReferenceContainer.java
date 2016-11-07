package de.uni_due.ltl.util.iaa;

public class ReferenceContainer {
	private int begin;
	private int end;
	private String coveredText;
	private String source;
	
	public int getBegin() {
		return begin;
	}
	public ReferenceContainer(int begin, int end, String coveredText, String source) throws Exception {
		this.begin = begin;
		this.end = end;
		this.coveredText = coveredText;
		if(source == null){
			this.source = "Foreigen Source";
		}
		this.source = source;
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
	public String getSource() {
		return source;
	}
	public void setSource(String tag) {
		this.source = tag;
	}
}
