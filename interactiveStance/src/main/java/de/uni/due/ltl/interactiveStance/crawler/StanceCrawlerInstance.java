package de.uni.due.ltl.interactiveStance.crawler;

import java.util.List;

import de.uni.due.ltl.interactiveStance.common.Data_Point;

public interface StanceCrawlerInstance {

	public void setURL(String url);
	public String getURL();
	public List<Data_Point> harvestDataPoints(); 
}
