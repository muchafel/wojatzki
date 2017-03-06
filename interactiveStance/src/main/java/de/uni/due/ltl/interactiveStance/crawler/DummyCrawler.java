package de.uni.due.ltl.interactiveStance.crawler;

import java.util.List;

import de.uni.due.ltl.interactiveStance.common.Data_Point;

public class DummyCrawler implements StanceCrawlerInstance {
private String url;
	public DummyCrawler(String url) {
		this.url= url;
	}

	@Override
	public void setURL(String url) {
		this.url=url;
	}

	@Override
	public String getURL() {
		return this.url;
	}

	@Override
	public List<Data_Point> harvestDataPoints() {
		// TODO Auto-generated method stub
		return null;
	}

}
