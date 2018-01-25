package assertionRegression.svetlanasTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import assertionRegression.judgmentPrediction.EmbeddingSimilarityMeasure;
import assertionRegression.similarity.Participant;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;

public class PredictJudgmentsOfIndividualParticipants_allIssues {
	
	public static void main(String[] args) throws Exception {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
		
		File folder= new File(baseDir+"/UCI/rawMatrices/");
		for(File file: folder.listFiles()) {
			if(file.getName().equals(".DS_Store"))continue;
			JudgmentPredictionExperimentFactory experimenter= new JudgmentPredictionExperimentFactory(file.getAbsolutePath());
//			System.out.println(experimenter.getAssertions());
//			System.out.println(experimenter.getParticipants());
			System.out.println();
			System.out.println("----------");
			System.out.println(file.getName());
			Predictor baselinePredictor= new BaseLinePredictor();
			Predictor randomPredictor= new RandomPredictor();
			Predictor mostSimilarAssertionPredictor_jaccard= new AssertionTextSimilarityPredictor(new WordNGramJaccardMeasure());
			Predictor mostSimilarAssertionPredictor_embedding= new AssertionTextSimilarityPredictor(new EmbeddingSimilarityMeasure(baseDir + "/UCI/data/pruned/wiki.en.vec"));
			Predictor mostSimilarAssertionPredictor_gst= new AssertionTextSimilarityPredictor(new GreedyStringTiling(5));
			Predictor mostSimilarAssertionPredictor_lcss= new AssertionTextSimilarityPredictor(new LongestCommonSubstringComparator());
			Predictor mostSimilarAssertionPredictor_judgment= new AssertionJudgmentSimilarityPredictor();
			Predictor mostSimilarAssertionPredictor_avg= new AssertionJudgmentSimilarityPredictor_averaged(3);
			Predictor meanhistoryPredictor= new MeanHistoryPredictor();
			Predictor meanOtherPredictor= new MeanOtherPredictor();
			Predictor mostSimilarUserPredictor= new MostSimilarUserPredictor();
			
			
			Map<Integer,Double> count2Accuracy_baseline = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_random = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_jaccard = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_gst = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_embedding = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_judgment = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_userHistory = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_meanOther = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_lcss = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_mostSimilarUser = new TreeMap<>();
			Map<Integer,Double> count2Accuracy_mostSimilAssertion_avg = new TreeMap<>();
			
			for(Participant participant: experimenter.getParticipants()) {
				PredictionExperiment experiment= experimenter.setUpExperiment(participant.getId());
				count2Accuracy_baseline=add2Map(count2Accuracy_baseline,experiment.getNonZeroJudgments_toTest().size(),baselinePredictor.predict(experiment));
				count2Accuracy_random=add2Map(count2Accuracy_random,experiment.getNonZeroJudgments_toTest().size(),randomPredictor.predict(experiment));
				count2Accuracy_jaccard=add2Map(count2Accuracy_jaccard,experiment.getNonZeroJudgments_toTest().size(),mostSimilarAssertionPredictor_jaccard.predict(experiment));
				count2Accuracy_embedding=add2Map(count2Accuracy_embedding,experiment.getNonZeroJudgments_toTest().size(),mostSimilarAssertionPredictor_embedding.predict(experiment));
				count2Accuracy_judgment=add2Map(count2Accuracy_judgment,experiment.getNonZeroJudgments_toTest().size(),mostSimilarAssertionPredictor_judgment.predict(experiment));
				count2Accuracy_userHistory=add2Map(count2Accuracy_userHistory,experiment.getNonZeroJudgments_toTest().size(),meanhistoryPredictor.predict(experiment));
				count2Accuracy_meanOther=add2Map(count2Accuracy_meanOther,experiment.getNonZeroJudgments_toTest().size(),meanOtherPredictor.predict(experiment));
				count2Accuracy_mostSimilarUser=add2Map(count2Accuracy_mostSimilarUser,experiment.getNonZeroJudgments_toTest().size(),mostSimilarUserPredictor.predict(experiment));
				count2Accuracy_lcss=add2Map(count2Accuracy_lcss,experiment.getNonZeroJudgments_toTest().size(),mostSimilarAssertionPredictor_lcss.predict(experiment));
				count2Accuracy_gst=add2Map(count2Accuracy_gst,experiment.getNonZeroJudgments_toTest().size(),mostSimilarAssertionPredictor_gst.predict(experiment));
				count2Accuracy_mostSimilAssertion_avg=add2Map(count2Accuracy_mostSimilAssertion_avg,experiment.getNonZeroJudgments_toTest().size(),mostSimilarAssertionPredictor_avg.predict(experiment));
			}

			System.out.println("baseline:\t"+mean(count2Accuracy_baseline));
			System.out.println("random:\t"+mean(count2Accuracy_random));
			System.out.println("userHistory:\t"+mean(count2Accuracy_userHistory));
			System.out.println("jaccard:\t"+mean(count2Accuracy_jaccard));
			System.out.println("mostSimilarJudgment:\t"+mean(count2Accuracy_judgment));
			System.out.println("embedding:\t"+mean(count2Accuracy_embedding));
			System.out.println("gst:\t"+mean(count2Accuracy_gst));
			System.out.println("meanOther:\t"+mean(count2Accuracy_meanOther));
			System.out.println("lcss:\t"+mean(count2Accuracy_lcss));
			System.out.println("mostSimilarUser:\t"+mean(count2Accuracy_mostSimilarUser));
			
		}
	}

	private static double mean(Map<Integer, Double> count2Acc) {
		double sum=0.0;
		for(Integer key : count2Acc.keySet()) {
			sum+=count2Acc.get(key);
		}
		return sum/count2Acc.size();
	}

	private static Map<Integer, Double> add2Map(Map<Integer, Double> count2Accuracy, int size, double accuracy) {
		if(count2Accuracy.keySet().contains(size)) {
			double val=(count2Accuracy.get(size)+accuracy)/2;
			count2Accuracy.put(size, val);
		}else {
			count2Accuracy.put(size, accuracy);
		}
		return count2Accuracy;
		
	}

}
