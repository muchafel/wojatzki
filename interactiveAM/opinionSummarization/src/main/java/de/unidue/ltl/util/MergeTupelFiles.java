package de.unidue.ltl.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MergeTupelFiles {

	public static void main(String[] args) throws IOException {
		File tupleFolder= new File("/Users/michael/Dropbox/nrc/Best-Worst-Scaling-Scripts/assertions");
		File toWrite = new File("src/main/resources/all_tuples.txt");

		for(File tupleFile: tupleFolder.listFiles()){
			if(tupleFile.getName().endsWith(".tuples")){
				System.out.println(tupleFile.getName());
				for(String line: FileUtils.readLines(tupleFile)){
//					System.out.println(line+"\t"+tupleFile.getName().split(".")[0]);
					FileUtils.writeStringToFile(toWrite, line+"\t"+tupleFile.getName().split("\\.")[0]+"\n", "UTF-8");
				}
			}
		}
		
	}
}
