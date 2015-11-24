package featureExtractors;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.api.features.meta.MetaCollector;
import de.tudarmstadt.ukp.dkpro.tc.api.features.meta.MetaDependent;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.util.NGramUtils;
import java_cup.reduce_action;
import lexicons.StanceLexicon;
import types.FunctionalPartAnnotation;
import util.CollocationMeasureHelper;
import util.StanceConstants;

public abstract class SummedStance_base extends FeatureExtractorResource_ImplBase
		implements DocumentFeatureExtractor, MetaDependent, StanceConstants {

	public enum RelevantTokens {
		ALL, HASHTAG, SENTENCE, SENTENCE_FILTERED, SENTENCE_NOUNS_VEBS_ADJECTIVES
	}

	public static final String PARAM_USE_STANCE_LEXICON = "useStanceLexicon";
	@ConfigurationParameter(name = PARAM_USE_STANCE_LEXICON, mandatory = true, defaultValue = "true")
	protected boolean useStances;

	public static final String PARAM_USE_HASHTAG_LEXICON = "useHashTagLexicon";
	@ConfigurationParameter(name = PARAM_USE_HASHTAG_LEXICON, mandatory = true, defaultValue = "true")
	protected boolean useHashtags;

	public static final String PARAM_STANCE_LEXICON_DIR = "wordStanceLexicon";
	@ConfigurationParameter(name = PARAM_STANCE_LEXICON_DIR, mandatory = true)
	protected String wordStanceDir;

	protected StanceLexicon wordStanceLexicon;
	protected StanceLexicon hashTagStanceLexicon;
	protected List<String> stopwords;
	protected boolean useStopwords = true;

	@Override
	public List<Class<? extends MetaCollector>> getMetaCollectorClasses() {
		List<Class<? extends MetaCollector>> metaCollectorClasses = new ArrayList<Class<? extends MetaCollector>>();
		metaCollectorClasses.add(StanceLexiconMetaCollector.class);

		return metaCollectorClasses;
	}

	/**
	 * reads the training data and creates a stance lexcicon object
	 * 
	 * @param wordStanceDir
	 * @param tokenMode
	 * @return
	 * @throws CollectionException
	 * @throws UIMAException
	 * @throws IOException
	 */
	protected StanceLexicon readLexicon(String wordStanceDir, RelevantTokens tokenMode)
			throws CollectionException, UIMAException, IOException {
		// create favor and against fds foreach target
		FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
		FrequencyDistribution<String> against = new FrequencyDistribution<String>();

		CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, wordStanceDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		// iterate over all cases that have been stored by the meta collector
		while (reader.hasNext()) {
			JCas jcas = JCasFactory.createJCas();
			reader.getNext(jcas.getCas());

			Collection<Token> relevantTokens = getRelevantTokens(jcas, tokenMode);
			// if tweet is against add tokens to favor frequency distribution
//			if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("FAVOR")) {
//				favor = incAll(favor, relevantTokens, stopwords, useStopwords);
//			}
			//STANCE VS NONE
			if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("STANCE")) {
				favor = incAll(favor, relevantTokens, stopwords, useStopwords);
//				favor = incAll_ngrams(relevantTokens,favor);
			}
			
			// if tweet is against add tokens to favor frequency distribution
//			if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
//					.equals("AGAINST")) {
//				against = incAll(against, relevantTokens, stopwords, useStopwords);
//			}
			
			//STANCE VS NONE
			if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("NONE")) {
				against = incAll(against, relevantTokens, stopwords, useStopwords);
//				against = incAll_ngrams(relevantTokens,against);
			}
			
		}
		
		//STATIC INC (KNOWLEDGE INFUSION)
//		favor=infuseKnowledge(favor);
		
		
		//
//		write("favor", favor.getMostFrequentSamples(500),favor);
//		write("against", against.getMostFrequentSamples(500),against);
//		
		
//		System.out.println(favor.getMostFrequentSamples(500));
		
//		//TODO: just an experiment!!!
//		FrequencyDistribution<String> favor_reduced = reduce(favor);
//		FrequencyDistribution<String> against_reduced = reduce(against);
		
		Map<String, Float> lexicon = createLexiconMap(favor, against);
//		writeLexicon("HillaryClinton_temp", sortMap(lexicon));
//		System.out.println("done with lexicon");
		return new StanceLexicon(lexicon);
	}

	private FrequencyDistribution<String> incAll_ngrams(Collection<Token> relevantTokens, FrequencyDistribution<String> favor) {
		
		for (List<String> ngram : new NGramStringListIterable(toText(relevantTokens), 1, 3)) {
			 favor.inc(StringUtils.join(ngram, "_"));
		 }
		return favor;
	}

	private FrequencyDistribution<String> infuseKnowledge(FrequencyDistribution<String> favor) {
		favor.addSample("president", 200);
		favor.addSample("democratic", 200);
		favor.addSample("republican", 200);
		favor.addSample("arkansas", 200);
		favor.addSample("party", 200);
		favor.addSample("candidate", 200);
		favor.addSample("equality", 200);
		return favor;
	}

	/**
	 * writes to the specified resource in the form Token:Stance
	 * @param target
	 * @param lexcicon
	 */
		private static void writeLexicon(String target, Map<String, Float> lexcicon) {
			
			try (PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("src/main/resources/lists/stanceLexicons/" + target + "/stanceLexicon.txt", true)))) {
				for (String key : lexcicon.keySet()) {
					out.println(key + ":" + lexcicon.get(key));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		
		private static Map<String, Float> sortMap(Map<String, Float> unsortMap) {
			// Convert Map to List
					List<Map.Entry<String, Float>> list = 
						new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

					// Sort list with comparator, to compare the Map values
					Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
						public int compare(Map.Entry<String, Float> o1,
			                                           Map.Entry<String, Float> o2) {
							return (o1.getValue()).compareTo(o2.getValue());
						}
					});

					// Convert sorted map back to a Map
					Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
					for (Iterator<Map.Entry<String, Float>> it = list.iterator(); it.hasNext();) {
						Map.Entry<String, Float> entry = it.next();
						sortedMap.put(entry.getKey(), entry.getValue());
					}
		return sortedMap;
	}	
		
	private FrequencyDistribution<String> reduce(FrequencyDistribution<String> full) {
		FrequencyDistribution<String> reduced = new FrequencyDistribution<String>();
		for(String word: full.getKeys()){
			if(full.getCount(word)>1){
				reduced.addSample(word, full.getCount(word));
			}
		}
		return reduced;
	}

	/**
	 * return
	 * 
	 * @param jcas
	 * @param tokenMode
	 * @return
	 */
	protected Collection<Token> getRelevantTokens(JCas jcas, RelevantTokens tokenMode) {
		if (tokenMode.equals(RelevantTokens.ALL))
			return JCasUtil.select(jcas, Token.class);
		else if (tokenMode.equals(RelevantTokens.SENTENCE))
			return getFunctionalTokens(jcas, SENTENCE_FUNCTION);
		else if (tokenMode.equals(RelevantTokens.HASHTAG))
			return getFunctionalTokens(jcas, TAG_FUNCTION);
		else if (tokenMode.equals(RelevantTokens.SENTENCE_FILTERED))
			return filterTokens(getFunctionalTokens(jcas, SENTENCE_FUNCTION));
		else if (tokenMode.equals(RelevantTokens.SENTENCE_NOUNS_VEBS_ADJECTIVES))
			return filterTokensNounsVerbsAdjectives(getFunctionalTokens(jcas, SENTENCE_FUNCTION));
		else
			return null;
	}


	private Collection<Token> filterTokensNounsVerbsAdjectives(Collection<Token> input) {
		Collection<Token> tokens = new HashSet<Token>();
		for (Token t : input) {
			String pos = t.getPos().getClass().getSimpleName();
//			System.out.println(t.getCoveredText()+" "+pos);
			if (pos.equals("NN") || pos.equals("V") || pos.equals("ADJ")) {
				tokens.add(t);
			}
		}
		return tokens;
	}

	/**
	 * 
	 * @param select
	 * @return
	 */
	private Collection<Token> filterTokens(Collection<Token> input) {
		Collection<Token> tokens = new HashSet<Token>();
		for (Token t : input) {
			String pos = t.getPos().getPosValue();
			if (!pos.equals(".") && !pos.equals(",") && !pos.equals(":") && !pos.equals("DT") && !pos.equals("IN")
					&& !pos.equals("-LRB-") && !pos.equals("-RRB-")) {
				tokens.add(t);
			}
		}
		return tokens;
	}

	/**
	 * returns only tokens that have been annotated with the specified function
	 * 
	 * @param jcas
	 * @param function
	 * @return
	 */
	protected Collection<Token> getFunctionalTokens(JCas jcas, String function) {
		Collection<Token> tokens = new HashSet<Token>();
		for (FunctionalPartAnnotation part : JCasUtil.select(jcas, FunctionalPartAnnotation.class)) {
			if (part.getFunction().equals(function)) {
				tokens.addAll(JCasUtil.selectCovered(Token.class, part));
			}
		}
		return tokens;
	}

	/**
	 * uses the two FrequencyDistributions to generate a map by calculating
	 * getDiffOfGMeans for each word in the two distributions
	 * 
	 * @param favour
	 * @param against
	 * @return
	 */
	protected static Map<String, Float> createLexiconMap(FrequencyDistribution<String> favour,
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

		return lexcicon;
	}

	/**
	 * read in a file and return a list of strings
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected List<String> init(String path) throws IOException {
		List<String> stopwords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
		}
		return stopwords;
	}

	protected FrequencyDistribution<String> incAll(FrequencyDistribution<String> freq, Collection<Token> tokens,
			List<String> stopwords, boolean useStopwords) {
		for (Token t : tokens) {
			if (useStopwords) {
				if (!stopwords.contains(t.getCoveredText())) {
					freq.inc(t.getCoveredText());
				}
			} else
				freq.inc(t.getCoveredText());
		}
		return freq;
	}
	
	private static void write( String polarity, List<String> mostFrequentSamples, FrequencyDistribution<String> fd) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("src/main/resources/lists/stanceLexicons/HillaryClinton_temp/"+polarity+".txt", true)))) {
			for (String key : mostFrequentSamples) {
				out.println(key +" "+fd.getCount(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
}
