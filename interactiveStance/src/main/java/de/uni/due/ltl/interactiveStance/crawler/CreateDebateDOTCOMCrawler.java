package de.uni.due.ltl.interactiveStance.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
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
import org.openqa.selenium.support.ui.Select;

import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.DataSet;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class CreateDebateDOTCOMCrawler implements StanceCrawlerInstance {

	private String url;
	private File linkFile;
	private boolean addToExisting;
	private int paginationCounter=0;
	
	

	public CreateDebateDOTCOMCrawler(String url, File file) {
		this.url= url;
		this.linkFile=file;
		System.setProperty("webdriver.chrome.driver", "src/chromedriver");
	}

	@Override
	public void setURL(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void harvestDataPoints(StanceDB db) {
		List<String> debateLinks = null;
		try {
			if (linkFile != null) {
				debateLinks = FileUtils.readLines(linkFile, Charset.defaultCharset());
			} else {
				debateLinks = getDebateLinks();
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}

		for (String link : debateLinks) {
			DataSet dataSet = null;
			dataSet = new DataSet(link, getDataSetName(link), "http://www.createDebate.com/",
					new ArrayList<String>(Arrays.asList(getDataSetName(link))));

			try {
				db.addDataSet(dataSet);
				List<DataPoint> dataPoints = crawlAndAddDataPoints(link, dataSet, db);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
		
		
		private List<DataPoint> crawlAndAddDataPoints(String link, DataSet dataSet, StanceDB db) throws Exception {
			List<DataPoint> result= new ArrayList<DataPoint>();
			
			System.out.println(link);
			Document doc = Jsoup.connect(link).get();
			Elements favor = doc.getElementsByClass("debateSideBox sideL");
			Elements against = doc.getElementsByClass("debateSideBox sideR");
			
			
			for(Element favorBox: favor){
				for(Element argument: favorBox.children()){
					if(argument.hasClass("argBox argument")){
						for(Element argumentBody:argument.getElementsByClass("argBody").not("arg-threaded")){
//							System.out.println("+"+"\t"+argumentBody.text());
							String text = makeSQLConform(argumentBody.text());
							DataPoint point= new DataPoint(dataSet, text, "FAVOR");
							db.addDataPoint(point);
							result.add(point);
						}
					}
				}
			}
			
			for(Element againstBox: against){
				for(Element argument: againstBox.children()){
					if(argument.hasClass("argBox argument")){
						for(Element argumentBody:argument.getElementsByClass("argBody").not("arg-threaded")){
//							System.out.println("-"+"\t"+argumentBody.text());
							String text = makeSQLConform(argumentBody.text());
							DataPoint point= new DataPoint(dataSet, text, "AGAINST");
							db.addDataPoint(point);
							result.add(point);
						}
					}
				}
			}

			return result;
		}

		private String makeSQLConform(String text) {
			text=text.replace(System.lineSeparator()," ");
			text=text.replace("'","\\'");
			text=text.replace("\"","\\\"");
			
			return text;
		}
		
		
		
		private String getDataSetName(String link) {
			return link.split("createdebate.com/debate/show/")[1];
		}
	
	private List<String> getDebateLinks() throws InterruptedException, IOException {
		List<String> debateLinks = new ArrayList<>();
		WebDriver driver = new ChromeDriver();
		driver.get(url);
		clickForAndAgainst(driver);
		WebElement nextButton =getNextButton(driver);
		while(nextButton != null){
			System.out.println("Found Targets "+getLinksFromPage(driver).size());
			List<String> pagedLinks=getLinksFromPage(driver);
			debateLinks.addAll(pagedLinks);
			FileUtils.writeLines(new File("createDebateLinks.txt"), pagedLinks, true);
			nextButton.click();
			Thread.sleep(2000);
			
			nextButton =getNextButton(driver);
		}
		return debateLinks;
	}

	
	private List<String> getLinksFromPage(WebDriver driver) {
		List<String> links= new ArrayList<>();
		
		for(WebElement debate: driver.findElements(By.className("debate-item-top"))){
			if(debate.getText()== null || debate.getText().length()==0){
				continue;
			}
			System.out.println(debate.getText());
			List<WebElement> childs = debate.findElements(By.xpath(".//*"));

	        for (WebElement e  : childs){
	        	if(e.getAttribute("href") != null){
//	        		 System.out.println(e.getAttribute("href"));
	        		 links.add(e.getAttribute("href"));
	        		 break;
	        	}
	        }
		}  
		return links;
	}
	
	
	private void clickForAndAgainst(WebDriver driver) throws InterruptedException {
		WebElement dropDownListBoxDebates = driver.findElement(By.cssSelector("#nav_form > select:nth-child(3)"));
		Select select1 = new Select(dropDownListBoxDebates); 
		select1.selectByVisibleText("For/Against Debates");
		Thread.sleep(2000);
		WebElement dropDownListBoxStatus = driver.findElement(By.cssSelector("#nav_form > select:nth-child(4)"));
		Select select2 = new Select(dropDownListBoxStatus); 
		select2.selectByVisibleText("Both");
		Thread.sleep(2000);
	}

	private WebElement getNextButton(WebDriver driver) {
		WebElement nextButton=null;
		if(paginationCounter==0){
			nextButton=driver.findElement(By.cssSelector("#content_wide > div > div:nth-child(6) > a"));
		}
		else {
			nextButton=driver.findElement(By.cssSelector("#content_wide > div > div:nth-child(6) > a:nth-child(2)"));
		}
		
		System.out.println(paginationCounter);
		paginationCounter++;
		return nextButton;
	}
	

}
