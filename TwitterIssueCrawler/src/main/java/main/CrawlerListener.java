package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.FileChooserUI;

import org.apache.commons.io.FileUtils;

import twitter4j.HashtagEntity;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.json.DataObjectFactory;

public class CrawlerListener implements StatusListener {

	private String issue;
	private Map<String, List<String>> file2Hashtags;
	
	
	public CrawlerListener(Map<String, List<String>> file2Hashtags) {
		this.file2Hashtags= file2Hashtags;
		for(String issue: this.file2Hashtags.keySet()){
			System.out.println("Start crawling for "+issue);
			System.out.println(file2Hashtags.get(issue));
		}
	}

	public void onException(Exception ex) {
		System.out.println(ex.getLocalizedMessage());
	}

	public void onStatus(Status status) {
		String username = status.getUser().getScreenName(); 
		String content = status.getText();
		String json = TwitterObjectFactory.getRawJSON(status);
		
		if (status.getPlace() != null && !status.getPlace().equals("United States")) {
			// do nothing
		} else {

			String towrite="";
			if(status.isRetweet()){ 
				towrite = "@" + username + "\t" + status.getRetweetedStatus().getText().replace("\n", "")+"\t"+ status.isRetweet()+"\t"+status.getCreatedAt() +"\n";
			}else{
				towrite = "@" + username + "\t" + content.replace("\n", "")+"\t"+ status.isRetweet() +"\t"+status.getCreatedAt()+ "\n";
			}
			

			for(String issue: this.file2Hashtags.keySet()){
				for(String hashtag: file2Hashtags.get(issue)){
					if(towrite.toLowerCase().contains(" "+hashtag.toLowerCase()+" ")){
						write(issue,towrite);
						write(issue.replace(".txt", ".json"),json+ "\n");
						System.out.println(towrite);
						System.out.println(json);
						break;
					}
				}
			}
		}
	}



	private void write(String issue, String towrite) {
		Writer out;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File("statuses/" + issue), true), "UTF-8"));
			out.write(towrite);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		System.err.println(statusDeletionNotice);
	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		System.err.println("track limit "+numberOfLimitedStatuses);
	}

	public void onScrubGeo(long userId, long upToStatusId) {
		
	}

	public void onStallWarning(StallWarning warning) {
		System.err.println("stall warning "+warning.getMessage());
		
	}
	

		

		}

