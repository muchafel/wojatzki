package de.uni_due.ltl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * java object that handls the xml configuration (hashtags to crawl, target location, tweet location) in config/crawlerConfig.xml
 * @author michael
 *
 */
public class Config {
	private List<String> hashtags;
	private String tweetFolder;
	private String targetFile;
	public Config(){
		
		hashtags = new ArrayList<String>();
		try{
		File fXmlFile = new File("config/crawlerConfig.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList xmlHashtagList = doc.getElementsByTagName("hashtag");
		
		//get #s
		for (int temp = 0; temp < xmlHashtagList.getLength(); temp++){
			String hashtag=xmlHashtagList.item(temp).getTextContent();
			this.hashtags.add(hashtag);
		}
		
		//get target file
		NodeList xmlTargetList = doc.getElementsByTagName("target");
		for (int temp = 0; temp < xmlTargetList.getLength(); temp++){
			this.targetFile=xmlTargetList.item(temp).getTextContent();
		}
		
		//get folder
		NodeList xmlTweetFolderList = doc.getElementsByTagName("rawTweetLocation");
		for (int temp = 0; temp < xmlTweetFolderList.getLength(); temp++){
			this.tweetFolder=xmlTweetFolderList.item(temp).getTextContent();
		}
		
		}
		catch (Exception e ){
			e.printStackTrace();
		}
	}
	
	public String[] getHashtags() {
		 
		return (String[]) hashtags.toArray( new String[hashtags.size()]);
	}
	public List<String> getHashtagList() {
		
		return hashtags;
	}
	
	private void createFolder(String folder) {

		try {
			File dir = new File(folder);
			dir.mkdirs();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public String getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	public String getTweetFolder() {
		return tweetFolder;
	}

	public void setTweetFolder(String tweetFolder) {
		this.tweetFolder = tweetFolder;
	}
}
