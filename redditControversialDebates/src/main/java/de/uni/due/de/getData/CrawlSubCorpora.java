package de.uni.due.de.getData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.maven.model.CiManagement;

import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.Comments;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.CommentSort;
import com.github.jreddit.retrieval.params.SearchSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

public class CrawlSubCorpora {

	static List<String> dpForHeinousCrimes = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/1klx2i/i_believe_that_capital_punishment_is_necessary/?ref=search_posts",
			"/r/changemyview/comments/3gwou8/cmv_sexual_predators_who_prey_upon_minors_should/?ref=search_posts",
			"/r/changemyview/comments/3erhhr/cmvthose_positively_identified_and_convicted_of/?ref=search_posts",
			"/r/changemyview/comments/1ktd4e/i_believe_all_pedophiles_should_be_given_the/?ref=search_posts",
			"/r/changemyview/comments/1q2av0/i_believe_that_capital_punishment_is_a_just/?ref=search_posts",
			"/r/changemyview/comments/2xxerp/cmv_i_think_murderers_pedophiles_and_violent/?ref=search_posts",
			"/r/changemyview/comments/214oud/convictions_of_serious_crimes_like_rape/?ref=search_posts",
			"/r/changemyview/comments/1fzlwq/i_believe_that_the_use_of_capital_punishment_is_a/?ref=search_posts",
			"/r/changemyview/comments/1fy05t/i_think_death_penalty_should_be_reserved_for/?ref=search_posts",
			"/r/changemyview/comments/1hoi97/i_believe_that_so_long_as_america_has_capital/?ref=search_posts"));

	static List<String> dpByGunShot = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/26pm3o/cmv_using_a_bullet_to_execute_an_inmate_is_no/?ref=search_posts",
			"/r/changemyview/comments/4du1rv/cmv_the_primary_method_of_capital_punishment/?ref=search_posts",
			"/r/changemyview/comments/41a86a/cmv_why_dont_we_conduct_statesanctioned_capital/?ref=search_posts"));

	static List<String> ifDPAbortion = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/4dgzo8/cmv_someone_who_supports_the_abolishment_of_the/?ref=search_posts",
			"/r/changemyview/comments/1xf9jf/i_believe_it_is_unjustifiable_to_support_abortion/?ref=search_posts",
			"/r/changemyview/comments/25l2yq/cmv_it_is_not_hypocritical_to_oppose_abortion_but/?ref=search_posts"));

	static List<String> dpCandiates4ExperimentsOrganDonation = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/1d3gyx/i_believe_that_death_row_inmates_should_be/?ref=search_posts",
			"/r/changemyview/comments/3173dl/cmv_people_sentenced_to_death_should_be_used_in/?ref=search_posts"));

	static List<String> ifDPEuthansia = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/2hsd0n/cmv_any_country_state_city_etc_which_legalizes/?ref=search_posts"));

	static List<String> requiredLevelOfCertainityIsUnachievable = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/2265kk/cmv_i_think_that_capital_punishment_requires_a/?ref=search_posts",
			"/r/changemyview/comments/22ss7b/cmv_the_death_penalty_can_be_justified_but_the/?ref=search_posts"));

	static List<String> replaceLifeLongWithDP = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/31n763/cmvlifelong_prison_sentences_should_be_banned_in/?ref=search_posts",
			"/r/changemyview/comments/21q36s/cmv_anyone_given_life_in_prison_should_be_given/?ref=search_posts",
			"/r/changemyview/comments/1rddqv/i_believe_those_sentenced_to_life_in_prison/?ref=search_posts",
			"/r/changemyview/comments/20tr54/cmv_no_argument_for_death_penalty_is_moral_or/?ref=search_posts"));

	static List<String> reduceNumberOrPermitAppeals = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/2dygof/cmvdeath_row_inmates_should_either_not_be_allowed/?ref=search_posts",
			"/r/changemyview/comments/3mjaet/cmv_if_someone_admits_that_they_are_guilty_of_a/?ref=search_posts"));

	static List<String> dpMoreOften4aBetterSociety = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/4gapy2/cmv_the_death_penalty_should_be_enforced_against/?ref=search_posts",
			"/r/changemyview/comments/1ek9g7/the_death_penalty_should_be_widely_implemented/?ref=search_posts",
			"/r/changemyview/comments/3wpctt/cmvi_believe_the_death_penalty_should_be_legal/?ref=search_posts",
			"/r/changemyview/comments/3g9x7b/cmv_jail_sentences_and_the_death_penalty_should/?ref=search_posts"));

	/**
	 * /r/changemyview/comments/2bk0qs/cmv_i_believe_that_in_certain_cases_capital/?ref=search_posts
/r/changemyview/comments/31ocvw/cmv_the_death_penalty_isnt_a_harsh_enough_penalty/?ref=search_posts
	 */
	static List<String> moreHarshOrInhumaneDPDesired = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/2bk0qs/cmv_i_believe_that_in_certain_cases_capital/?ref=search_posts",
			"/r/changemyview/comments/31ocvw/cmv_the_death_penalty_isnt_a_harsh_enough_penalty/?ref=search_posts"));

	static List<String> dpHasANegativeImpactOnHumanPsyche = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/3s19v4/cmv_being_prodeath_penalty_is_immoral_especially/?ref=search_posts"));

	static List<String> dpByHypoxia = new ArrayList<String>(Arrays
			.asList("/r/changemyview/comments/4u5b66/cmv_if_we_must_use_the_death_penalty_the/?ref=search_posts"));

	static List<String> thereIsNoHumandFormOfDP = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/2sbfi7/cmv_there_is_currently_no_humane_form_of_capital/?ref=search_posts"));

	static List<String> ifNoDpThanNoOtherStateLethalForce = new ArrayList<String>(Arrays
			.asList("/r/changemyview/comments/1zhoyc/if_death_penalty_opponents_were_ideologically/?ref=search_posts"));

	static List<String> ifDPisReallyDetereedItIsImmoralToOpposeIt = new ArrayList<String>(Arrays
			.asList("/r/changemyview/comments/31opwo/cmv_if_studies_were_to_show_that_the_death/?ref=search_posts"));

	static List<String> dpByElectricChair = new ArrayList<String>(Arrays.asList(
			"/r/changemyview/comments/1e7dkp/i_think_murderers_should_get_the_electric_chair/?ref=search_posts"));

	public static void main(String[] args) {
		
		Map<String,List<String>> listOfTargets= new HashMap<String,List<String>>();
		listOfTargets.put("dpForHeinousCrimes", dpForHeinousCrimes);
		listOfTargets.put("ifDPAbortion", ifDPAbortion);
		listOfTargets.put("dpByGunShot", dpByGunShot);
		listOfTargets.put("dpCandiates4ExperimentsOrganDonation", dpCandiates4ExperimentsOrganDonation);
		listOfTargets.put("dpByElectricChair", dpByElectricChair);
		listOfTargets.put("dpByHypoxia", dpByHypoxia);
		listOfTargets.put("dpHasANegativeImpactOnHumanPsyche", dpHasANegativeImpactOnHumanPsyche);
		listOfTargets.put("dpMoreOften4aBetterSociety", dpMoreOften4aBetterSociety);
		listOfTargets.put("thereIsNoHumandFormOfDP", thereIsNoHumandFormOfDP);
		listOfTargets.put("ifDPEuthansia", ifDPEuthansia);
		listOfTargets.put("ifDPisReallyDetereedItIsImmoralToOpposeIt", ifDPisReallyDetereedItIsImmoralToOpposeIt);
		listOfTargets.put("ifNoDpThanNoOtherStateLethalForce", ifNoDpThanNoOtherStateLethalForce);
		listOfTargets.put("reduceNumberOrPermitAppeals", reduceNumberOrPermitAppeals);
		listOfTargets.put("replaceLifeLongWithDP", replaceLifeLongWithDP);
		listOfTargets.put("requiredLevelOfCertainityIsUnachievable", requiredLevelOfCertainityIsUnachievable);
		listOfTargets.put("moreHarshOrInhumaneDPDesired",moreHarshOrInhumaneDPDesired);
		
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

		// Handle to Comments, which offers the basic API functionality
		Comments coms = new Comments(restClient, user);

		// Retrieve comments of a submission
//		String id = "1klx2i";
		
		Submissions subms = new Submissions(restClient, user);
		String queryTerm="death+penalty";
		List<Submission> results= subms.search(queryTerm+"&subreddit:changemyview", null, SearchSort.COMMENTS, null, 1000, 99, null, null, true);
		
		
		for(String target:listOfTargets.keySet()){
			for(String link: listOfTargets.get(target)){
				String id= getIdFromLink(link);
				System.out.println(id);
				printSubmissionCorpus(null, target, id, coms);
				
//				for(Submission submission: results){
////					if(submission.getIdentifier().equals(id)){
//						printSubmissionCorpus(submission,target,id,coms);
////					}
//				}
			}
		}
	}

	private static String getIdFromLink(String link) {
		return link.split("/")[4];
	}

	private static void printSubmissionCorpus(Submission submission, String target, String id, Comments coms) {
		List<Comment> commentsSubmission = coms.ofSubmission(id, null, 0, 8, 300, CommentSort.TOP);
		RedditSubmission rSubmission = new RedditSubmission();
//		rSubmission.setSelfText(submission.getSelftext());
//		rSubmission.setId(id);
//		rSubmission.setPermaLink(submission.getPermalink());
//		System.out.println(rSubmission.getPermaLink());
//		rSubmission.setCommentCount(submission.getCommentCount());

		List<RedditComment> comments = new ArrayList<>();
		for (Comment comment : commentsSubmission) {
//			System.out.println(comment.getAuthor() + " : " + comment.getBody());
			RedditComment commi = getRecursiveReplies(comment);
			//
			// for(Comment reply: comment.getReplies()){
			// System.out.println("\t >"+reply.getAuthor()+" :
			// "+reply.getBody());
			// }
			comments.add(commi);
		}
		rSubmission.setRedditComments(comments);
		System.out.println("----");

		try {
			// if the directory does not exist, create it
			File targetFolder = new File("src/main/resources/"+target);
			if (!targetFolder.exists()) {
			        targetFolder.mkdir();
			    } 
			
			File file = new File(targetFolder.getAbsolutePath()+"/" + id + ".xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(RedditSubmission.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(rSubmission, file);
			jaxbMarshaller.marshal(rSubmission, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		
	}

	/**
	 * recursivly returns the replies
	 * 
	 * @param comment
	 * @return
	 */
	private static RedditComment getRecursiveReplies(Comment comment) {
		RedditComment commi = new RedditComment();
		commi.setId(comment.getIdentifier());
		commi.setAuthor(comment.getAuthor());
		commi.setBody(comment.getBody());
		commi.setDownvotes(comment.getDownvotes());
		commi.setUpvotes(comment.getUpvotes());
		List<RedditComment> replies = new ArrayList<RedditComment>();
		for (Comment reply : comment.getReplies()) {
			replies.add(getRecursiveReplies(reply));
		}
		commi.setReplies(replies);
		return commi;
	}

}
