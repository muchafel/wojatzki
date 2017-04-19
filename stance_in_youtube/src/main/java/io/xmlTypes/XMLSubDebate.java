package io.xmlTypes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

public class XMLSubDebate {

	private int begin;
	private int end;
	private String sub_debate_set;
	private String sub_debate_target;
	private String stance;

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

	@XmlElement(name = "sub_debate_target")
	public String getSub_debate_target() {
		return sub_debate_target;
	}


	public void setSub_debate_target(String sub_debate_target) {
		this.sub_debate_target = sub_debate_target;
	}

	@XmlElement(name = "sub_debate_stance")
	public String getStance() {
		return stance;
	}

	public void setStance(String stance) {
		this.stance = stance;
	}

	@XmlElement(name = "sub_debate_set")
	public String getSub_debate_set() {
		return sub_debate_set;
	}

	public void setSub_debate_set(String sub_debate_set) {
		this.sub_debate_set = sub_debate_set;
	}
}
