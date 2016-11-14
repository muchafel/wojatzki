package de.uni_due.ltl.simpleClassifications;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

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
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.meta.MetaCollectorConfiguration;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.LuceneFeatureExtractorBase;
import org.dkpro.tc.features.ngram.meta.LuceneNGramMetaCollector;
import org.dkpro.tc.features.ngram.util.NGramUtils;
import org.dkpro.tc.features.ngram.util.TermFreqTuple;

import com.google.common.collect.MinMaxPriorityQueue;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import preprocessing.CommentText;

public class CommentNGram extends LuceneFeatureExtractorBase implements FeatureExtractor {

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
		FrequencyDistribution<String> documentNgrams = null;

		System.out.println(target.getCoveredText());
		documentNgrams = getCommentNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches, ngramMinN,
				ngramMaxN, stopwords);
		

		// documentNgrams = NGramUtils.getAnnotationNgrams(jcas, target,
		// ngramLowerCase,filterPartialStopwordMatches, ngramMinN, ngramMaxN,
		// stopwords);

		try {
			for (String topNgram : getTopNgrams().getKeys()) {
				if (documentNgrams.getKeys().contains(topNgram)) {
					features.add(new Feature(getFeaturePrefix() + "_" + topNgram, 1));
				} else {
					features.add(new Feature(getFeaturePrefix() + "_" + topNgram, 0, true));
				}
			}
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
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

	@Override
	public List<MetaCollectorConfiguration> getMetaCollectorClasses(Map<String, Object> parameterSettings)
			throws ResourceInitializationException {
		return Arrays.asList(new MetaCollectorConfiguration(LuceneNGramMetaCollector.class, parameterSettings)
				.addStorageMapping(LuceneNGramMetaCollector.PARAM_TARGET_LOCATION, LuceneNGram.PARAM_SOURCE_LOCATION,
						LuceneNGramMetaCollector.LUCENE_DIR));
	}

	@Override
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

	@Override
	protected String getFieldName() {
		return LUCENE_NGRAM_FIELD + featureExtractorName;
	}

	@Override
	protected String getFeaturePrefix() {
		return "ngram";
	}

	@Override
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

		throw new ResourceInitializationException("implement this method uing a metaclolector (stance lexicon like)", requiredTypes);

//		return topNGrams;
	}
}
