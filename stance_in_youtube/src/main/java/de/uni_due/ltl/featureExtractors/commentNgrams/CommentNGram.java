package de.uni_due.ltl.featureExtractors.commentNgrams;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.internal.ExtendedLogger;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.meta.MetaCollectorConfiguration;
import org.dkpro.tc.api.features.meta.MetaDependent;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.meta.LuceneNGramMetaCollector;
import org.dkpro.tc.features.ngram.util.TermFreqTuple;

import com.google.common.collect.MinMaxPriorityQueue;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.frequency.tfidf.model.DfModel;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import preprocessing.CommentText;

public class CommentNGram extends FeatureExtractorResource_ImplBase implements FeatureExtractor,MetaDependent {

	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true)
	protected File luceneDir;
	
	public static final String PARAM_UNIQUE_NAME = "commentNgramUniqueName";
	@ConfigurationParameter(name = PARAM_UNIQUE_NAME, mandatory = true)
	protected String uniqueName;

	public static final String PARAM_NGRAM_MIN_N = "commentNgramMinN";
	@ConfigurationParameter(name = PARAM_NGRAM_MIN_N, mandatory = true, defaultValue = "1")
	protected int ngramMinN;

	public static final String PARAM_NGRAM_MAX_N = "commentNgramMaxN";
	@ConfigurationParameter(name = PARAM_NGRAM_MAX_N, mandatory = true, defaultValue = "3")
	protected int ngramMaxN;

	public static final String PARAM_NGRAM_USE_TOP_K = "ngramUseTopK";
	@ConfigurationParameter(name = PARAM_NGRAM_USE_TOP_K, mandatory = true, defaultValue = "500")
	protected int ngramUseTopK;

	public static final String PARAM_TF_IDF_CALCULATION = "tfIdfCalculation";
	@ConfigurationParameter(name = PARAM_TF_IDF_CALCULATION, mandatory = true, defaultValue = "false")
	protected boolean tfIdfCalculation;

	public static final String PARAM_NGRAM_STOPWORDS_FILE = "ngramStopwordsFile";
	@ConfigurationParameter(name = PARAM_NGRAM_STOPWORDS_FILE, mandatory = false)
	protected String ngramStopwordsFile;

	public static final String PARAM_FILTER_PARTIAL_STOPWORD_MATCHES = "filterPartialStopwordMatches";
	@ConfigurationParameter(name = PARAM_FILTER_PARTIAL_STOPWORD_MATCHES, mandatory = true, defaultValue = "false")
	protected boolean filterPartialStopwordMatches;

	public static final String PARAM_NGRAM_FREQ_THRESHOLD = "ngramFreqThreshold";
	@ConfigurationParameter(name = PARAM_NGRAM_FREQ_THRESHOLD, mandatory = true, defaultValue = "0.0")
	protected float ngramFreqThreshold;

	public static final String PARAM_NGRAM_LOWER_CASE = "commentNgramLowerCase";
	@ConfigurationParameter(name = PARAM_NGRAM_LOWER_CASE, mandatory = true, defaultValue = "true")
	protected boolean ngramLowerCase;

	public static final String LUCENE_NGRAM_FIELD = "ngram";

	private Set<String> stopwords = new HashSet<>();
	protected FrequencyDistribution<String> topKSet;
	protected DfModel dfStore;
	protected String prefix;
	private ExtendedLogger logger;

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
		FrequencyDistribution<String> documentNgrams = null;

		documentNgrams = getCommentNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches, ngramMinN,
				ngramMaxN, stopwords);

		try {
			for (String topNgram : getTopNgrams().getKeys()) {
				if (documentNgrams.getKeys().contains(topNgram)) {
					features.add(new Feature(getFeaturePrefix()+"_"+uniqueName + "_" + topNgram, 1));
				} else {
					features.add(new Feature(getFeaturePrefix() +"_"+uniqueName + "_" + topNgram, 0, true));
				}
			}
		} catch (ResourceInitializationException e) {
			throw new TextClassificationException(e);
		}
		return features;
	}

	private FrequencyDistribution<String> getCommentNgrams(JCas jcas, Annotation focusAnnotation,
			boolean lowerCaseNGrams, boolean filterPartialMatches, int minN, int maxN, Set<String> stopwords) {
		FrequencyDistribution<String> annoNgrams = new FrequencyDistribution<String>();

		// If the focusAnnotation contains sentence annotations, extract the
		// ngrams sentence-wise
		// if not, extract them from all tokens in the focusAnnotation
		if (JCasUtil.selectCovered(jcas, CommentText.class, focusAnnotation).size() > 0) {
			for (CommentText s : selectCovered(jcas, CommentText.class, focusAnnotation)) {
				for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, s)), minN,
						maxN)) {

					if (lowerCaseNGrams) {
						ngram = lower(ngram);
					}

					if (passesNgramFilter(ngram, stopwords, filterPartialMatches)) {
						String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
						annoNgrams.inc(ngramString);
					}
				}
			}
		}
		// FIXME the focus annotation branch doesn't make much sense
		else {
			for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, focusAnnotation)),
					minN, maxN)) {

				if (lowerCaseNGrams) {
					ngram = lower(ngram);
				}

				if (passesNgramFilter(ngram, stopwords, filterPartialMatches)) {
					String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
					annoNgrams.inc(ngramString);
				}
			}
		}
		return annoNgrams;
	}

	public List<MetaCollectorConfiguration> getMetaCollectorClasses(Map<String, Object> parameterSettings)
			throws ResourceInitializationException {
		System.out.println(CommentNGramMetaCollector.PARAM_TARGET_LOCATION+" "+ LuceneNGram.PARAM_SOURCE_LOCATION+" "+ LuceneNGramMetaCollector.LUCENE_DIR);
		return Arrays.asList(new MetaCollectorConfiguration(CommentNGramMetaCollector.class, parameterSettings)
				.addStorageMapping(CommentNGramMetaCollector.PARAM_TARGET_LOCATION, LuceneNGram.PARAM_SOURCE_LOCATION,
						LuceneNGramMetaCollector.LUCENE_DIR));
	}

	protected void logSelectionProcess(long N) {
		getLogger().log(Level.INFO,
				"+++ SELECTING THE " + N + " MOST FREQUENT WORD [" + range() + "]-GRAMS (" + caseSensitivity() + ")");
	}

	private String range() {
		return ngramMinN == ngramMaxN ? ngramMinN + "" : ngramMinN + "-" + ngramMaxN;
	}

	private String caseSensitivity() {
		return ngramLowerCase ? "case-insensitive" : "case-sensitive";
	}

	protected String getFieldName() {
		return LUCENE_NGRAM_FIELD + featureExtractorName;
	}

	protected String getFeaturePrefix() {
		return "comment_ngram";
	}

	protected int getTopN() {
		return ngramUseTopK;
	}

	public static List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}

	public static boolean passesNgramFilter(List<String> tokenList, Set<String> stopwords,
			boolean filterPartialMatches) {
		List<String> filteredList = new ArrayList<String>();
		for (String ngram : tokenList) {
			if (!stopwords.contains(ngram)) {
				filteredList.add(ngram);
			}
		}

		if (filterPartialMatches) {
			return filteredList.size() == tokenList.size();
		} else {
			return filteredList.size() != 0;
		}
	}

	protected FrequencyDistribution<String> getTopNgrams() throws ResourceInitializationException {

		FrequencyDistribution<String> topNGrams = new FrequencyDistribution<String>();

		MinMaxPriorityQueue<TermFreqTuple> topN = MinMaxPriorityQueue.maximumSize(getTopN()).create();
		
		
		long ngramVocabularySize = 0;
		try (IndexReader reader =DirectoryReader.open(FSDirectory.open(luceneDir));) {
			Fields fields = MultiFields.getFields(reader);
			if (fields != null) {
				Terms terms = fields.terms(getFieldName());
				if (terms != null) {
					TermsEnum termsEnum = terms.iterator(null);
					BytesRef text = null;
					while ((text = termsEnum.next()) != null) {
						String term = text.utf8ToString();
						long freq = termsEnum.totalTermFreq();
						if (passesScreening(term)) {
							topN.add(new TermFreqTuple(term, freq));
							ngramVocabularySize += freq;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		getLogger().log(Level.INFO," select "+getTopN()+ " "+ topN.size());
		int size = topN.size();
		for (int i = 0; i < size; i++) {
			TermFreqTuple tuple = topN.poll();
			long absCount = tuple.getFreq();
			double relFrequency = ((double) absCount) / ngramVocabularySize;

			if (relFrequency >= ngramFreqThreshold) {
				topNGrams.addSample(tuple.getTerm(), tuple.getFreq());
			}
		}

		logSelectionProcess(topNGrams.getB());

		return topNGrams;
	}

	public ExtendedLogger getLogger() {
		if (logger == null) {
			logger = new ExtendedLogger(getUimaContext());
		}
		return logger;
	}
	
	protected boolean passesScreening(String term){
        return true;
    }
}
