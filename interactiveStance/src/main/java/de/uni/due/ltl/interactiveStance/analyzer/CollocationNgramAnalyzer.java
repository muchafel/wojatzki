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
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;
import de.uni.due.ltl.interactiveStance.util.CollocationMeasureHelper;
import de.uni.due.ltl.interactiveStance.util.EvaluationUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;
import edu.stanford.nlp.io.EncodingPrintWriter.out;

public class CollocationNgramAnalyzer {

	private StanceDB db;
	private AnalysisEngine engine;
	private EvaluationScenario scenario;
	private final int fixedThreshold=75;

	public CollocationNgramAnalyzer(StanceDB db, EvaluationScenario scenario) {
		this.db = db;
		this.engine = getTokenizerEngine();
		this.scenario=scenario;
	}

	/**
	 * TODO: check whether we can get rid of all the casting from int to
	 * String... ID is a INT! 
	 * TODO evaluation needs to be expandend to larger ngrams than unigrams (we may nee mutual expectation)
	 * 
	 * @param selectedTargets
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws UIMAException
	 * @throws TextClassificationException
	 */
	public EvaluationResult analyze(HashMap<String, ExplicitTarget> selectedTargetsFavor,HashMap<String, ExplicitTarget> selectedTargetsAgainst, int maxNgramSize, boolean evaluateTrain)
			throws NumberFormatException, SQLException, UIMAException, TextClassificationException {

		Map<Integer, StanceLexicon> lexica = new HashMap<Integer, StanceLexicon>();

		for (int i = 1; i <= maxNgramSize; i++) {
			lexica.put(i, createStanceLexicon(selectedTargetsFavor,selectedTargetsAgainst, i));
		}
		
		Fscore<String> fscore = null;
		for (int lexiconId : lexica.keySet()) {
			if(evaluateTrain){
				fscore=evaluateUsingLexiconAndFixedThreshold(lexica.get(lexiconId), scenario.getTrainData(), fixedThreshold, fixedThreshold);
			}else{
				fscore=evaluateUsingLexiconAndFixedThreshold(lexica.get(lexiconId), scenario.getTestData(), fixedThreshold, fixedThreshold);
			}
		}
		
		return new EvaluationResult(fscore);
	}

	/**
	 * TODO: check whether we can get rid of all the casting from int to
	 * String... ID is a INT! 
	 * TODO evaluation needs to be expandend to larger ngrams than unigrams (we may nee mutual expectation)
	 * 
	 * @param selectedTargets
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws UIMAException
	 * @throws TextClassificationException
	 */
	public EvaluationResult analyzeOptimized(HashMap<String, ExplicitTarget> selectedTargetsFavor,HashMap<String, ExplicitTarget> selectedTargetsAgainst, int maxNgramSize, boolean evaluateTrain)
			throws NumberFormatException, SQLException, UIMAException, TextClassificationException {

		Map<Integer, StanceLexicon> lexica = new HashMap<Integer, StanceLexicon>();

		for (int i = 1; i <= maxNgramSize; i++) {
			lexica.put(i, createStanceLexicon(selectedTargetsFavor,selectedTargetsAgainst, i));
		}

		
		Fscore<String> fscore = null;
		for (int lexiconId : lexica.keySet()) {
			if(evaluateTrain){
				fscore=evaluateUsingLexicon(lexica.get(lexiconId), scenario.getTrainData());
			}else{
				fscore=evaluateUsingLexicon(lexica.get(lexiconId), scenario.getTestData());
			}
		}
		
		return new EvaluationResult(fscore);
	}
	
	
	
	/**
	 * runs an (optimized) evaluation with a threshold which is optimized on the given data
	 * @param stanceLexicon
	 * @param evaluationDataSet
	 * @return
	 * @throws AnalysisEngineProcessException
	 */
	public Fscore<String> evaluateUsingLexicon(StanceLexicon stanceLexicon, EvaluationDataSet evaluationDataSet) throws AnalysisEngineProcessException {
		Map<String,EvaluationData<String>> thresholdId2Outcome=getThresholdId2Outcome(stanceLexicon,evaluationDataSet);
		String topConfig=getTopConfig(thresholdId2Outcome);
		System.out.println("Using threshold config "+ topConfig+" : "+EvaluationUtil.getSemEvalMeasure(new Fscore<>(thresholdId2Outcome.get(topConfig))));
		System.out.println(new ConfusionMatrix<String>(thresholdId2Outcome.get(topConfig)));
		return new Fscore<>(thresholdId2Outcome.get(topConfig));
	}
	
	/**
	 * runs an pure evaluation on the given data set with a fixed threshold
	 * @param stanceLexicon
	 * @param evaluationDataSet
	 * @param upperPercentage
	 * @param lowerPercentage
	 * @return
	 * @throws AnalysisEngineProcessException
	 */
	public Fscore<String> evaluateUsingLexiconAndFixedThreshold(StanceLexicon stanceLexicon, EvaluationDataSet evaluationDataSet,int upperPercentage, int lowerPercentage) throws AnalysisEngineProcessException {
		EvaluationData<String> evalData = new EvaluationData<>();
		float upperBound = stanceLexicon.getNthPositivePercent(upperPercentage);
		float lowerBound = stanceLexicon.getNthNegativePercent(lowerPercentage);
		
		for (JCas jcas : new JCasIterable(evaluationDataSet.getDataReader())){
			this.engine.process(jcas);
			String goldOutcome= JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance();
			float commentPolarity = getPolarity(stanceLexicon, jcas);
			String outcome = ressolveOutcome(upperBound,lowerBound,commentPolarity);

			evalData.register(goldOutcome, outcome);
			
		}
		System.out.println("Using threshold config "+ upperPercentage+"_"+lowerPercentage+" : "+EvaluationUtil.getSemEvalMeasure(new Fscore<>(evalData)));
		System.out.println(new ConfusionMatrix<String>(evalData));
		return new Fscore<>(evalData);
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
			this.engine.process(jcas);
			result=addEvaluations(result, stanceLexicon,jcas);
		}
//		for(String thresholdId: result.keySet()){
//			Fscore<String> fscore=new Fscore<>(result.get(thresholdId));
//			System.out.println(thresholdId+":"+ EvaluationUtil.getSemEvalMeasure(fscore));
//
////			System.out.println(thresholdId+" : "+ getSemEvalMeasure(fscore)+ " | F(none): "+fscore.getScoreForLabel("NONE")+" | F(against): "+fscore.getScoreForLabel("AGAINST")+" | F(favor): "+fscore.getScoreForLabel("FAVOR"));
//		}
		
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

	private String getThresholdId(float upperBound, float lowerBound) {
		return String.valueOf(upperBound)+"_"+String.valueOf(lowerBound);
	}

	private float getPolarity(StanceLexicon stanceLexicon, JCas jcas) {
		float result=0;
		
		for(Token token : JCasUtil.select(jcas, Token.class)){
			result+=stanceLexicon.getStancePolarity(token.getCoveredText().toLowerCase());
		}
		
		return result;
	}

	/**
	 * @param selectedTargets
	 * @param selectedTargetsAgainst 
	 * @param i
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws UIMAException
	 * @throws TextClassificationException
	 */
	private StanceLexicon createStanceLexicon(HashMap<String, ExplicitTarget> selectedTargetsFavor, HashMap<String, ExplicitTarget> selectedTargetsAgainst, int i)
			throws NumberFormatException, SQLException, UIMAException, TextClassificationException {

		System.out.println("create lexicon for " + i + "-grams");

		FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
		FrequencyDistribution<String> against = new FrequencyDistribution<String>();
		
		//TODO refactoring (this is heavy code duplication)
		// Adding ngrams in natural order 	
		
		for (String id : selectedTargetsFavor.keySet()) {
			for(DataPoint point: db.getDataPointsByDataSetId(Integer.valueOf(id))){
				JCas jcas = JCasFactory.createText(point.getText(), "en");
				engine.process(jcas);
				if (point.getLabel().equals("FAVOR")) {
					favor = addFd(getNgrams(jcas, i), favor);
				} else {
					against = addFd(getNgrams(jcas, i), against);
				}
			}
			System.out.println(".");
		}
		// Invert Logic		
		for (String id : selectedTargetsAgainst.keySet()) {
			for(DataPoint point: db.getDataPointsByDataSetId(Integer.valueOf(id))){
				JCas jcas = JCasFactory.createText(point.getText(), "en");
				engine.process(jcas);
				if (point.getLabel().equals("FAVOR")) {
					against = addFd(getNgrams(jcas, i), against);
				} else {
					favor = addFd(getNgrams(jcas, i), favor);
				}
			}
			System.out.println(".");
		}
		
		
		return createLexiconFromDistributions(favor, against);
	}

	private FrequencyDistribution<String> addNgrams(int i, Set<String> keySet, FrequencyDistribution<String> favor) {
		// TODO Auto-generated method stub
		return null;
	}

	private FrequencyDistribution<String> getNgrams(JCas jcas, int i) throws TextClassificationException {
		FrequencyDistribution<String> ngrams = new FrequencyDistribution<>();
		for (Sentence s : JCasUtil.select(jcas, Sentence.class)) {
			ngrams = addNgrams(jcas, s, true, i, ngrams);
		}

		return ngrams;
	}

	private FrequencyDistribution<String> addNgrams(JCas jcas, Sentence s, boolean lowerCase, int ngramCount,
			FrequencyDistribution<String> ngrams) {
		for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, s)), 1,
				ngramCount)) {

			if (lowerCase) {
				ngram = lower(ngram);
			}
			
			String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
			
			//TODO: add better NLP
			if(!ngramString.matches("[,.!?;:]")){
				ngrams.inc(ngramString);
			}
		}
		return ngrams;
	}

	public static List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}

	private FrequencyDistribution<String> addFd(FrequencyDistribution<String> ngrammsToAdd,
			FrequencyDistribution<String> addingTo) {
		for (String ngram : ngrammsToAdd.getKeys()) {
			addingTo.addSample(ngram, ngrammsToAdd.getCount(ngram));
		}
		return addingTo;
	}

	private AnalysisEngine getTokenizerEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
//			builder.add(createEngineDescription(createEngineDescription(ArktweetTokenizer.class)));
			builder.add(createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

	/**
	 * takes a favor and and against frequency distribution and returns a stance lexicon
	 * @param favour
	 * @param against
	 * @return
	 */
	public StanceLexicon createLexiconFromDistributions(FrequencyDistribution<String> favour,
			FrequencyDistribution<String> against) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		// add all cands. dublicates will be removed because map stores just
		// unique entries
		candidates.addAll(favour.getKeys());
		candidates.addAll(against.getKeys());

		CollocationMeasureHelper helper = new CollocationMeasureHelper(favour, against);

		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfDice(word));
		}

		return new StanceLexicon(lexcicon);
	}

	

	private String ressolveOutcome(float upperBound, float lowerBound, float commentPolarity) {
		if (commentPolarity >= upperBound) {
			return "FAVOR";
		} else if (commentPolarity < upperBound && commentPolarity > lowerBound) {
			return "NONE";
		} else {
			return "AGAINST";
		}
		
	}

}
