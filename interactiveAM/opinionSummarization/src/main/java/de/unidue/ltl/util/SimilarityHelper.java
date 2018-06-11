package de.unidue.ltl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.print.attribute.standard.RequestingUserName;


public class SimilarityHelper {

	public static float getCosineSimilarity(double[] a,double[] b, boolean useDistance){
		
		float cosine;
		//if the vectors are the same set cosine to 1 (to avoinding floating point issues)
		if(Arrays.equals(a, b)){
			cosine= 1.0f;
		}else{
			//calculate the actual cosine similarity
			cosine= dotProduct(a,b) / (normalizeVector(a) * normalizeVector(b));
			//if one vector has zero set cosine to 0
			if(Float.isNaN(cosine)){
				cosine= 0.0f;
			}
		}
		
		if(useDistance){
			return 1-cosine;
		}
		return cosine;
	}
	
	private static float normalizeVector(double[] a) {
		float sum = 0;
		for(double dimension: a){
			sum+=dimension*dimension;
		}
		return (float) Math.sqrt(sum);
	}
	private static float dotProduct(double[] a, double[] b) {
		float sum = 0;
		int i=0;
		for(double dimension: a){
			sum+=a[i]*b[i];
			i++;
		}
		return sum;
	}
	
}
