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
import java.util.HashMap;
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
import org.apache.uima.resource.ResourceInitializationException;

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
import lexicons.WordEmbeddingLexicon;
import types.FunctionalPartAnnotation;
import util.CollocationMeasureHelper;
import util.StanceConstants;
import util.wordEmbeddingUtil.WordEmbeddingHelper;

public abstract class SummedStance_base extends BinCasMetaDependent {

	public static final String PARAM_USE_STANCE_LEXICON = "useStanceLexicon";
	@ConfigurationParameter(name = PARAM_USE_STANCE_LEXICON, mandatory = true, defaultValue = "true")
	protected boolean useStances;

	public static final String PARAM_USE_HASHTAG_LEXICON = "useHashTagLexicon";
	@ConfigurationParameter(name = PARAM_USE_HASHTAG_LEXICON, mandatory = true, defaultValue = "true")
	protected boolean useHashtags;

	public static final String PARAM_USE_POLARITY = "usePolarity";
	@ConfigurationParameter(name = PARAM_USE_POLARITY, mandatory = true, defaultValue = "true")
	protected boolean usePolarity;

	protected StanceLexicon wordStanceLexicon;
	protected StanceLexicon hashTagStanceLexicon;
	protected List<String> stopwords;
	protected boolean useStopwords = true;

	/**
	 * reads the training data and creates a stance lexcicon object
	 * 
	 * @param binCasDir
	 * @param tokenMode
	 * @return
	 * @throws CollectionException
	 * @throws UIMAException
	 * @throws IOException
	 */
	protected StanceLexicon readLexicon(String binCasDir, RelevantTokens tokenMode)
			throws CollectionException, UIMAException, IOException {
		// create favor and against fds foreach target
		FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
		FrequencyDistribution<String> against = new FrequencyDistribution<String>();

		CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		// iterate over all CASes that have been stored by the meta collector
		while (reader.hasNext()) {
			JCas jcas = JCasFactory.createJCas();
			reader.getNext(jcas.getCas());

			Collection<Token> relevantTokens = getRelevantTokens(jcas, tokenMode);

			if (usePolarity) {
				// if tweet is against add tokens to favor frequency
				// distribution
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("FAVOR")) {
					favor = incAll(favor, relevantTokens, stopwords, useStopwords);
				}

				// if tweet is against add tokens to favor frequency
				// distribution
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("AGAINST")) {
					against = incAll(against, relevantTokens, stopwords, useStopwords);
				}
			} else {
				// STANCE VS NONE
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("STANCE")) {
					favor = incAll(favor, relevantTokens, stopwords, useStopwords);
					// favor = incAllLemmas(against, relevantTokens, stopwords,
					// useStopwords);
				}
				// STANCE VS NONE
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("NONE")) {
					against = incAll(against, relevantTokens, stopwords, useStopwords);
					// against = incAllLemmas(against, relevantTokens,
					// stopwords, useStopwords);
				}
			}
		}

		// STATIC INC (KNOWLEDGE INFUSION)
		// favor=infuseKnowledge(favor);

		//
		// write("favor", favor.getMostFrequentSamples(500),favor);
		// write("against", against.getMostFrequentSamples(500),against);
		//

		// System.out.println(favor.getMostFrequentSamples(500));

		// //TODO: just an experiment!!!
		// FrequencyDistribution<String> favor_reduced = reduce(favor);
		// FrequencyDistribution<String> against_reduced = reduce(against);

		Map<String, Float> lexicon = createLexiconMap(favor, against);
		// writeLexicon("HillaryClinton_temp", sortMap(lexicon));
		// System.out.println("done with lexicon");
		return new StanceLexicon(lexicon);
	}

	private FrequencyDistribution<String> incAll_ngrams(Collection<Token> relevantTokens,
			FrequencyDistribution<String> favor) {

		for (List<String> ngram : new NGramStringListIterable(toText(relevantTokens), 1, 3)) {
			favor.inc(StringUtils.join(ngram, "_"));
		}
		return favor;
	}

	/**
	 * writes to the specified resource in the form Token:Stance
	 * 
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
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
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
		for (String word : full.getKeys()) {
			if (full.getCount(word) > 1) {
				reduced.addSample(word, full.getCount(word));
			}
		}
		return reduced;
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

	protected FrequencyDistribution<String> incAllLemmas(FrequencyDistribution<String> freq, Collection<Token> tokens,
			List<String> stopwords, boolean useStopwords) {
		for (Token t : tokens) {
			if (useStopwords) {
				if (!stopwords.contains(t.getCoveredText())) {
					freq.inc(t.getLemma().getValue().toLowerCase());
				}
			} else
				freq.inc(t.getLemma().getValue().toLowerCase());
		}
		return freq;
	}

	private static void write(String polarity, List<String> mostFrequentSamples, FrequencyDistribution<String> fd) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				"src/main/resources/lists/stanceLexicons/HillaryClinton_temp/" + polarity + ".txt", true)))) {
			for (String key : mostFrequentSamples) {
				out.println(key + " " + fd.getCount(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * returns a word-embedding vector representation of a jcases that are pro/stance
	 * @param binCasDir
	 * @param stopwords2 
	 * @param lexicon2
	 * @param stopwords2
	 * @return
	 * @throws IOException 
	 * @throws UIMAException 
	 * @throws CollectionException 
	 */
	protected List<Float> readStanceVector(String binCasDir, WordEmbeddingLexicon lexicon, List<String> stopwords) throws CollectionException, UIMAException, IOException {
		CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		Set<String> embeddingCandidates= new HashSet<String>();
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		// iterate over all CASes that have been stored by the meta collector
		while (reader.hasNext()) {
			JCas jcas = JCasFactory.createJCas();
			reader.getNext(jcas.getCas());

			//TODO: check for stopwords?
			Collection<Token> relevantTokens = new HashSet<>();
			for(Token t: JCasUtil.select(jcas, Token.class)){
				//filter stopwords and punctuations
				if (!stopwords.contains(t.getCoveredText().toLowerCase()) || !t.getPos().getPosValue().equals(",")
						|| !t.getPos().getPosValue().equals(".") ||! t.getPos().getPosValue().equals("$")
						|| !t.getPos().getPosValue().equals("'") ||! t.getPos().getPosValue().equals(":")){
					relevantTokens.add(t);
				}
			}

			if (usePolarity) {
				// if tweet is against add tokens to favor frequency
				// distribution
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("FAVOR")) {
					for(Token t: relevantTokens)embeddingCandidates.add(t.getCoveredText().toLowerCase());
				}

			} else {
				// STANCE VS NONE
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("STANCE")) {
					for(Token t: relevantTokens)embeddingCandidates.add(t.getCoveredText().toLowerCase());
				}
			}
		}
		return helper.getAveragedSentenceVector(embeddingCandidates);
	}
	
	/**
	 * extracts the top 10 concepts and returns thier wordembeddings
	 * @param binCasDir
	 * @param stopwords 
	 * @param lexicon
	 * @param stopwords
	 * @return
	 * @throws IOException 
	 * @throws UIMAException 
	 * @throws CollectionException 
	 */
	protected  Map<String,List<Float>> readKeyConceptsVector(String binCasDir, WordEmbeddingLexicon lexicon, List<String> stopwords) throws CollectionException, UIMAException, IOException {
		// create favor and against fds foreach target
				FrequencyDistribution<String> favor = new FrequencyDistribution<String>();

				CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
						BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
						BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

				// iterate over all CASes that have been stored by the meta collector
				while (reader.hasNext()) {
					JCas jcas = JCasFactory.createJCas();
					reader.getNext(jcas.getCas());

					Collection<Token> relevantTokens = getRelevantTokens(jcas,RelevantTokens.SENTENCE_NOUNS);

					if (usePolarity) {
						// if tweet is against add tokens to favor frequency
						// distribution
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("FAVOR")) {
							favor = incAll(favor, relevantTokens, stopwords, useStopwords);
						}

					} else {
						// STANCE VS NONE
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("STANCE")) {
							favor = incAll(favor, relevantTokens, stopwords, useStopwords);
						}
					}
				}
				//TODO: normalize embeddings
				return getWordEmbeddingsMap(favor.getMostFrequentSamples(20),lexicon);
	}

	private Map<String, List<Float>> getWordEmbeddingsMap(List<String> mostFrequentSamples, WordEmbeddingLexicon lexicon) {
		Map<String, List<Float>> result= new HashMap<String, List<Float>>();
		for(String key: mostFrequentSamples){
			result.put(key, lexicon.getEmbedding_WithFallback(key));
		}
		return result;
	}
}
