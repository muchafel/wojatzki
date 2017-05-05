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
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.features.ngram.util.NGramUtils;
import org.springframework.expression.spel.support.ReflectionHelper.ArgsMatchKind;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.util.CollocationMeasureHelper;

public class CollocationNgramAnalyzer {

	private StanceDB db;
	private int ngramSize;
	private AnalysisEngine engine;

	public CollocationNgramAnalyzer(StanceDB db) {
		this.db = db;
		this.ngramSize = ngramSize;
		this.engine = getTokenizerEngine();
	}

	/**
	 * TODO: check whether we can get rid of all the casting from int to
	 * String... ID is a INT! TODO: expand to ngrams TODO: bind to train data
	 * 
	 * @param selectedTargets
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws UIMAException
	 * @throws TextClassificationException
	 */
	public void analyze(HashMap<String, ExplicitTarget> selectedTargetsFavor,HashMap<String, ExplicitTarget> selectedTargetsAgainst, int maxNgramSize)
			throws NumberFormatException, SQLException, UIMAException, TextClassificationException {

		Map<Integer, StanceLexicon> lexica = new HashMap<Integer, StanceLexicon>();

		for (int i = 1; i <= maxNgramSize; i++) {
			lexica.put(i, createStanceLexicon(selectedTargetsFavor,selectedTargetsAgainst, i));
		}
		
		
		
		for (int i : lexica.keySet()) {
			System.out.println(i + " ----");
			System.out.println(lexica.get(i).prettyPrint());
		}

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
		// Adding ngrams in naturla order 		
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
		}
		
		
		return createLexiconFromDistributions(favor, against);
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
		for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, s)), ngramCount,
				ngramCount)) {

			if (lowerCase) {
				ngram = lower(ngram);
			}

			String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
			ngrams.inc(ngramString);
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

	private static StanceLexicon createLexiconFromDistributions(FrequencyDistribution<String> favour,
			FrequencyDistribution<String> against) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		// add all cands. dublicates will be removed because map stores just
		// unique entries
		candidates.addAll(favour.getKeys());
		candidates.addAll(against.getKeys());

		CollocationMeasureHelper helper = new CollocationMeasureHelper(favour, against);

		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfGMeans(word));
		}

		return new StanceLexicon(lexcicon);
	}

}
