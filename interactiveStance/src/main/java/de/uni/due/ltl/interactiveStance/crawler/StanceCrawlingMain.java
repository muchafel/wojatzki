package de.uni.due.ltl.interactiveStance.crawler;

import java.util.ArrayList;
import java.util.List;

import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class StanceCrawlingMain {

	public List<StanceCrawlerInstance> crawlers;
	private StanceDB db;
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		StanceCrawlingMain crawlerFleet= new StanceCrawlingMain();
		crawlerFleet.setDb(new StanceDB("", ""));
		crawlerFleet.crawlers= new ArrayList<>();
//		crawlerFleet.getCrawlers().add(new DummyCrawler("dummyUrl"));
		crawlerFleet.getCrawlers().add(new ForAndAgainstDOTCOMCrawler("http://www.forandagainst.com/"));
		
		for(StanceCrawlerInstance crawler: crawlerFleet.getCrawlers()){
			crawler.harvestDataPoints();
		}
	}

	private List<StanceCrawlerInstance> getCrawlers() {
		return this.crawlers;
	}

	private void setDb(StanceDB stanceDB) {
		this.db=stanceDB;
	}

}
