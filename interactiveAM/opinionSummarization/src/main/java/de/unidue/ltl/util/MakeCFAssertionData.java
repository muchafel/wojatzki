package de.unidue.ltl.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MakeCFAssertionData {
	public static void main(String[] args) throws IOException {

		File toWrite = new File("src/main/resources/assertions_final.txt");

		for (File file : new File("src/main/resources/assertions").listFiles()) {
			if (file.getName().split("_")[1].equals("removedRedundancy.txt")) {
				addToFile(toWrite, file);
			}
		}
	}

	private static void addToFile(File toWrite, File file) throws IOException {
		String issue = file.getName().split("_")[0];
		for (String line : FileUtils.readLines(file)) {
			line = makeNice(line);
			String text = line + "\t" + issue + "\n";
			FileUtils.writeStringToFile(toWrite, text, "UTF-8");
		}

	}

	private static String makeNice(String line) {
		if (!line.endsWith(".")) {
			line = line + ".";
		}
		if(!Character.isUpperCase(line.charAt(0))){
			line=line.substring(0, 1).toUpperCase() + line.substring(1);
		}
		return line;
	}
}
