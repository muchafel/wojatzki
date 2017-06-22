package de.uni.due.ltl.interactiveStance.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.DataSet;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

/**
 * TODO crawling is not finished (only likns are crawled yet)
 * We need to think about how we treat the quite different data (--> we should probably use yes and no points)
 * @author michael
 *
 */
public class DebateWiseDOTORGCrawler implements StanceCrawlerInstance {

	private String url;
	private File linkFile;
	
	public DebateWiseDOTORGCrawler(String url, File file) {
		this.url=url;
		this.linkFile=linkFile;
		System.setProperty("webdriver.chrome.driver", "src/chromedriver");
		
	}

	@Override
	public void setURL(String url) {
		this.url=url;

	}

	@Override
	public String getURL() {
		return url;
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
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(debateLinks.size());
		for(String link : debateLinks){
			DataSet dataSet = new DataSet(link, getDataSetName(link), "http://www.debatewise.org/",new ArrayList<String>(Arrays.asList(getDataSetName(link))));
			
//			try {
//				db.addDataSet(dataSet);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			List<DataPoint> dataPoints= crawlAndAddDataPoints(link,dataSet,db);
		}
	}

	private List<DataPoint> crawlAndAddDataPoints(String link, DataSet dataSet, StanceDB db) {
		// TODO crawl data
		return null;
	}

	private String getDataSetName(String link) {
		String name= link.split("http://debatewise.org/debates/")[1];
		return name;
	}

	private List<String> getDebateLinks() throws IOException, InterruptedException {
		List<String> debateLinks = new ArrayList<>();
		WebDriver driver = new ChromeDriver();
		driver.get(url);
		int i=0;
		for(int j=0; j<65; j++){
			if(j==0){
				List<String> pagedLinks=getLinksFromPage(driver);
				debateLinks.addAll(pagedLinks);
				FileUtils.writeLines(new File("debateWiseLinks.txt"), pagedLinks, true);
				Thread.sleep(2000);
			}else{
				driver.navigate().to(url+"/page/"+String.valueOf(j)+"/");
				List<String> pagedLinks=getLinksFromPage(driver);
				debateLinks.addAll(pagedLinks);
				FileUtils.writeLines(new File("debateWiseLinks.txt"), pagedLinks, true);
				Thread.sleep(2000);
			}
		}
		
		return debateLinks;
	}

	private List<String> getLinksFromPage(WebDriver driver) {
		List<String> links= new ArrayList<>();
		for (WebElement debate : driver.findElements(By.className("hometitle"))) {
			if (debate.getText() == null || debate.getText().length() == 0) {
				continue;
			}
			System.out.println(debate.findElement(By.tagName("a")).getAttribute("href"));
			links.add(debate.findElement(By.tagName("a")).getAttribute("href"));
		}
		return links;
	}

	private WebElement getNextButton(WebDriver driver, int i) {
//		if(i<2){
//			return driver.findElement(By.cssSelector("#content > div > div.navigation > ol > li:nth-child(9) > a"));
//		}else if(i>1 && i<63){
//			return driver.findElement(By.cssSelector("#content > div > div.navigation > ol > li:nth-child(10) > a"));
//		}else{
//			return driver.findElement(By.cssSelector("#content > div > div.navigation > ol > li:nth-child(9) > a"));
//		}
		if(i<=7){
			return driver.findElement(By.cssSelector("#content > div > div.navigation > ol")).findElement(By.className("next"));
		}else{
			return driver.findElement(By.cssSelector("#content > div > div.navigation > ol > li:nth-child(9) > a"));
		}
		
//		return driver.findElement(By.className("next"));
		
	}

}
