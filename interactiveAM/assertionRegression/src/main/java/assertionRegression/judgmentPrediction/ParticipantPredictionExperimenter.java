package assertionRegression.judgmentPrediction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;

public class ParticipantPredictionExperimenter {
	public static void main(String[] args) throws Exception {
		ParticipanJudgmentPredictionExperiment exp= new ParticipanJudgmentPredictionExperiment("src/main/resources/rawMatrices/Black Lives Matter.tsv");
		int numberofParticipants=exp.getNumberOfParticipants();
		
		Map<Integer, List<Double>> resultsPermutation= new HashMap<>();
		BaselinePredictor baselinePredictor= new BaselinePredictor();
		MostSimilarUserPredictor mostSimilarUserPredictor= new MostSimilarUserPredictor();
		MeanUserPredictor meanUserPredictor= new MeanUserPredictor();
		RandomPredictor randomPredictor= new RandomPredictor();
		
		
		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_jaccard= new MostSimilarAssertionPredictor(new WordNGramJaccardMeasure(3));
		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_embedding= new MostSimilarAssertionPredictor(new EmbeddingSimilarityMeasure("/Users/michael/DKPRO_HOME/UCI/data/pruned/wiki.en.vec"));
		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_gst= new MostSimilarAssertionPredictor(new GreedyStringTiling(1));
		MostSimilarAssertionPredictor mostSimilarAssertionPredictor_lcss= new MostSimilarAssertionPredictor(new LongestCommonSubstringComparator());
		
		for (int z = 1; z <= 30; z++) {
			List<Integer> order= getRandomOrder(numberofParticipants);
			resultsPermutation=exp.runExperiment(order,50,resultsPermutation,mostSimilarAssertionPredictor_gst,true);
		}
		
//		System.out.println(j+"\t"+avg / numberofParticipants);
		
		for(int i: resultsPermutation.keySet()) {
			System.out.println(i+" "+avg(resultsPermutation.get(i)));
		}
	}
		
	private static double avg(List<Double> list) {
		double score = 0.0;
		for (double d : list) {
			score += d;
		}
		return score / list.size();
	}


		private static List<Integer> getRandomOrder(int numberofParticipants) {
			List<Integer> result= new ArrayList<Integer>();
			for(int i= 0; i<numberofParticipants-1; i++) {
				result.add(i);
			}
			Collections.shuffle(result);
			return result;
		}
}
