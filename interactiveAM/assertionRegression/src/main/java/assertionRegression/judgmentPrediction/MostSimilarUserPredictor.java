package assertionRegression.judgmentPrediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import assertionRegression.similarity.Participant;

public class MostSimilarUserPredictor extends NextJudgmentPredictor {

	private SimilarityHelper similarityHelper;
	
	public  MostSimilarUserPredictor() {
		this.similarityHelper= new SimilarityHelper();
	}
	
	@Override
	public double predict(int numberOfGivenJudgments, boolean predcitOnlyNext,
			ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		setUpPrediction(numberOfGivenJudgments,experiment);
		Map<Participant,Double> similarityRankedParticipants= rankSimilarity(experiment);
//		for(Participant p: similarityRankedParticipants.keySet()) {
//			System.out.println(similarityRankedParticipants.get(p));
//		}
		
		int correct=0;
		if(experiment.getAssertionsToTest().size()==0) {
//			System.err.println("no assertions to test left");
			return Double.NaN;
		}

		if(predcitOnlyNext) {
			String nextAssertion=experiment.getAssertionsToTest().get(0);
			
//			System.out.println(nextAssertion);
			double prediction = predictionOfMostSimilarParticipant(similarityRankedParticipants,experiment.getAssertionsToTest().get(0),0,experiment);
			
			if(experiment.getAssertion2TrueScore().get(nextAssertion)==0.0) return Double.NaN;
//			System.out.println(experiment.getParticipantToTest().getId()+" "+nextAssertion+" "+prediction+ " "+experiment.getAssertion2TrueScore().get(nextAssertion));

			
			if(prediction==experiment.getAssertion2TrueScore().get(nextAssertion)) {
				return 1.0;
			}else {
				return 0.0;
			}
		}else {
			for(String assertion: experiment.getAssertionsToTest()) {
				double prediction = predictionOfMostSimilarParticipant(similarityRankedParticipants,assertion,0,experiment);
//				System.out.println(assertion +" "+ prediction + " " + assertion2TrueScore.get(assertion));
				if(prediction==experiment.getAssertion2TrueScore().get(assertion)) {
					correct++;
				}
			}
//			System.out.println(correct+" "+experiment.getAssertionsToTest().size());
			return (double)correct/experiment.getAssertionsToTest().size();
		}
	}
	
	
	private Map<Participant,Double> rankSimilarity(ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		Map<Participant,List<Double>> partialMatrix=getPartialMatrix(experiment.getNonZeroGivenAssertions(),experiment);
		List<Double> judgments = new ArrayList();
		for (String assertion : experiment.getNonZeroGivenAssertions()) {
			judgments.add(experiment.getAssertion2TrueScore().get(assertion));
		}
		Map<Participant,Double> result= new LinkedHashMap();
		
		for(Participant p: partialMatrix.keySet()) {
			List<Double> vector= partialMatrix.get(p);
			double currentCosine=similarityHelper.getCosineSimilarity(vector,judgments);
			if(Double.isNaN(currentCosine)) {
				currentCosine=-1.0;
			}
//		System.out.println(currentCosine);
			result.put(p, currentCosine);
		}
		
		Participant p= sortByValue(result).keySet().iterator().next();
//		System.out.println("most similar Vector: "+partialMatrix.get(p));
//		System.out.println("test Vector: "+judgments+" ("+getCosineSimilarity(partialMatrix.get(p),judgments)+")");
		
		return sortByValue(result);
	}
	
	private Map<Participant, List<Double>> getPartialMatrix(List<String> nonZeroGivenAssertions, ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		Map<Participant, List<Double>> result = new HashMap();

		int i = 0;
		for (Participant p : experiment.getData().getParticipants()) {
			List<Double> judgments = new ArrayList();
			for (String assertion : nonZeroGivenAssertions) {
				judgments.add(experiment.getData().getRatingsForAssertion(assertion)[i]);
			}
			i++;
			result.put(p, judgments);
		}
		return result;
	}
	

	
	private Map<Participant, Double> sortByValue(Map<Participant, Double> unsortMap) {
        List<Map.Entry<Participant, Double>> list = new LinkedList<Map.Entry<Participant, Double>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Participant, Double>>() {
            public int compare(Map.Entry<Participant, Double> o1,
                               Map.Entry<Participant, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Participant, Double> sortedMap = new LinkedHashMap<Participant, Double>();
        for (Map.Entry<Participant, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
	}
	
	private double predictionOfMostSimilarParticipant(Map<Participant, Double> similarityRankedParticipants,String assertion, int level, ParticipanJudgmentPredictionExperiment experiment) throws Exception {
		int i=0;
		Participant participant=null; 
		for(Participant p : similarityRankedParticipants.keySet()) {
			if(i==level) {
//				System.out.println("select p with "+similarityRankedParticipants.get(p));
				participant=p;
				break;
			}else {
				i++;
			}
		}
//		System.out.println(Arrays.toString(judgmentsOfParticipant));
//		System.out.println(Arrays.toString(data.getRatingsOfParticipant(participant)));
//		System.out.println();
		double value=experiment.getData().getValue(participant.getId(), assertion);
//		System.out.println(value+ " "+assertion+ " "+level);
//		return value;
		if(value!=0.0) {
//			if(similarityRankedParticipants.get(participant)<0.8) return 1.0;
			return value;
		}else {
			level=level+1;
			return predictionOfMostSimilarParticipant(similarityRankedParticipants,assertion,level,experiment);
		}
	}
	
}
