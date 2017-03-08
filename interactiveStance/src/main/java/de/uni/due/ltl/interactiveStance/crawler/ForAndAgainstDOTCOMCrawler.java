package de.uni.due.ltl.interactiveStance.crawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import de.uni.due.ltl.interactiveStance.common.Data_Point;

public class ForAndAgainstDOTCOMCrawler implements StanceCrawlerInstance {

	private String url;
	private int paginationCounter=0;
	
	
	public ForAndAgainstDOTCOMCrawler(String url) {
		this.url = url;
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
	public List<Data_Point> harvestDataPoints() {
		try {
			List<String> debateLinks= getDebateLinks();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
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

	private boolean isArticle(Element debate) {
		String[] parts= debate.attr("href").split("/");
		if(parts.length>1 && parts[1].equals("articles")){
			return true;
		}
		return false;
	}

}
