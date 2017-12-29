package assertionRegression.svetlanasTask;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public abstract class Predictor {

	public double predict(PredictionExperiment experiment) throws Exception {
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		for(String assertion: experiment.getNonZeroJudgments_toTest().keySet()) {
			fd.inc(getPredictionForAssertion(assertion,experiment));
//			correct+=getPredictionForAssertion(assertion,experiment);
		}
		
		double precision =getPrecision(fd);
		double recall =getRecall(fd);
		double fmeasure=getF(precision,recall);
		
		double accuracy =getAccuracy(fd);

//		System.out.println(fmeasure +" "+precision+ " "+recall);
		return accuracy;
		
	}
	
	private double getF(double precision, double recall) {
		double fmeasure= 2*(precision*recall)/(precision+recall);
		if(Double.isNaN(fmeasure)) {
			return 0.0;
		}
		return fmeasure;
	}

	private double getAccuracy(FrequencyDistribution<String> fd) {
		return ((double)fd.getCount("tp")+(double)fd.getCount("tn"))/((double)fd.getCount("tp")+(double)fd.getCount("tn")+(double)(fd.getCount("fp")+fd.getCount("fn")));
	}

	private double getRecall(FrequencyDistribution<String> fd) {
		double recall;
		if(fd.getCount("tp")==0.0) {
			recall=0.0;
		}else {
			recall= (double)fd.getCount("tp")/((double)(fd.getCount("tp")+fd.getCount("fn")));
		}
		return recall;
	}

	private double getPrecision(FrequencyDistribution<String> fd) {
		double precision;
		if(fd.getCount("tp")==0.0) {
			precision=0.0;
		}else {
			precision= (double)fd.getCount("tp")/((double)(fd.getCount("tp")+fd.getCount("fp")));
		}
		
		return precision;
	}

	protected String result(double prediction, Double trueValue) throws Exception {
		if(prediction==1.0 && trueValue==1.0) {
			//TRUE POSITIVE
			return "tp";
		}else if(prediction==-1.0 && trueValue==-1.0) {
			//TRUE NEGATIVE
			return "tn";
		}else if(prediction==1.0 && trueValue==-1.0) {
			//FALSE POSITIVE
			return "fp";
		}else if(prediction==-1.0 && trueValue==1.0){
			//FALSE NEGATIVE
			return "fn";
		}else {
			throw new Exception("wrong value");
		}
	}


	public double predict(PredictionExperiment experiment, int i) throws Exception {
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		for(String assertion: experiment.getNonZeroJudgments_toTest().keySet()) {
			fd.inc(getPredictionForAssertion(assertion,experiment,i));
			if(getPredictionForAssertion(assertion,experiment).equals("tn")) {
				System.out.println(assertion+ " tn");
			}
		}
		
		double accuracy =getAccuracy(fd);
//		System.out.println(i+" "+accuracy);
		return accuracy;
	}
	protected abstract String getPredictionForAssertion(String assertion, PredictionExperiment experiment) throws Exception;

	protected abstract String getPredictionForAssertion(String assertion, PredictionExperiment experiment, int historySize) throws Exception;
}
