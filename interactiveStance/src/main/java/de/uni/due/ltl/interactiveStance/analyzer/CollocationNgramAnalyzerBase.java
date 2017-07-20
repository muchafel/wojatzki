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
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.exception.TextClassificationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;
import de.uni.due.ltl.interactiveStance.util.CollocationMeasureHelper;
import de.uni.due.ltl.interactiveStance.util.EvaluationUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public abstract class CollocationNgramAnalyzerBase {
	protected StanceDB db;
	protected AnalysisEngine engine;
	protected EvaluationScenario scenario;
	protected ExperimentLogging logging;
	protected boolean useBinCas;
//	private final int fixedThreshold=75;

	public CollocationNgramAnalyzerBase(StanceDB db, EvaluationScenario scenario,ExperimentLogging logging, boolean useBinCas) {
		this.db = db;
		this.engine = getTokenizerEngine();
		this.scenario=scenario;
		this.logging= logging;
		this.useBinCas=useBinCas;
	}

	/**
	 * TODO: check whether we can get rid of all the casting from int to
	 * String... ID is a INT! 
	 * TODO evaluation needs to be expandend to larger ngrams than unigrams (we may nee mutual expectation)
	 * @param logging 
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
		
		//TODO Cascading logic using backoff
		EvaluationData<String> result = null;
		for (int lexiconId : lexica.keySet()) {
			if(evaluateTrain){
				result=evaluateUsingLexicon(lexica.get(lexiconId), scenario.getTrainData());
			}else{
				result=evaluateUsingLexicon(lexica.get(lexiconId), scenario.getTestData());
			}
		}
		
		return new EvaluationResult(result);
	}

	protected abstract EvaluationData<String> evaluateUsingLexicon(StanceLexicon stanceLexicon, EvaluationDataSet trainData) throws AnalysisEngineProcessException;
	
	protected String getThresholdId(float upperBound, float lowerBound) {
		return String.valueOf(upperBound)+"_"+String.valueOf(lowerBound);
	}

	protected float getPolarity(StanceLexicon stanceLexicon, JCas jcas) {
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
	protected StanceLexicon createStanceLexicon(HashMap<String, ExplicitTarget> selectedTargetsFavor, HashMap<String, ExplicitTarget> selectedTargetsAgainst, int i)
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


	protected FrequencyDistribution<String> getNgrams(JCas jcas, int i) throws TextClassificationException {
		FrequencyDistribution<String> ngrams = new FrequencyDistribution<>();
		for (Sentence s : JCasUtil.select(jcas, Sentence.class)) {
			ngrams = addNgrams(jcas, s, true, i, ngrams);
		}
		return ngrams;
	}

	protected FrequencyDistribution<String> addNgrams(JCas jcas, Sentence s, boolean lowerCase, int ngramCount,
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

	protected static List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}

	protected FrequencyDistribution<String> addFd(FrequencyDistribution<String> ngrammsToAdd,
			FrequencyDistribution<String> addingTo) {
		for (String ngram : ngrammsToAdd.getKeys()) {
			addingTo.addSample(ngram, ngrammsToAdd.getCount(ngram));
		}
		return addingTo;
	}

	protected AnalysisEngine getTokenizerEngine() {
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

	

	protected String ressolveOutcome(float upperBound, float lowerBound, float commentPolarity) {
		if (commentPolarity >= upperBound) {
			return "FAVOR";
		} else if (commentPolarity < upperBound && commentPolarity > lowerBound) {
			return "NONE";
		} else {
			return "AGAINST";
		}
		
	}
}
