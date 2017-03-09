package de.uni.due.ltl.interactiveStance.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.DataSet;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class ForAndAgainstDOTCOMCrawler implements StanceCrawlerInstance {

	private String url;
	private int paginationCounter=0;
	private File linkFile;
	
	
	public ForAndAgainstDOTCOMCrawler(String url, File linkFile) {
		this.url = url;
		this.linkFile=linkFile;
		System.setProperty("webdriver.chrome.driver", "src/chromedriver");
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
		List<String> debateLinks= null;
		try {
			if(linkFile != null){
				 debateLinks= FileUtils.readLines(linkFile,Charset.defaultCharset());
			}else{
				debateLinks= getDebateLinks();
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		System.out.println(debateLinks.size());
		for(String link : debateLinks){
			// at first we create a data set without the # of against and favor instances (maybe we should do this in the DB)
			DataSet dataSet = new DataSet(link, getDataSetName(link), "http://www.forandagainst.com/",
					new ArrayList<String>(Arrays.asList(getDataSetName(link))), 0, 0);
			
			try {
				db.addDataSet(dataSet);
				List<DataPoint> dataPoints= crawlAndAddDataPoints(link,dataSet,db);
			} catch ( Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private List<DataPoint> crawlAndAddDataPoints(String link, DataSet dataSet, StanceDB db) throws Exception {
		List<DataPoint> result= new ArrayList<DataPoint>();
		int numberOfFavor=0;
		int numberOfAgainst=0;
		
		Document doc = Jsoup.connect(link).get();
        Elements favorTable = doc.select("body > table > tbody > tr > td:nth-child(1) > table:nth-child(2) > tbody > tr:nth-child(10) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr");
        Elements againstTable = doc.select("body > table > tbody > tr > td:nth-child(1) > table:nth-child(2) > tbody > tr:nth-child(10) > td > table > tbody > tr > td:nth-child(3) > table > tbody > tr");
        for(Element explicitStance: favorTable.select("table")){
        	String text= explicitStance.select("tbody > tr:nth-child(1) > td:nth-child(2)").text();
        	if(text.isEmpty())continue;
        	text=makeSQLConform(text);
        	DataPoint point= new DataPoint(dataSet, text, "FAVOR");
        	result.add(point);
        	numberOfFavor++;
        	System.out.println(text);
        	db.addDataPoint(point);
        }
        for(Element explicitStance: againstTable.select("table")){
        	String text= explicitStance.select("tbody > tr:nth-child(1) > td:nth-child(2)").text();
        	if(text.isEmpty())continue;
        	text=makeSQLConform(text);
        	DataPoint point= new DataPoint(dataSet, text, "AGAINST");
        	result.add(point);
        	numberOfAgainst++;
        	db.addDataPoint(point);
        	System.out.println(text);
        }
        dataSet.setNumberOfAgainstInstances(numberOfAgainst);
        dataSet.setNumberOfFavorInstances(numberOfFavor);
        db.updateDatSetNumberOfInstances(dataSet);
        
		return result;
	}

	private String makeSQLConform(String text) {
		text=text.replace(System.lineSeparator()," ");
		text=text.replace("'","\\'");
		text=text.replace("\"","\\\"");
		
		return text;
	}

	private String getDataSetName(String link) {
		return link.split("forandagainst.com/")[1];
	}

	private List<String> getDebateLinks() throws InterruptedException, IOException {
		List<String> debateLinks = new ArrayList<>();
		WebDriver driver = new ChromeDriver();
		driver.get(url);
		driver.findElement(By.cssSelector("#menulayer1 > table > tbody > tr:nth-child(1) > td:nth-child(1) > div > table > tbody > tr > td:nth-child(5) > a")).click();
		Thread.sleep(2000);
		WebElement nextButton =getNextButton(driver);
		while(nextButton != null){
			System.out.println("Found Targets "+getLinksFromPage(driver).size());
			List<String> pagedLinks=getLinksFromPage(driver);
			debateLinks.addAll(pagedLinks);
			FileUtils.writeLines(new File("favorAgainstLinks.txt"), pagedLinks, true);
			nextButton.click();
			Thread.sleep(2000);
			
			nextButton =getNextButton(driver);
		}
		return debateLinks;
	}

	
	private WebElement getNextButton(WebDriver driver) {
		WebElement nextButton=null;
		if(paginationCounter==0){
			nextButton=driver.findElement(By.cssSelector("#alldebates > table > tbody > tr:nth-child(21) > td > table > tbody > tr > td:nth-child(11) > a"));
		}
		else if(0<paginationCounter && paginationCounter <6){
			nextButton=driver.findElement(By.cssSelector("#alldebates > table > tbody > tr:nth-child(21) > td > table > tbody > tr > td:nth-child(11) > a"));
		}
		else if(5<paginationCounter && paginationCounter<2174){
			nextButton=driver.findElement(By.cssSelector("#alldebates > table > tbody > tr:nth-child(21) > td > table > tbody > tr > td:nth-child(14) > a"));
		}
		else if(2174<paginationCounter && paginationCounter<2178){
			nextButton=driver.findElement(By.cssSelector("#alldebates > table > tbody > tr:nth-child(21) > td > table > tbody > tr > td:nth-child(13) > a"));
		}
		else{
			return null;
		}
		System.out.println(paginationCounter);
		paginationCounter++;
		return nextButton;
	}

	private List<String> getLinksFromPage(WebDriver driver) {
		List<String> links= new ArrayList<>();
		for(WebElement debate: driver.findElements(By.className("debate_title"))){
			if(!isArticle(debate)){
				if(debate.getText()== null || debate.getText().length()==0){
					continue;
				}
				links.add(debate.getAttribute("href"));
//				System.out.println(debate.getText());
			}
		}  
		return links;
	}

	private boolean isArticle(WebElement debate) {
		String[] parts= debate.getAttribute("href").split("/");
		if(parts.length>1 && parts[1].equals("articles")){
			return true;
		}
		return false;
	}


}
