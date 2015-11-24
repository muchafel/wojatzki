package util;

import org.apache.regexp.recompile;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.LevenshteinComparator;

public class SimilarityHelper {

	public static boolean WordsAreSimilar(String w1, String w2){
		String lower_i=w1.toLowerCase();
		String lower_j=w2.toLowerCase();
		TermSimilarityMeasure measure2 = new LevenshteinComparator();
		if(!w1.equals(w2)){
			double normalized_levensthein = 0;
			try {
				normalized_levensthein = measure2.getSimilarity(lower_i, lower_j)/lower_i.length();
			} catch (SimilarityException e) {
				e.printStackTrace();
			}
			if(lower_i.equals(lower_j)){
				return true;
			}
			else if(lower_i.equals(hashTagVariant(lower_j))){
				return true;
			}
			else if (normalized_levensthein<0.2){
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	private static String hashTagVariant(String lower_j) {
		if(lower_j.startsWith("#"))return lower_j.replace("#", "");
		return lower_j;
	}
}
