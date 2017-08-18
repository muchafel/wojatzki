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
	
	public CrawlerListener(String issue){
		System.out.println("Start crawling for "+issue);
		this.issue=issue;
	}
	
	public void onException(Exception ex) {
		System.out.println(ex.getLocalizedMessage());
	}

	public void onStatus(Status status) {
		String username = status.getUser().getScreenName(); 
		String content = status.getText();
		

		if (status.getPlace() != null && !status.getPlace().equals("United States")) {
			// do nothing
		} else {

			String towrite="";
			if(status.isRetweet()){ 
				towrite = "@" + username + "\t" + status.getRetweetedStatus().getText().replace("\n", "") + "\n";
			}else{
				towrite = "@" + username + "\t" + content.replace("\n", "") + "\n";
			}
			System.out.println(towrite);

			
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

