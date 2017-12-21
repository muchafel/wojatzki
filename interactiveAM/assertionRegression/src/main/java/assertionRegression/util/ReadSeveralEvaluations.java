package assertionRegression.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;


public class ReadSeveralEvaluations {
	public static void main(String[] args) throws IOException {
		File folder= new File("/Users/michael/DKPRO_HOME/org.dkpro.lab/repository");
		Map<String, Map<String, String>> issues2Scores= new TreeMap<String,Map<String,String>>();
		for(File file: folder.listFiles()) {
			if(!file.getName().startsWith("Evaluation")) continue;
			
			String a= file.getName().split("-")[1];
//			System.out.println(a.split("_")[1]+ " "+a.split("_")[3]);
			String pearson= getPearson(file);
//			System.out.println(a.split("_")[1]+"\t"+a.split("_")[3]+ "\t"+pearson);
			issues2Scores=addToMap(issues2Scores,a.split("_")[1],a.split("_")[3],pearson);
		}
		
		for(String key : issues2Scores.keySet()) {
			System.out.println(key+"\t"+issues2Scores.get(key).values());
		}
	}

	private static Map<String, Map<String, String>> addToMap(Map<String, Map<String, String>> issues2Scores, String issuaA, String issueB, String pearson) {
		
		if(issues2Scores.containsKey(issuaA)) {
			issues2Scores.get(issuaA).put(issueB,pearson);
		}else {
			Map<String, String> issue2Score= new TreeMap<>();
			issue2Score.put(issueB,pearson);
			issues2Scores.put(issuaA, issue2Score);
		}
		return issues2Scores;
//		issue2Scores.put(key, value)
		
	}

	private static String getPearson(File file) throws IOException {
		double pearson=0.0;
		for(File subfile: file.listFiles()) {
//			System.out.println(subfile.getName());
			if(subfile.getName().equals("evaluation_results.csv")) {
				int i=0;
				for(String line: FileUtils.readLines(subfile)) {
					if(i==1) {
//						System.out.println(line.split(",")[69]);
						return line.split(",")[69].replace("\"", "");
					}
					i++;
				}
			}
		}
		return "";
	}
}
