package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCrawler {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, InterruptedException {

		//new connection
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true).setOAuthConsumerKey("XFnr4vaULEuKpL36ErReMf4ix")
				.setOAuthConsumerSecret("h8wJQTgTjpORDoSOfl0cppQg2HkS9txAsc3ewiKaDwxlaVBrRr")
				.setOAuthAccessToken("40204501-Qs4xweEqQTVC6CA6hoScat4u9ScLGIdvLwdQkhbX4")
				.setOAuthAccessTokenSecret("r4U1abDH5cm0r3owrGcMIlDh1wvENwOEq43sMRmTzqJ1P");
		
		cb.setJSONStoreEnabled(true);
		cb.setIncludeMyRetweetEnabled(true);
		cb.setIncludeExtAltTextEnabled(true);
		cb.setTweetModeExtended(true);
		TwitterStream twitterStream= new TwitterStreamFactory(cb.build()).getInstance();

		
//		load hastgas and issues
		Map<String,List<String>> file2Hashtags= new HashMap<>();
		List<String> listOfallHashtags= new ArrayList<>();
		List<String> configLines= FileUtils.readLines(new File("config/queries.txt"));
		for(String line : configLines){
			String[] lineParts= line.split("\t");
			if(file2Hashtags.containsKey(lineParts[0]+".txt")){
				file2Hashtags.get(lineParts[0]+".txt").add(lineParts[1]);
				listOfallHashtags.add(lineParts[1]);
			}else{
				List<String> hashTags=new ArrayList<>();
				hashTags.add(lineParts[1]);
				file2Hashtags.put(lineParts[0]+".txt", hashTags);
			}
		}
		
		//new query
		FilterQuery fq = new FilterQuery();

		String[] keywordsArray = listOfallHashtags.toArray(new String[0]);
		
		CrawlerListener listener = new CrawlerListener(file2Hashtags);
		twitterStream.addListener(listener);
		fq.language(new String[]{"en"});

		fq.track(keywordsArray);
		twitterStream.filter(fq);
	}
}
