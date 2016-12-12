package de.uni_due.ltl.featureExtractors.explcitVocab;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.meta.MetaCollectorConfiguration;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.uni_due.ltl.util.CollocationMeasureHelper;
import preprocessing.CommentText;

public class SubdebateVocab extends BinCasMetaDependent {

	public static final String PARAM_VOCAB_TARGET = "SubdebateVocab_Target";
	@ConfigurationParameter(name = PARAM_VOCAB_TARGET, mandatory = true)
	private String target;
	
	
//	public static final String PARAM_COLLOCATION_CUT_OFF = "collocationCutOff";
//	@ConfigurationParameter(name = PARAM_COLLOCATION_CUT_OFF, mandatory = true)
	private double cutoff = 0.005;

	Set<String> highlyAssociatedVocab;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		try {
			Map<String, Float> word2Association = createAssociationMap();
			word2Association=sortMap(word2Association);
			highlyAssociatedVocab = getTop(word2Association, cutoff);
			System.out.println(target+ " "+highlyAssociatedVocab);
		} catch (AnalysisEngineProcessException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}

	private Set<String> getTop(Map<String, Float> word2Association, double minimalValue) {
		Set<String> presentWords = new HashSet<>();
		for (String word : word2Association.keySet()) {
			System.out.println(word+" "+word2Association.get(word));
			if (word2Association.get(word) > minimalValue) {
				presentWords.add(word);
			}else{
				return presentWords;
			}
		}
		return presentWords;
	}

	private Map<String, Float> createAssociationMap()
			throws ResourceInitializationException, AnalysisEngineProcessException {

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		FrequencyDistribution<String> fd_none = new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_present = new FrequencyDistribution<>();
		AnalysisEngine engine = getPreprocessingEngine();

		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			for (TextClassificationOutcome outcome : JCasUtil.select(jcas, TextClassificationOutcome.class)) {
				if (outcome.getOutcome().equals("FAVOR") || outcome.getOutcome().equals("AGAINST")) {
					fd_present = incTokens(fd_present,
							JCasUtil.selectCovered(CommentText.class, outcome).iterator().next());
				}
				if (outcome.getOutcome().equals("NONE")) {
					fd_none = incTokens(fd_none, JCasUtil.selectCovered(CommentText.class, outcome).iterator().next());
				}
			}
		}
		System.out.println(fd_present.getB()+" "+fd_none.getB());
		return createLexiconMap(fd_present,fd_none);
	}

	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		List<Token> tokens = JCasUtil.selectCovered(Token.class, unit);

		for (String word : highlyAssociatedVocab) {
			if (contained(tokens, word)) {
				featList.add(new Feature(target + "_Vocab_" + word, 1));
			} else {
				featList.add(new Feature(target + "_Vocab_" + word, 0));
			}
		}
		return featList;
	}

	private boolean contained(List<Token> tokens, String word) {
		for (Token t : tokens) {
			if (t.getCoveredText().toLowerCase().equals(word))
				return true;
		}
		return false;
	}

	private static FrequencyDistribution<String> incTokens(FrequencyDistribution<String> fd, CommentText text) {
		for (Token t : JCasUtil.selectCovered(Token.class, text)) {
			if (t.getPos().getPosValue().equals("N") || t.getPos().getPosValue().equals("V")
					|| t.getPos().getPosValue().equals("A")) {
				fd.inc(t.getCoveredText().toLowerCase());
			}
		}
		return fd;
	}

	/**
	 * uses the two FrequencyDistributions to generate a map by calculating
	 * getDiffOfGMeans for each word in the two distributions
	 * 
	 * @param favour
	 * @param fd2
	 * @return
	 */
	protected static Map<String, Float> createLexiconMap(FrequencyDistribution<String> fd1,
			FrequencyDistribution<String> fd2) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		// add all cands. dublicates will be removed because set stores just
		// unique entries
		candidates.addAll(fd1.getKeys());
		candidates.addAll(fd2.getKeys());

		CollocationMeasureHelper helper = new CollocationMeasureHelper(fd1, fd2);

		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfDice(word));
		}

		return lexcicon;
	}

	private static Map<String, Float> sortMap(Map<String, Float> unsortMap) {
		// Convert Map to List
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Collections.reverse(list);

		// Convert sorted map back to a Map
		Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
		for (Iterator<Map.Entry<String, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	private static AnalysisEngine getPreprocessingEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(createEngineDescription(ArktweetPosTagger.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
}
