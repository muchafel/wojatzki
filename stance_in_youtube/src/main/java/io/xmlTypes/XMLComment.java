package io.xmlTypes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"author", "replyTo","text","stance","subdebateCollection"})
public class XMLComment {

	private String author;
	private String replyTo;
	private String stance;
	private String text;
	
	private XMLSubdebateCollection subDebateCollection;

	
	@XmlElement(name = "author")
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@XmlElement(name = "debate_stance")
	public String getStance() {
		return stance;
	}

	public void setStance(String stance) {
		this.stance = stance;
	}

	@XmlElement(name = "sub_debates")
	public XMLSubdebateCollection getSubdebateCollection() {
		return subDebateCollection;
	}

	public void setSubdebateCollection(XMLSubdebateCollection subdebateSet) {
		this.subDebateCollection = subdebateSet;
	}

	@XmlElement(name = "text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@XmlElement(name = "replyToUser")
	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	
}
