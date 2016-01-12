package util;

import java.util.List;

import org.apache.regexp.recompile;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.LevenshteinComparator;

public class SimilarityHelper {

	public static boolean wordsAreSimilar(String w1, String w2){
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
			else if(lower_i.equals(userVariant(lower_j))){
				return true;
			}
			else if(lower_i.equals(pluralS(lower_j))){
				return true;
			}
			else if(lower_i.equals(genetiveS(lower_j))){
				return true;
			}
			else if (normalized_levensthein<0.15){
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	private static Object genetiveS(String lower_j) {
		if(lower_j.endsWith("'s"))return lower_j.replace("'s", "");
		return lower_j;
	}
	private static Object pluralS(String lower_j) {
		if(lower_j.endsWith("s"))return lower_j.substring(0, lower_j.length()-1);
		return lower_j;
	}
	private static String hashTagVariant(String lower_j) {
		if(lower_j.startsWith("#"))return lower_j.replace("#", "");
		return lower_j;
	}
	
	private static String userVariant(String lower_j) {
		if(lower_j.startsWith("@"))return lower_j.replace("@", "");
		return lower_j;
	}
	
	public static float getCosineSimilarity(List<Float>a,List<Float>b){
		return dotProduct(a,b) / (normalizeVector(a) * normalizeVector(b));
	}
	
	private static float normalizeVector(List<Float> a) {
		float sum = 0;
		for(float dimension: a){
			sum+=dimension*dimension;
		}
		return (float) Math.sqrt(sum);
	}
	private static float dotProduct(List<Float> a, List<Float> b) {
		float sum = 0;
		int i=0;
		for(float dimension: a){
			sum+=a.get(i)*b.get(i);
			i++;
		}
		return sum;
	}
}
