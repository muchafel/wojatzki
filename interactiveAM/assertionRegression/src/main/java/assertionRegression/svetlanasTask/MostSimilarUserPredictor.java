package assertionRegression.svetlanasTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import assertionRegression.similarity.Participant;
import assertionRegression.util.SimilarityHelper;

public class MostSimilarUserPredictor extends Predictor {
	
	private SimilarityHelper similarityHelper;
	
	public MostSimilarUserPredictor() {
		similarityHelper= new SimilarityHelper();
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception {
		Collection<Double> curentParticipantVector= experiment.getJudgments_toTest().values();
		double prediction= predictionOfMostSimilarUser(assertion,curentParticipantVector,experiment);
		return result(prediction,experiment.getNonZeroJudgments_toTest().get(assertion));
	}

	private double predictionOfMostSimilarUser(String assertion,Collection<Double> curentParticipantVector, PredictionExperiment experiment) throws Exception {
		Participant mostSimilarParticipant=null;
		double bestSimScore=0.0;
		for(Participant p: experiment.getJudgments_other().getParticipants()) {
			double sim = 0;
			
			double[] vectorOfOther =experiment.getJudgments_other().getRatingsOfParticipant(p);
			sim=similarity(curentParticipantVector,vectorOfOther);
			
			if(sim>=bestSimScore && experiment.getJudgments_other().getValue(p.getId(), assertion)!=0.0) {
				mostSimilarParticipant=p;
				bestSimScore=sim;
			}
			
		}
		if(bestSimScore==0.0 || mostSimilarParticipant==null) {
			return 1.0;
		}
		double prediction= experiment.getJudgments_other().getValue(mostSimilarParticipant.getId(), assertion);
		return prediction;
	}

	private double similarity(Collection<Double> currentParticipantVector, double[] vectorOfOther) {
		double sim=similarityHelper.getCosineSimilarity(new ArrayList(currentParticipantVector), toList(vectorOfOther));
		return sim;
	}
	
	private List<Double> toList(double[] vectorA) {
		List<Double> result= new ArrayList<>();
		for(double d: vectorA) {
			result.add(d);
		}
		return result;
	}

	@Override
	protected String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
