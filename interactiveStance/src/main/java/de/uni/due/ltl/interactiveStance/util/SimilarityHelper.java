package de.uni.due.ltl.interactiveStance.util;

import java.util.List;

import org.apache.regexp.recompile;


public class SimilarityHelper {

	
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
