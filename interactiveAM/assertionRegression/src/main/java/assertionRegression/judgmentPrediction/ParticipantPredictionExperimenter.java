package assertionRegression.judgmentPrediction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;

public class ParticipantPredictionExperimenter {
	public static void main(String[] args) throws Exception {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
		ParticipanJudgmentPredictionExperiment exp= new ParticipanJudgmentPredictionExperiment(baseDir+"/UCI/rawMatrices/Black Lives Matter.tsv");
		int numberofAssertions=exp.getNumberOfAssertions();
		System.out.println(numberofAssertions);
		
		Map<Integer, List<Double>> resultsPermutation= new HashMap<>();
		BaselinePredictor baselinePredictor= new BaselinePredictor();
		MostSimilarUserPredictor mostSimilarUserPredictor= new MostSimilarUserPredictor();
		MeanUserPredictor meanUserPredictor= new MeanUserPredictor();
		RandomPredictor randomPredictor= new RandomPredictor();
		
		
//		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_jaccard= new MostSimilarAssertionPredictor(new WordNGramJaccardMeasure(3));
//		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_embedding= new MostSimilarAssertionPredictor(new EmbeddingSimilarityMeasure(baseDir + "/UCI/data/pruned/wiki.en.vec"));
//		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_gst= new MostSimilarAssertionPredictor(new GreedyStringTiling(1));
//		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_lcss= new MostSimilarAssertionPredictor(new LongestCommonSubstringComparator());
		
		for (int z = 1; z <= 10; z++) {
			List<Integer> order= getRandomOrder(numberofAssertions);
			System.out.println(order);
			resultsPermutation=exp.runExperiment(order,40,resultsPermutation,baselinePredictor,true);
		}
		
//		System.out.println(j+"\t"+avg / numberofParticipants);
		
		for(int i: resultsPermutation.keySet()) {
			System.out.println(i+"\t"+avgMINMAX(resultsPermutation.get(i)));
		}
	}
		
	private static String avgMINMAX(List<Double> list) {
		double score = 0.0;
		double min=1.0;
		double max=0.0;
		for (double d : list) {
			score += d;
			if(d<min) {
				min=d;
			}
			if(d>max) {
				max=d;
			}
		}
		return String.valueOf(score / list.size())+"\t"+String.valueOf(min)+"\t"+String.valueOf(max);
	}

	private static double avg(List<Double> list) {
		double score = 0.0;
		for (double d : list) {
			score += d;
		}
		return score / list.size();
	}


		private static List<Integer> getRandomOrder(int numberOfAssertions) {
			List<Integer> result= new ArrayList<Integer>();
			for(int i= 0; i<numberOfAssertions-1; i++) {
				result.add(i);
			}
			Collections.shuffle(result);
			return result;
		}
}
