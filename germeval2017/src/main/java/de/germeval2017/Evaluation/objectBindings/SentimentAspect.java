package de.germeval2017.Evaluation.objectBindings;

import javax.xml.bind.annotation.XmlAttribute;

public class SentimentAspect {
	
	private int begin;
	private int end;
	private int begin2;
	private int end2;
	private String aspect;
	private String ote;
	private String sentiment;
	
	public SentimentAspect(int begin, int end, String aspect, String sentiment) {
		this.begin = begin;
		this.end = end;
		this.aspect = aspect;
		this.sentiment = sentiment;
	}
	
	@XmlAttribute(name = "from")
	public int getBegin() {
		return begin;
	}
	
	public void setBegin(int begin) {
		this.begin = begin;
	}
	
	@XmlAttribute(name = "to")
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	
	@XmlAttribute(name = "from2")
	public int getBegin2() {
		return begin2;
	}
	
	public void setBegin2(int begin) {
		this.begin2 = begin;
	}
	
	@XmlAttribute(name = "to2")
	public int getEnd2() {
		return end2;
	}
	public void setEnd2(int end2) {
		this.end2 = end2;
	}
	
	@XmlAttribute(name = "category")
	public String getAspect() {
		return aspect;
	}
	public void setAspect(String aspect) {
		this.aspect = aspect;
	}
	
	@XmlAttribute(name = "polarity")
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	public SentimentAspect() {
	}

	@XmlAttribute(name = "target")
	public String getOte() {
		return ote;
	}

	public void setOte(String ote) {
		this.ote = ote;
	}
}
