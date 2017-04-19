package io.xmlTypes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class XMLCommentSet {
	List<XMLComment> comments;

	@XmlElement(name = "comment")
	public List<XMLComment> getComments() {
		return comments;
	}

	public void setComments(List<XMLComment> comments) {
		this.comments = comments;
	}
}
