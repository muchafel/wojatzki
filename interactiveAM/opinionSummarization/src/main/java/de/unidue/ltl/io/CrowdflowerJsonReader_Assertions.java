package de.unidue.ltl.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CrowdflowerJsonReader_Assertions {

	Map<String,IssueAssertions> assertion;
	
	public CrowdflowerJsonReader_Assertions(File jsonFile) throws JSONException, IOException {
		assertion= new HashMap<>();
		for(String line: FileUtils.readLines(jsonFile)){
			JSONObject obj = new JSONObject(line);
			JSONObject issue = obj.getJSONObject("data");
			assertion.put(issue.get("issue").toString(), createAssertionsFromJson(obj));
		}
	}

	private IssueAssertions createAssertionsFromJson(JSONObject obj) {
		List<String> assertion_list = new ArrayList<>();
		Set<String> assertion_set =  new HashSet<>();
		Set<String> setOfVariables=  new HashSet<>();
		JSONArray arr =obj.getJSONObject("results").getJSONArray("judgments");
		for (int i = 0; i < arr.length(); i++){
			assertion_set.add((arr.getJSONObject(i).getJSONObject("data").getString("q3_enter_statement_1_about_issue")));
			assertion_set.add((arr.getJSONObject(i).getJSONObject("data").getString("q4_enter_statement_2_about_issue")));
			assertion_set.add((arr.getJSONObject(i).getJSONObject("data").getString("q5_enter_statement_3_about_issue")));
			assertion_set.add((arr.getJSONObject(i).getJSONObject("data").getString("q6_enter_statement_4_about_issue")));
			assertion_set.add((arr.getJSONObject(i).getJSONObject("data").getString("q7_enter_statement_5_about_issue")));
			assertion_list.add((arr.getJSONObject(i).getJSONObject("data").getString("q3_enter_statement_1_about_issue")));
			assertion_list.add((arr.getJSONObject(i).getJSONObject("data").getString("q4_enter_statement_2_about_issue")));
			assertion_list.add((arr.getJSONObject(i).getJSONObject("data").getString("q5_enter_statement_3_about_issue")));
			assertion_list.add((arr.getJSONObject(i).getJSONObject("data").getString("q6_enter_statement_4_about_issue")));
			assertion_list.add((arr.getJSONObject(i).getJSONObject("data").getString("q7_enter_statement_5_about_issue")));
			setOfVariables.addAll(getLines(arr.getJSONObject(i).getJSONObject("data").getString("q8_what_groups_of_people_are_likely_to_have_a_similar_position_on_the_issue_or_what_characteristics_of_a_person_are_likely_to_influence_wether_they_agree_or_disagree_with_the_statements_relevant_to_this_issue_you_can_enter_multiple_groups_by_writing_each_group_into_a_separate_line")));
		}

		
		return new IssueAssertions(assertion_list, assertion_set, setOfVariables);
	}

	private Collection<String> getLines(String string) {
		return new HashSet<String>(Arrays.asList(string.split("\n")));
	}

	public Map<String, IssueAssertions> getAssertion() {
		return assertion;
	}

	public void setAssertion(Map<String, IssueAssertions> assertion) {
		this.assertion = assertion;
	}

}
