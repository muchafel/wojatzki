package de.unidue.ltl.data.dummy.opinionSummarization;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;

public class CFJudgmentsCSVReader {

	private File csv;
	
	public CFJudgmentsCSVReader(File file) {
		this.csv=file;
	}

	public OpinionSummarizationData read() throws IOException {
		for(String line: FileUtils.readLines(csv)){
			for(String header: line.split("\t")){
				
			}
		}
//		OpinionSummarizationData data= new OpinionSummarizationData(participants, statements, valueMatrixInput);		
		return null;
	}

}
