package de.uni_due.ltl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.helper.opencv_calib3d;

public class PruneEmbeddings {

	public static void main(String[] args) throws IOException {
		List<String> dataVocab= FileUtils.readLines(new File("src/main/resources/list/vocab"));
		List<String> redditVocab= FileUtils.readLines(new File("src/main/resources/list/redditVocab"));
		List<String> idebateVocab= FileUtils.readLines(new File("src/main/resources/list/idebateVocab"));
		
		Set<String> mergedVocab=new HashSet();
		mergedVocab.addAll(dataVocab);
		mergedVocab.addAll(redditVocab);
		mergedVocab.addAll(idebateVocab);
		
		System.out.println(mergedVocab.size());
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("/Users/michael/DKPRO_HOME/wordEmbeddings/glove_840B/glove.840B.300d.txt"));
			while ((line = br.readLine()) != null) { 
				String word=line.split(" ")[0];
				if(mergedVocab.contains(word)||particelContained(mergedVocab,word)){
					mergedVocab.remove(word);
					FileUtils.write(new File("src/main/resources/list/prunedEmbeddings_data_reddit_idebate.84B.300d.txt"), line+System.lineSeparator(), true);
				}
			} 
		} 
		catch (IOException e) {
			System.err.println("Error: " + e);
		}
		System.out.println(mergedVocab);
		System.out.println(mergedVocab.size());
	}

	private static boolean particelContained(Set<String> vocab, String word) {
		if(vocab.contains(word.split("'")[0])){
			return true;
		}
		if(vocab.contains(word.toLowerCase())){
			return true;
		}
		return false;
	}

}
