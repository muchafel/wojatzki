package io.xmlTypes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "videos")
public class XMLCorpus {
	List<XMLVideo> videos;

	@XmlElement(name = "video")
	public List<XMLVideo> getVideos() {
		return videos;
	}

	public void setVideos(List<XMLVideo> videos) {
		this.videos = videos;
	}
}
