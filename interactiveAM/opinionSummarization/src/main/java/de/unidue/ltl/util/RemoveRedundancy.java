package de.unidue.ltl.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.spi.IIOServiceProvider;

import org.apache.commons.io.FileUtils;

public class RemoveRedundancy {

	public static void main(String[] args) throws IOException {
		File file= new File("/Users/michael/Dropbox/nrc/assertions_final.txt");
		File toWrite = new File("src/main/resources/assertions_final_removedRedundancy.txt");
		Map<String,Set<String>>issueToUniqueAssertions= new HashMap<>();
		for(String line: FileUtils.readLines(file)){
			String issue=line.split("\t")[1];
			String assertion=line.split("\t")[0];
			issueToUniqueAssertions=processLine(issue,issueToUniqueAssertions,assertion);
		}

		for(String issue: issueToUniqueAssertions.keySet()){
			File issueFile= new File("src/main/resources/"+issue+".txt");
			for(String assertion: issueToUniqueAssertions.get(issue)){
				String text = assertion + "\t" + issue + "\n";
				FileUtils.writeStringToFile(toWrite, text, "UTF-8");
				FileUtils.writeStringToFile(issueFile, assertion+"\n", "UTF-8");
			}
		}
	}

	private static Map<String, Set<String>> processLine(String issue,
			Map<String, Set<String>> issueToUniqueAssertions, String assertion) {
		if(issueToUniqueAssertions.containsKey(issue)){
			issueToUniqueAssertions.get(issue).add(assertion);
		}else{
			Set<String> assertions= new HashSet<>();
			assertions.add(assertion);
			issueToUniqueAssertions.put(issue, assertions);
			
		}
		return issueToUniqueAssertions;
	}

}
