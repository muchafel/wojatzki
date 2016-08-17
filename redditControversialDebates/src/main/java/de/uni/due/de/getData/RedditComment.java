package de.uni.due.de.getData;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class RedditComment {

	private String author;
	private String body;
	private int upvotes;
	private int downvotes;
	private List<RedditComment> replies;
	private String id;
	private String parent;
	
	public String getAuthor() {
		return author;
	}
	@XmlElement
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBody() {
		return body;
	}
	@XmlElement(name="ReplyBody")
	public void setBody(String body) {
		this.body = body;
	}
	public int getUpvotes() {
		return upvotes;
	}
	@XmlElement
	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}
	public int getDownvotes() {
		return downvotes;
	}
	@XmlElement
	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}
	public List<RedditComment> getReplies() {
		return replies;
	}
	@XmlElement
	public void setReplies(List<RedditComment> replies) {
		this.replies = replies;
	}
	public String getId(){
		return this.id;
	}
	@XmlElement
	public void setId(String identifier) {
		this.id=identifier;
	}
	public String getParent() {
		return parent;
	}
	@XmlElement(name="ParentReply")
	public void setParent(String parent) {
		this.parent = parent;
	}
}
