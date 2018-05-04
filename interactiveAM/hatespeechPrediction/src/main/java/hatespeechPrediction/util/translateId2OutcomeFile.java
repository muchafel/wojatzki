package hatespeechPrediction.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class translateId2OutcomeFile {
	public static void main(String[] args) throws IOException {
		String firstLine = FileUtils.readLines(new File("src/main/resources/similarity_gold/matrix.tsv"), "UTF-8").get(0);
		String[] parts= firstLine.split("\t");
		
		for(String id2outcomes: FileUtils.readLines(new File("src/main/resources/similarityPredicted/id2OutcomeUntranslated.txt"), "UTF-8")) {
			String[] parts2= id2outcomes.split("=");
			String row=parts2[0].split(" ")[0];
			String column= parts2[0].split(" ")[1];
			
			row=parts[Integer.valueOf(row.replace("\\", ""))];
			column=parts[Integer.valueOf(column.replace("\\", ""))];
			
			String newline= row+"_"+column+"="+parts2[1];
			System.out.println(newline);
			FileUtils.write(new File("src/main/resources/similarityPredicted/id2OutcomeTranslated.txt"), newline+"\n", "UTF-8", true);
					
		}
		
		
	}
}
