package de.uni.due.de.getData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SearchSort;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.retrieval.params.TimeSpan;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

public class Crawl {

	public static void main(String[] args) throws IOException {
		// Initialize REST Client
		RestClient restClient = new HttpRestClient();
		restClient.setUserAgent("bot/1.0 by name");

		// Connect the user 
		User user = new User(restClient, "muchafel", "Julia123");
		try {
		    user.connect();
		} catch (Exception e) {
		    e.printStackTrace();
		}

		// Handle to Submissions, which offers the basic API submission functionality
		Submissions subms = new Submissions(restClient, user);
		List<String> queryTerms = new ArrayList<String>(Arrays.asList("capital+punishment", "death+penalty", "death+sentence"));
		Map<String,String> identifierToSubmission= new HashMap<String,String>();
		for(String queryTerm:queryTerms){
			crawlSubmissions(queryTerm,"changemyview",subms,identifierToSubmission);
		}
		printSubmission(identifierToSubmission);
	}

	private static void printSubmission(Map<String, String> identifierToSubmission) throws IOException {
		File targetCorpusFile= new File("combinedQueries_cmvs_justTargets.txt");
		for(String submissionIdentifier: identifierToSubmission.keySet()){
			FileUtils.write(targetCorpusFile, submissionIdentifier+"\t"+identifierToSubmission.get(submissionIdentifier)+"\n", "UTF-8", true);
		}
		
	}

	private static void crawlSubmissions(String queryTerm, String subreddit, Submissions subms, Map<String, String> identifierToSubmission) {
		String query=queryTerm+"&subreddit:"+subreddit;
		List<Submission> results= subms.search(query, null, SearchSort.COMMENTS, null, 1000, 99, null, null, true);
		
		int i=0;
		File cmvPosts_justTargets= new File(queryTerm+"_cmvs_justTargets.txt");
		File cmvPosts_fullXML= new File(queryTerm+"_cmvs_full.xml");
		for (Submission submission: results){
			System.out.println(i+"\t"+submission.getTitle()+ "\t"+ submission.getCommentCount() +"\t"+submission.getSelftext()); //" "+submission.getSelftext()
			try {
				identifierToSubmission.put(submission.getIdentifier(), submission.getTitle()+ "\t"+ submission.getCommentCount() + "\t"+submission.getPermalink());
				FileUtils.write(cmvPosts_justTargets, i+"\t"+submission.getTitle()+ "\t"+ submission.getCommentCount() + "\t"+submission.getPermalink()+"\n", "UTF-8", true);
				FileUtils.write(cmvPosts_fullXML,"<submission><id>"+ i+"</id><identifier>"+submission.getIdentifier()+"</identifier><title>"+submission.getTitle()+ "</title><commentCount>"+ submission.getCommentCount() + "</commentCount><permaLink>"+submission.getPermalink()+"</permaLink><selfText>"+submission.getSelftext()+"</selfText></submission> \n", "UTF-8", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
		
	}

}
