package main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCrawler {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, InterruptedException {

		File folder= new File("config");
		for(File file: folder.listFiles()){

			//new connection
			ConfigurationBuilder cb = new ConfigurationBuilder();

			cb.setDebugEnabled(true).setOAuthConsumerKey("jepApDU1iFv0sURBL8zLFDFTs")
					.setOAuthConsumerSecret("NaJVtN9IliOQ7gy1HumS17jtXPjk8WKc4zX7yg5zbJypFNhjP8")
					.setOAuthAccessToken("40204501-Qs4xweEqQTVC6CA6hoScat4u9ScLGIdvLwdQkhbX4")
					.setOAuthAccessTokenSecret("r4U1abDH5cm0r3owrGcMIlDh1wvENwOEq43sMRmTzqJ1P");
			
			cb.setJSONStoreEnabled(true);
			cb.setIncludeMyRetweetEnabled(true);
			cb.setIncludeExtAltTextEnabled(true);
			cb.setTweetModeExtended(true);
			
			TwitterStream twitterStream= new TwitterStreamFactory(cb.build()).getInstance();
			
			//new query
			FilterQuery fq = new FilterQuery();
			Config config= new Config(file.getPath());

			String[] keywordsArray = config.getHashtags();
			
			CrawlerListener listener = new CrawlerListener(file.getName().replace(".xml", ".txt"));
			twitterStream.addListener(listener);
			
			double lati1 = 31.47; // south border 31.47
			double longi1 = 67.0; // east port maine 67.0
			double lati2 = 48.59; //north border 48.59
			double longi2 = 111.8; //https://en.wikipedia.org/wiki/Cape_Alava 111.8
			double[][] bb= {{longi1, lati1}, {longi2, lati2}};

			fq.locations(bb);

			fq.track(keywordsArray);
			twitterStream.filter(fq);
			Thread.sleep(360*1000);
		}
	}
}
