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
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LevenshteinComparator;

public class CheckLexiconsForSimilarEntries {

	public static void main(String[] args) throws SimilarityException {
		Map<String, Float> favor= generateLexicon("src/main/resources/lists/stanceLexicons/Atheism_ordered/favor.txt");
		Map<String, Float> against= generateLexicon("src/main/resources/lists/stanceLexicons/Atheism_ordered/against.txt");
		
		System.err.println("FAVOR");
		Map<String, List<String>>favor_BOW = inspectSimilarKeys(favor);
		System.err.println("AGAINST");
		Map<String, List<String>>against_BOW = inspectSimilarKeys(against);
	}

	private static Map<String, List<String>> inspectSimilarKeys(Map<String, Float> favor) throws SimilarityException {
		
		
		TermSimilarityMeasure measure = new GreedyStringTiling(3);
		TermSimilarityMeasure measure2 = new LevenshteinComparator();
		double epsilon = 0.0001;
		
		Map<String, List<String>> result= new HashMap<String, List<String>>();
		for(String key_i: favor.keySet()){
			System.out.println("___");
			for(String key_j: favor.keySet()){
				String lower_i=key_i.toLowerCase();
				String lower_j=key_j.toLowerCase();
				if(!key_i.equals(key_j)){
					double normalized_levensthein=measure2.getSimilarity(lower_i, lower_j)/lower_i.length();
					if(lower_i.equals(lower_j)){
						System.out.println(key_i+" "+key_j);
					}
					else if(lower_i.equals(hashTagVariant(lower_j))){
						System.out.println(key_i+" "+key_j);
					}
					else if (normalized_levensthein<0.2){
						System.out.println(key_i+" "+key_j+" "+measure2.getSimilarity(lower_i, lower_j)+ " "+ normalized_levensthein);
					}
//					else if (measure.getSimilarity(lower_i, lower_j)>0.9)System.out.println(key_i+" "+key_j+" "+measure.getSimilarity(lower_i, lower_j));
				}
			}
		}
		return null;
		
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
