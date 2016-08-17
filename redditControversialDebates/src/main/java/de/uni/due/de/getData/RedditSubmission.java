package de.uni.due.de.getData;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RedditSubmission {

	private String id;
	private long commentCount;
	private String permaLink;
	private String selfText;
	private List<RedditComment> redditComments;
	
	public String getId() {
		return id;
	}
	@XmlElement
	public void setId(String id) {
		this.id = id;
	}
	public String getPermaLink() {
		return permaLink;
	}
	@XmlElement
	public void setPermaLink(String permaLink) {
		this.permaLink = permaLink;
	}
	public String getSelfText() {
		return selfText;
	}
	@XmlElement
	public void setSelfText(String selfText) {
		this.selfText = selfText;
	}
	public List<RedditComment> getRedditComments() {
		return redditComments;
	}
	@XmlElement
	public void setRedditComments(List<RedditComment> redditComments) {
		this.redditComments = redditComments;
	}
	public long getCommentCount() {
		return commentCount;
	}
	@XmlElement
	public void setCommentCount(long commentCount) {
		this.commentCount = commentCount;
	}
}
