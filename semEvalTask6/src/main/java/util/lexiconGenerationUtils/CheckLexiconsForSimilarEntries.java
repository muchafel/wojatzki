package util.lexiconGenerationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.ConditionalFrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class CheckLexiconsForSimilarEntries {

	public static void main(String[] args) {
		Map<String, Float> favor= generateLexicon("src/main/resources/lists/stanceLexicons/Atheism_ordered/favor.txt");
		Map<String, Float> against= generateLexicon("src/main/resources/lists/stanceLexicons/Atheism_ordered/against.txt");
		
		System.err.println("FAVOR");
		inspectSimilarKeys(favor);
		System.err.println("AGAINST");
		inspectSimilarKeys(against);
	}

	private static void inspectSimilarKeys(Map<String, Float> favor) {
		for(String key_i: favor.keySet()){
			System.out.println("___");
			for(String key_j: favor.keySet()){
				String lower_i=key_i.toLowerCase();
				String lower_j=key_j.toLowerCase();
				if(!key_i.equals(key_j)){
					if(lower_i.equals(lower_j))System.out.println(key_i+" "+key_j);
					else if(lower_i.equals(hashTagVariant(lower_j)))System.out.println(key_i+" "+key_j);
				}
			}
		}
		
	}

	private static String hashTagVariant(String lower_j) {
		if(lower_j.startsWith("#"))return lower_j.replace("#", "");
		return lower_j;
	}

	/**
	 * generates the lexicon according to the specified resource
	 */
	private static HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
				String[] entry = currentString.split(" ");
				lexicon.put(entry[0], Float.valueOf(entry[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}
	
	private static void write(String target, String polarity, List<String> mostFrequentSamples, FrequencyDistribution<String> fd) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("src/main/resources/lists/stanceLexicons/" + target + "/"+polarity+".txt", true)))) {
			for (String key : mostFrequentSamples) {
				out.println(key +" "+fd.getCount(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
