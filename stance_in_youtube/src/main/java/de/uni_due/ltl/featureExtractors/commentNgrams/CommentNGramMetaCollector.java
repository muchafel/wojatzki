package de.uni_due.ltl.featureExtractors.commentNgrams;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.ngram.meta.LuceneBasedMetaCollector;
import org.dkpro.tc.features.ngram.util.NGramUtils;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathFactory;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import edu.stanford.nlp.pipeline.Annotation;
import preprocessing.CommentText;

public class CommentNGramMetaCollector extends LuceneBasedMetaCollector {
	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, mandatory = true, defaultValue = "1")
	private int ngramMinN;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, mandatory = true, defaultValue = "3")
	private int ngramMaxN;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_STOPWORDS_FILE, mandatory = false)
	private String ngramStopwordsFile;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_FILTER_PARTIAL_STOPWORD_MATCHES, mandatory = true, defaultValue = "false")
	private boolean filterPartialStopwordMatches;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_LOWER_CASE, mandatory = false, defaultValue = "true")
	private String stringNgramLowerCase;

	boolean ngramLowerCase = true;

	private Set<String> stopwords;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		ngramLowerCase = Boolean.valueOf(stringNgramLowerCase);

		try {
			stopwords = FeatureUtil.getStopwords(ngramStopwordsFile, ngramLowerCase);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	protected FrequencyDistribution<String> getNgramsFD(JCas jcas) throws TextClassificationException {

		TextClassificationTarget target = new TextClassificationTarget(jcas, 0, jcas.getDocumentText().length());
		FrequencyDistribution<String> fd = null;
		fd = getDocumentNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches, ngramMinN,
				ngramMaxN, stopwords,Token.class);

		return fd;
	}

	@Override
	protected String getFieldName() {
		return LuceneNGram.LUCENE_NGRAM_FIELD + featureExtractorName;
	}
	public FrequencyDistribution<String> getDocumentNgrams(JCas jcas, TextClassificationTarget target,
	            boolean lowerCaseNGrams, boolean filterPartialMatches, int minN, int maxN,
	            Set<String> stopwords,Class<Token> annotationClass)
	                throws TextClassificationException
	    {
	        FrequencyDistribution<String> documentNgrams = new FrequencyDistribution<String>();
	        for (CommentText s : selectCovered(jcas, CommentText.class, target)) {
	            List<String> strings = valuesToText(jcas, s, annotationClass.getName());
	            for (List<String> ngram : new NGramStringListIterable(strings, minN, maxN)) {
	                if (lowerCaseNGrams) {
	                    ngram = lower(ngram);
	                }

	                String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
	                documentNgrams.inc(ngramString);
	            }
	        }
	        return documentNgrams;
	    }
	private List<String> lower(List<String> ngram)
    {
        List<String> newNgram = new ArrayList<String>();
        for (String token : ngram) {
            newNgram.add(token.toLowerCase());
        }
        return newNgram;
    }
	private <T extends Annotation> List<String> valuesToText(JCas jcas, CommentText s,
            String annotationClassName)
                throws TextClassificationException
    {
        List<String> texts = new ArrayList<String>();

        try {
            for (Entry<AnnotationFS, String> entry : FeaturePathFactory.select(jcas.getCas(),
                    annotationClassName)) {
                if (entry.getKey().getBegin() >= s.getBegin()
                        && entry.getKey().getEnd() <= s.getEnd()) {
                    texts.add(entry.getValue());
                }
            }
        }
        catch (FeaturePathException e) {
            throw new TextClassificationException(e);
        }
        return texts;
    }
}
