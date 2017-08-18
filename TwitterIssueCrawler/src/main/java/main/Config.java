package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Config {

	private List<String> hashtags;
	public Config(String path) throws ParserConfigurationException, SAXException, IOException{
		
		hashtags = new ArrayList<String>();
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("hashtag");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			String hashtag = nList.item(temp).getTextContent();
			hashtags.add(hashtag);
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

}
