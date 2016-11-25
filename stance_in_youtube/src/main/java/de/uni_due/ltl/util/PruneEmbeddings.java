package de.uni_due.ltl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.helper.opencv_calib3d;

public class PruneEmbeddings {

	public static void main(String[] args) throws IOException {
		List<String> vocab= FileUtils.readLines(new File("src/main/resources/list/vocab"));
		System.out.println(vocab.size());
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("/Users/michael/DKPRO_HOME/wordEmbeddings/glove_840B/glove.840B.300d.txt"));
			while ((line = br.readLine()) != null) { 
				String word=line.split(" ")[0];
				if(vocab.contains(word)||particelContained(vocab,word)){
					vocab.remove(word);
					FileUtils.write(new File("src/main/resources/list/prunedEmbeddings.84B.300d.txt"), line+System.lineSeparator(), true);
				}
			} 
		} 
		catch (IOException e) {
			System.err.println("Error: " + e);
		}
		System.out.println(vocab);
		System.out.println(vocab.size());
	}

	private static boolean particelContained(List<String> vocab, String word) {
		if(vocab.contains(word.split("'")[0])){
			return true;
		}
		if(vocab.contains(word.toLowerCase())){
			return true;
		}
		return false;
	}

}
