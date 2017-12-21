package assertionRegression.svetlanasTask;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public abstract class Predictor {

	public double predict(PredictionExperiment experiment) {
		double correct = 0;
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		for(String assertion: experiment.getNonZeroJudgments_toTest().keySet()) {
			fd.inc(getPredictionForAssertion(assertion,experiment));
//			correct+=getPredictionForAssertion(assertion,experiment);
		}
//		return correct;
//		return correct/(double)experiment.getNonZeroJudgments_toTest().size();
		
//		System.out.println(fd.getCount("tp")+" "+fd.getCount("fn")+" "+fd.getCount("tn")+" "+fd.getCount("tn"));

		
		double precision;
		double recall;
		double accuracy;
		if(fd.getCount("tp")==0.0) {
			recall=0.0;
			precision=0.0;
		}else {
			precision= (double)fd.getCount("tp")/((double)(fd.getCount("tp")+fd.getCount("fp")));
			recall= (double)fd.getCount("tp")/((double)(fd.getCount("tp")+fd.getCount("fn")));
		}
		double fmeasure= 2*(precision*recall)/(precision+recall);
		if(Double.isNaN(fmeasure)) {
//			System.out.println(fd.getCount("tp")+" "+fd.getCount("fn")+" "+fd.getCount("tn")+" "+fd.getCount("tn"));
			fmeasure=0.0;
		}
		accuracy= ((double)fd.getCount("tp")+(double)fd.getCount("tn"))/((double)fd.getCount("tp")+(double)fd.getCount("tn")+(double)(fd.getCount("fp")+fd.getCount("fn")));

//		System.out.println(fmeasure +" "+precision+ " "+recall);
		return accuracy;
		
	}
	
	protected String result(double prediction, Double trueValue) {
		if(prediction==1.0 && trueValue==1.0) {
			//TRUE POSITIVE
			return "tp";
		}else if(prediction==-1.0 && trueValue==-1.0) {
			//TRUE NEGATIVE
			return "tn";
		}else if(prediction==1.0 && trueValue==-1.0) {
			//FALSE POSITIVE
			return "fp";
		}else {
			//FALSE NEGATIVE
			return "fn";
		}
	}

	protected abstract String getPredictionForAssertion(String assertion, PredictionExperiment experiment);
}
