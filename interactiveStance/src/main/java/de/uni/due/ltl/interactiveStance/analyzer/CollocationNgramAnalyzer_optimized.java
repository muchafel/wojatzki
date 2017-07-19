package de.uni.due.ltl.interactiveStance.analyzer;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.features.ngram.util.NGramUtils;
import org.springframework.expression.spel.support.ReflectionHelper.ArgsMatchKind;

import com.google.common.collect.Multiset.Entry;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.experimentLogging.ThresholdEvent;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;
import de.uni.due.ltl.interactiveStance.util.CollocationMeasureHelper;
import de.uni.due.ltl.interactiveStance.util.EvaluationUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;
import edu.stanford.nlp.io.EncodingPrintWriter.out;

public class CollocationNgramAnalyzer_optimized extends CollocationNgramAnalyzerBase {

	private final int fixedThreshold=75;
	
	public CollocationNgramAnalyzer_optimized(StanceDB db, EvaluationScenario scenario,ExperimentLogging logging, boolean useBinCas) {
		super(db, scenario,logging,useBinCas);
	}


	@Override
	protected EvaluationData<String> evaluateUsingLexicon(StanceLexicon stanceLexicon, EvaluationDataSet data) throws AnalysisEngineProcessException {
		return evaluateUsingLexicon_ThresholdOptimization(stanceLexicon, data);
	}

	

	/**
	 * TODO: check whether we can get rid of all the casting from int to
	 * String... ID is a INT! 
	 * TODO evaluation needs to be expandend to larger ngrams than unigrams (we may need mutual expectation)
	 * 
	 * @param selectedTargets
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws UIMAException
	 * @throws TextClassificationException
	 */
	public EvaluationData<String> analyzeOptimized(HashMap<String, ExplicitTarget> selectedTargetsFavor,HashMap<String, ExplicitTarget> selectedTargetsAgainst, int maxNgramSize, boolean evaluateTrain)
			throws NumberFormatException, SQLException, UIMAException, TextClassificationException {

		Map<Integer, StanceLexicon> lexica = new HashMap<Integer, StanceLexicon>();

		for (int i = 1; i <= maxNgramSize; i++) {
			lexica.put(i, createStanceLexicon(selectedTargetsFavor,selectedTargetsAgainst, i));
		}

		
		EvaluationData<String> result = null;
		for (int lexiconId : lexica.keySet()) {
			if(evaluateTrain){
				result=evaluateUsingLexicon_ThresholdOptimization(lexica.get(lexiconId), scenario.getTrainData());
			}else{
				result=evaluateUsingLexicon_ThresholdOptimization(lexica.get(lexiconId), scenario.getTestData());
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * runs an (optimized) evaluation with a threshold which is optimized on the given data
	 * @param stanceLexicon
	 * @param evaluationDataSet
	 * @return
	 * @throws AnalysisEngineProcessException
	 */
	public EvaluationData<String> evaluateUsingLexicon_ThresholdOptimization(StanceLexicon stanceLexicon, EvaluationDataSet evaluationDataSet) throws AnalysisEngineProcessException {
		Map<String,EvaluationData<String>> thresholdId2Outcome=getThresholdId2Outcome(stanceLexicon,evaluationDataSet);
		String topConfig=getTopConfig(thresholdId2Outcome);
		new ThresholdEvent(logging, topConfig,"optimized").persist(false);
		System.out.println("Using threshold config "+ topConfig+" : "+EvaluationUtil.getSemEvalMeasure(new Fscore<>(thresholdId2Outcome.get(topConfig))));
		System.out.println(new ConfusionMatrix<String>(thresholdId2Outcome.get(topConfig)));
		return thresholdId2Outcome.get(topConfig);
	}
	
	

	/**
	 * get best config from the result map
	 * @param thresholdId2Outcome
	 * @return
	 */
	private String getTopConfig(Map<String, EvaluationData<String>> thresholdId2Outcome) {
		String result=null;
		double bestf=0;
		for(String key: thresholdId2Outcome.keySet()){
			double semEval=EvaluationUtil.getSemEvalMeasure(new Fscore<>(thresholdId2Outcome.get(key)));
			if(semEval>bestf){
				result=key;
				bestf=semEval;
			}
		}
		return result;
	}

	private Map<String, EvaluationData<String>> getThresholdId2Outcome(StanceLexicon stanceLexicon, EvaluationDataSet evaluationDataSet) throws AnalysisEngineProcessException {
		Map<String, EvaluationData<String>> result= new HashMap<>();
		
		
		for (JCas jcas : new JCasIterable(evaluationDataSet.getDataReader())){
			if(!useBinCas){
				this.engine.process(jcas);
			}
			result=addEvaluations(result, stanceLexicon,jcas);
		}
		
		return result;
	}


	private Map<String, EvaluationData<String>> addEvaluations(Map<String, EvaluationData<String>> result,StanceLexicon stanceLexicon, JCas jcas) {
		
		//TODO: greedy ?
		for (int i = 0; i <= 100; i+=5) {
			for (int j = 0; j <= 100; j+=5) {

				float upperBound = stanceLexicon.getNthPositivePercent(i);
				float lowerBound = stanceLexicon.getNthNegativePercent(j);
				float commentPolarity = getPolarity(stanceLexicon, jcas);
				String outcome = ressolveOutcome(upperBound,lowerBound,commentPolarity);
				result = addPredictionOutcomePairing(result, outcome, jcas, getThresholdId(i, j));
//				result = addPredictionOutcomePairing(result, outcome, jcas, getThresholdId(upperBound, lowerBound));
			}
		}
		
		return result;
	}

	private Map<String, EvaluationData<String>> addPredictionOutcomePairing(Map<String, EvaluationData<String>> result,String predictedOutcome, JCas jcas, String thresholdId) {
		String goldOutcome= JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance();
		if(result.containsKey(thresholdId)){
			result.get(thresholdId).register(goldOutcome, predictedOutcome);
		}else{
			EvaluationData<String> evalData= new EvaluationData<>();
			evalData.register(goldOutcome, predictedOutcome);
			result.put(thresholdId, evalData);
		}
			
		return result;
	}


	

}
