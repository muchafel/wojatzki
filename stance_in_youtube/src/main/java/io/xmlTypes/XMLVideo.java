package io.xmlTypes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"URL", "comments"})
public class XMLVideo {
	
	private XMLCommentSet comments;
	private String URL;

	@XmlElement(name = "comments")
	public XMLCommentSet getComments() {
		return comments;
	}
	
	public void setComments(XMLCommentSet set) {
		this.comments=set;
	}

	@XmlElement(name = "url")
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
}
