package de.unidue.ltl.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReadCrowdflowerJSON_Assertions {
	public static void main(String[] args) throws JSONException, IOException {
		File jsonFile = new File("/Users/michael/Downloads/job_1187822 6.json");
		CrowdflowerJsonReader_Assertions reader = new CrowdflowerJsonReader_Assertions(jsonFile);
		// System.out.println(reader.getAssertion().get("Obama Care --
		// Affordable Health Care Act").getSetOfVariables());
		for (String issue : reader.getAssertion().keySet()) {
			writeResults(issue, reader.getAssertion());
		}
	}

	private static void writeResults(String issue, Map<String, IssueAssertions> map) throws IOException {
		writeFile("src/main/resources/assertions/" + issue + "_demographics.txt", map.get(issue).getSetOfVariables(),
				true);
		writeFile("src/main/resources/assertions/" + issue + "_fullList.txt", map.get(issue).getAssertion_list(),
				false);
		writeFile("src/main/resources/assertions/" + issue + "_removedRedundancy.txt",
				map.get(issue).getAssertion_set(), true);

	}

	private static void writeFile(String file, Collection<String> setToWrite, boolean doValidation) throws IOException {
		FileWriter writer = new FileWriter(file);
		for (String str : setToWrite) {
			if (doValidation) {
				if (valid(str)) {
					writer.write(str + "\n");
				}
			} else {
				writer.write(str + "\n");
			}
		}
		writer.close();

	}

	private static boolean valid(String str) {
		if (str.split(" ").length > 2) {
			return true;
		} else if (str.length() > 10) {
			return true;
		}
		return false;
	}
}
