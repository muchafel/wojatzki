package de.uni_due.ltl.featureExtractors.explcitVocab;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

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

import org.apache.commons.lang.StringUtils;
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
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.uni_due.ltl.util.CollocationMeasureHelper;
import preprocessing.CommentText;

public class SubdebateVocabNgrams extends BinCasMetaDependent {

	public static final String PARAM_VOCAB_TARGET = "SubdebateVocab_Target";
	@ConfigurationParameter(name = PARAM_VOCAB_TARGET, mandatory = true)
	private String target;
	
//	public static final String PARAM_COLLOCATION_CUT_OFF = "collocationCutOff";
//	@ConfigurationParameter(name = PARAM_COLLOCATION_CUT_OFF, mandatory = true)
	private double cutoff = 0.01;

	Set<String> highlyAssociatedUniGrams;
	Set<String> highlyAssociatedBiGrams;
	Set<String> highlyAssociatedTriGrams;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		try {
			Map<String, Float> uniGram2Association = createAssociationMapNGrams(1);
			Map<String, Float> biGram2Association = createAssociationMapNGrams(2);
			Map<String, Float> triGram2Association = createAssociationMapNGrams(3);
			uniGram2Association=sortMap(uniGram2Association);
			biGram2Association=sortMap(biGram2Association);
			triGram2Association=sortMap(triGram2Association);
			highlyAssociatedUniGrams = getTopNgrams(uniGram2Association, cutoff);
			highlyAssociatedBiGrams = getTopNgrams(biGram2Association, cutoff);
			highlyAssociatedTriGrams = getTopNgrams(triGram2Association, cutoff);
			System.out.println(target+ " "+highlyAssociatedUniGrams);
			System.out.println(target+ " "+highlyAssociatedBiGrams);
			System.out.println(target+ " "+highlyAssociatedTriGrams);
		} catch (AnalysisEngineProcessException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}

	private Set<String> getTopNgrams(Map<String, Float> word2Association, double minimalValue) {
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

	private Map<String, Float> createAssociationMapNGrams(int i)
			throws ResourceInitializationException, AnalysisEngineProcessException {

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		FrequencyDistribution<String> fd_none = new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_present = new FrequencyDistribution<>();

		for (JCas jcas : new JCasIterable(reader)) {
			for (TextClassificationOutcome outcome : JCasUtil.select(jcas, TextClassificationOutcome.class)) {
				if (outcome.getOutcome().equals("FAVOR") || outcome.getOutcome().equals("AGAINST")) {
					fd_present=incNgrams(fd_present, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),i));
				}
				if (outcome.getOutcome().equals("NONE")) {
					fd_none=incNgrams(fd_none, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),i));
				}
			}
		}
		System.out.println(fd_present.getB()+" "+fd_none.getB());
		return createLexiconMap(fd_present,fd_none);
	}

	
	private FrequencyDistribution<String> incNgrams(FrequencyDistribution<String> fd,
			FrequencyDistribution<String> currentNgrams) {
		for(String n_gram:currentNgrams.getKeys()){
			fd.addSample(n_gram, currentNgrams.getCount(n_gram));
		}
		return fd;
	}



	private FrequencyDistribution<String> getNgrams(CommentText text, int n_gramSize) {
		FrequencyDistribution<String> annoNgrams = new FrequencyDistribution<String>();

		for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, text)), n_gramSize,
				n_gramSize)) {
			ngram = lower(ngram);
			String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
			annoNgrams.inc(ngramString);
		}
		return annoNgrams;
	}
	
	public static List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}
	
	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		CommentText text=JCasUtil.selectCovered(CommentText.class,unit).iterator().next();

		FrequencyDistribution<String> fd_uni=getNgrams(text, 1);
		FrequencyDistribution<String> fd_bi=getNgrams(text, 2);
		FrequencyDistribution<String> fd_tri=getNgrams(text, 3);

				
//		for (String word : highlyAssociatedUniGrams) {
//			if (fd_uni.contains(word)) {
//				featList.add(new Feature(target + "_Ngram_" + word, 1));
//			} else {
//				featList.add(new Feature(target + "_Ngram_" + word, 0));
//			}
//		}
		for (String word : highlyAssociatedBiGrams) {
			if (fd_bi.contains(word)) {
				featList.add(new Feature(target + "_Ngram_" + word, 1));
			} else {
				featList.add(new Feature(target + "_Ngram_" + word, 0));
			}
		}
		for (String word : highlyAssociatedTriGrams) {
			if (fd_tri.contains(word)) {
				featList.add(new Feature(target + "_Ngram_" + word, 1));
			} else {
				featList.add(new Feature(target + "_Ngram_" + word, 0));
			}
		}
		return featList;
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
