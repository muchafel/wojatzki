package de.unidue.ltl.data.dummy.opinionSummarization;

import java.io.File;
import java.io.IOException;

import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;

public class AnalyzeCrowdflowerJudgements {

	public static void main(String[] args) throws IOException {
		File folder= new File("src/main/resources/matrices");
		for(File file: folder.listFiles()){
			CFJudgmentsCSVReader reader= new CFJudgmentsCSVReader(file);
			OpinionSummarizationData data= reader.read();
		}
	}

}
