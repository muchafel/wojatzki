package de.unidue.ltl.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CrowdflowerJsonReader_Judgements {

	private Map<Integer,List<Judgement>>id2judgments;
	private Map<Integer,Demographics>id2demographics;
	private Map<Integer,Integer> participantId2Count;
	private Map<String, Set<String>> issue2Assertions = new HashMap<>();
	public CrowdflowerJsonReader_Judgements(File jsonFile_judgments, File jsonFile_demographics) throws JSONException, IOException {
		
		id2demographics=getDemographicMapping(jsonFile_demographics);
		id2judgments= getJudmentMapping(jsonFile_judgments);
		
	}
	private Map<Integer, List<Judgement>> getJudmentMapping(File jsonFile_judgments) throws JSONException, IOException {
		Map<Integer,List<Judgement>> id2judgments= new HashMap<>();
		participantId2Count = new HashMap<>();
		
		for(String line: FileUtils.readLines(jsonFile_judgments)){
			JSONObject obj = new JSONObject(line);
			String issue = obj.getJSONObject("data").getString("issue");
			String assertionA = obj.getJSONObject("data").getString("tuple_item_a");
			String assertionB = obj.getJSONObject("data").getString("tuple_item_b");
			String assertionC = obj.getJSONObject("data").getString("tuple_item_c");
			String assertionD = obj.getJSONObject("data").getString("tuple_item_d");
			addAssertions(issue,assertionA,assertionB,assertionC,assertionD);
//			System.out.println(issue);
			
			JSONArray arr =obj.getJSONObject("results").getJSONArray("judgments");
			for (int i = 0; i < arr.length(); i++){
				Judgement judgment= new Judgement(arr.getJSONObject(i).getInt("worker_id"));
				if(arr.getJSONObject(i).getJSONObject("data").getString("assertion_1_tuple_item_a").equals("Disagree")){
					judgment.addDisAgree(assertionA);
				}else{
					judgment.addAgree(assertionA);
				}
				if(arr.getJSONObject(i).getJSONObject("data").getString("assertion_2_tuple_item_b").equals("Disagree")){
					judgment.addDisAgree(assertionB);
				}else{
					judgment.addAgree(assertionB);
				}
				if(arr.getJSONObject(i).getJSONObject("data").getString("assertion_3_tuple_item_c").equals("Disagree")){
					judgment.addDisAgree(assertionC);
				}else{
					judgment.addAgree(assertionC);
				}
				if(arr.getJSONObject(i).getJSONObject("data").getString("assertion_4_tuple_item_d").equals("Disagree")){
					judgment.addDisAgree(assertionD);
				}else{
					judgment.addAgree(assertionD);
				}
				//TODO this is somewhat hacked
				if(arr.getJSONObject(i).getJSONObject("data").has("q2_which_of_these_assertions_on_the_issue_issue_do_you_support_the_most_or_oppose_the_least")&& arr.getJSONObject(i).getJSONObject("data").has("q3_which_of_these_assertions_on_the_issue_issue_do_you_oppose_the_most_or_support_the_least")){
					String best=arr.getJSONObject(i).getJSONObject("data").getString("q2_which_of_these_assertions_on_the_issue_issue_do_you_support_the_most_or_oppose_the_least");
					String worst=arr.getJSONObject(i).getJSONObject("data").getString("q3_which_of_these_assertions_on_the_issue_issue_do_you_oppose_the_most_or_support_the_least");
					if(!best.equals(worst)){
						judgment.addBest(best.substring(13));
						judgment.addWorst(worst.substring(13));
					}else{
						judgment.incFaultyBWSTuple();
					}
				}
			
				if(id2judgments.containsKey(judgment.getId())){
					id2judgments.get(judgment.getId()).add(judgment);
				}else{
					List<Judgement> judgments=new ArrayList<>();
					judgments.add(judgment);
					id2judgments.put(judgment.getId(), judgments);
				}
				
				if(participantId2Count.containsKey(judgment.getId())){
					int oldCount=participantId2Count.get(judgment.getId());
//					System.out.println("old count "+oldCount);
					oldCount+=1;
					participantId2Count.put(judgment.getId(),oldCount);
				}else{
					participantId2Count.put(judgment.getId(),1);
				}
				
			}
//			participantId2Count.put(key, value)
			

		}
		
		return id2judgments;
	}
	private void addAssertions(String issue, String assertionA, String assertionB, String assertionC,
			String assertionD) {
		if(issue2Assertions.containsKey(issue)){
			issue2Assertions.get(issue).add(assertionA);
			issue2Assertions.get(issue).add(assertionB);
			issue2Assertions.get(issue).add(assertionC);
			issue2Assertions.get(issue).add(assertionD);
		}else{
			Set<String> assertions=new LinkedHashSet<>();
			assertions.add(assertionA);
			assertions.add(assertionB);
			assertions.add(assertionC);
			assertions.add(assertionD);
			issue2Assertions.put(issue, assertions);
		}
		
	}
	private Map<Integer, Demographics> getDemographicMapping(File jsonFile_demographics) throws JSONException, IOException {
		Map<Integer,Demographics> id2demographics= new HashMap<>();
		for(String line: FileUtils.readLines(jsonFile_demographics)){
			JSONObject obj = new JSONObject(line);
			JSONArray arr =obj.getJSONObject("results").getJSONArray("judgments");
			for (int i = 0; i < arr.length(); i++){
				Demographics demographics= new Demographics();
				demographics.setId(arr.getJSONObject(i).getInt("worker_id"));
				demographics.setGender(arr.getJSONObject(i).getJSONObject("data").getString("q1_what_is_your_gender"));
				demographics.setAge(arr.getJSONObject(i).getJSONObject("data").getInt("q2_how_old_are_you"));
				demographics.setAffiliation(arr.getJSONObject(i).getJSONObject("data").getString("affiliation"));
				demographics.setEductaion(arr.getJSONObject(i).getJSONObject("data").getString("q3_what_is_the_highest_level_of_education_you_have_completed"));
				demographics.setRace(arr.getJSONObject(i).getJSONObject("data").getString("q8_what_describes_you_best"));
				if(arr.getJSONObject(i).getJSONObject("data").getString("religion").equals("other")){
					demographics.setReligion("other:"+arr.getJSONObject(i).getJSONObject("data").getString("indicate_your_present_religion"));
				}else{
					demographics.setReligion(arr.getJSONObject(i).getJSONObject("data").getString("religion"));
				}
				demographics.setUsCitizen(arr.getJSONObject(i).getJSONObject("data").getString("q6_are_you_a_us_citizen"));
				if(arr.getJSONObject(i).getJSONObject("data").getString("family").equals("other")){
					demographics.setFamilyStauts("other:"+arr.getJSONObject(i).getJSONObject("data").getString("indicate_your_family_status"));
				}else{
					demographics.setFamilyStauts(arr.getJSONObject(i).getJSONObject("data").getString("family"));
				}
				//
				if(arr.getJSONObject(i).getJSONObject("data").getString("profession").equals("other")){
					demographics.setProfession("other:"+arr.getJSONObject(i).getJSONObject("data").getString("indicate_what_you_professionally_do"));
				}else{
					demographics.setProfession(arr.getJSONObject(i).getJSONObject("data").getString("profession"));
				}
				demographics.setTies2Overseas(arr.getJSONObject(i).getJSONObject("data").getString("q9_do_you_have_friends_or_family_that_live_in_countries_other_than_the_us"));
//				System.out.println(demographics.prettyPrint());
				id2demographics.put(demographics.getId(), demographics);
			}
		}
		return id2demographics;
	}
	public Map<Integer, List<Judgement>> getId2judgments() {
		return id2judgments;
	}
	public Map<Integer, Demographics> getId2demographics() {
		return id2demographics;
	}
	public Map<String, Set<String>> getIssue2Assertions() {
		return issue2Assertions;
	}
	public Map<Integer, Integer> getParticipantId2Count() {
		return participantId2Count;
	}

}
