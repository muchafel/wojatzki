package de.uni_due.ltl.catalanStanceDetection.cv;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
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

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NgramCoverage extends LuceneFeatureExtractorBase implements FeatureExtractor {
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();

		int coberedUnigrams=getCoverage(NGramUtils.getAnnotationNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches,
				1, 1, stopwords));
		int coveredBigrams=getCoverage(NGramUtils.getAnnotationNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches,
				2, 2, stopwords));
		int coveredTrigrams=getCoverage(NGramUtils.getAnnotationNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches,
				3, 3, stopwords));
		
		int numberOfTokens=JCasUtil.selectCovered(Token.class, target).size();
		
		features.add(new Feature("UniGramCoverage", ((double)coberedUnigrams/(double)numberOfTokens)));
		features.add(new Feature("BiGramCoverage", ((double)coveredBigrams/(double)numberOfTokens)));
		features.add(new Feature("TriGramCoverage", ((double)coveredTrigrams/(double)numberOfTokens)));
		
		
		return features;
	}

	private int getCoverage(FrequencyDistribution<String> annotationNgrams) {
		int coveredNgrams=0;
		for(String ngram: annotationNgrams.getKeys()){
			if(topKSet.getKeys().contains(ngram)){
				coveredNgrams++;
			}
		}
		
		return coveredNgrams;
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
}
