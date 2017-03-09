package de.uni.due.ltl.interactiveStance.crawler;

import java.util.List;

import de.uni.due.ltl.interactiveStance.db.StanceDB;

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
	public void harvestDataPoints(StanceDB db) {
		// TODO Auto-generated method stub
	}

}
