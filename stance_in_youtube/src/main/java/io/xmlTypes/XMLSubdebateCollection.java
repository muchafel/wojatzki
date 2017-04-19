package io.xmlTypes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class XMLSubdebateCollection {

	List<XMLSubDebate> sub_debates;
	
	@XmlElement(name = "subdebate")
	public List<XMLSubDebate> getSub_debates() {
		return sub_debates;
	}

	public void setSub_debates(List<XMLSubDebate> sub_debates) {
		this.sub_debates = sub_debates;
	}
}
