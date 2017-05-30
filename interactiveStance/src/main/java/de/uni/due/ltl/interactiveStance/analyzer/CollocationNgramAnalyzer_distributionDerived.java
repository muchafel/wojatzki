package de.uni.due.ltl.interactiveStance.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.fitting.WeightedObservedPoint;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.jfree.data.xy.XYSeries;

import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class CollocationNgramAnalyzer_distributionDerived extends CollocationNgramAnalyzerBase {

	public CollocationNgramAnalyzer_distributionDerived(StanceDB db, EvaluationScenario scenario) {
		super(db, scenario);
	}

	@Override
	protected Fscore<String> evaluateUsingLexicon(StanceLexicon stanceLexicon, EvaluationDataSet data)
			throws AnalysisEngineProcessException {
		return evaluateUsingLexicon_Distributional(stanceLexicon, data);
	}

	private Fscore<String> evaluateUsingLexicon_Distributional(StanceLexicon stanceLexicon, EvaluationDataSet data) {

	
		LevenbergMarquardtOptimizer opt= new LevenbergMarquardtOptimizer();
		PolynomialFitter fitter = new PolynomialFitter(3,opt);
	
		int j = 0;
        List<String> keys = new ArrayList<String>(stanceLexicon.getKeys());
        System.out.println(keys);
        Collections.reverse(keys);
        for(String key : keys){
        	if(j==0){
        		fitter.addObservedPoint(new WeightedObservedPoint(1000.0, (double) j, (double) (stanceLexicon.getStancePolarity(key))));
        	}else if(j==keys.size()-1){
        		fitter.addObservedPoint(new WeightedObservedPoint(1000.0, (double) j, (double) (stanceLexicon.getStancePolarity(key))));
        	}else{
        		fitter.addObservedPoint(new WeightedObservedPoint(1.0, (double) j, (double) (stanceLexicon.getStancePolarity(key))));
        	}
			j++;
        }		
		
		System.out.println("fit");
		
		//fit the function and add the interpolated function
		PolynomialFunction func= new PolynomialFunction(fitter.fit());
		System.out.println(func.toString());
		PolynomialFunction funcII= func.polynomialDerivative().polynomialDerivative();
		System.out.println(funcII.toString());
		LaguerreSolver solver= new LaguerreSolver();
		double root1= solver.solve(100,funcII,0,keys.size()-1,100);
		double root2= solver.solve(100,funcII,0,keys.size()-1,keys.size()/2+100);
		System.out.println(root1+ " "+func.value((int)(root1)));
		System.out.println(root2+ " "+func.value((int)(root2)));
		
		return null;
	}

}
