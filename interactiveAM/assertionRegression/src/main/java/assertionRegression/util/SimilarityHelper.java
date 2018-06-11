package assertionRegression.util;

import java.util.List;

import org.apache.regexp.recompile;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.LevenshteinComparator;

public class SimilarityHelper {

	
	public  double getCosineSimilarity(List<Double>a,List<Double>b){
		return dotProduct(a,b) / (normalizeVector(a) * normalizeVector(b));
	}
	
	private  double normalizeVector(List<Double> a) {
		double sum = 0;
		for(double dimension: a){
			sum+=dimension*dimension;
		}
		return Math.sqrt(sum);
	}
	
	private double dotProduct(List<Double> a, List<Double> b) {
		float sum = 0;
		int i=0;
		for(double dimension: a){
			sum+=a.get(i)*b.get(i);
			i++;
		}
		return sum;
	}
}
