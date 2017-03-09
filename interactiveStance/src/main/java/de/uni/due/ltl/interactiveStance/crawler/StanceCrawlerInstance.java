package de.uni.due.ltl.interactiveStance.crawler;

import java.util.List;

import de.uni.due.ltl.interactiveStance.db.StanceDB;

public interface StanceCrawlerInstance {

	public void setURL(String url);
	public String getURL();
	public void harvestDataPoints(StanceDB db); 
}
