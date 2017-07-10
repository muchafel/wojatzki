package de.uni.due.ltl.interactiveStance.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.fitting.WeightedObservedPoint;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jfree.data.xy.XYSeries;

import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.experimentLogging.ThresholdEvent;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;
import de.uni.due.ltl.interactiveStance.util.EvaluationUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;
import nl.peterbloem.powerlaws.Continuous;

public class CollocationNgramAnalyzer_distributionDerived extends CollocationNgramAnalyzerBase {

	private double percentil=0.95;
	
	public CollocationNgramAnalyzer_distributionDerived(StanceDB db, EvaluationScenario scenario, double d,ExperimentLogging logging) {
		super(db, scenario,logging);
		this.percentil=d;
	}

	@Override
	protected EvaluationData<String> evaluateUsingLexicon(StanceLexicon stanceLexicon, EvaluationDataSet data)
			throws AnalysisEngineProcessException {
		return evaluateUsingLexicon_Distributional(stanceLexicon, data);
	}

	private EvaluationData<String> evaluateUsingLexicon_Distributional(StanceLexicon stanceLexicon, EvaluationDataSet data) throws AnalysisEngineProcessException {
		EvaluationData<String> evalData = new EvaluationData<>();
		
		ZipfDistributionsContainer zipfContainer= new ZipfDistributionsContainer(stanceLexicon, percentil);
		
		for (JCas jcas : new JCasIterable(data.getDataReader())){
			this.engine.process(jcas);
			String goldOutcome= JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance();
			float commentPolarity = getPolarity(stanceLexicon, jcas);
			String outcome = ressolveOutcome((float)zipfContainer.getZipfUpperBound(),(float)zipfContainer.getZipfLowerBound(),commentPolarity);
			evalData.register(goldOutcome, outcome);
			
		}
		new ThresholdEvent(logging,"zipfPercentil\t"+ percentil+"\t"+zipfContainer.getZipfUpperBound()+"_"+zipfContainer.getZipfLowerBound(),"fixed").persist(false);
		System.out.println("Using found thresholds "+ zipfContainer.getZipfUpperBound()+"_"+zipfContainer.getZipfLowerBound()+" : "+EvaluationUtil.getSemEvalMeasure(new Fscore<>(evalData)));
		System.out.println(new ConfusionMatrix<String>(evalData));
		return evalData;
	}

	public double getPercentil() {
		return percentil;
	}

	public void setPercentil(double percentil) {
		this.percentil = percentil;
	}

}
