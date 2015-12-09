package featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.Sentiment;

public class SimpleSentencePolarityDFE extends FeatureExtractorResource_ImplBase
implements DocumentFeatureExtractor{

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		
		Set<Feature> features = new HashSet<Feature>();
		
		float sentencePolarity = 0;
		int numberOfPositiveTokens=0;
		int numberOfNegativeTokens=0;
		
		for(Sentiment sentiment: JCasUtil.select(jcas, Sentiment.class)){
			float combinedSentiment= getCombinedSentiment(sentiment);
			sentencePolarity+=combinedSentiment;
			if(combinedSentiment<0)numberOfNegativeTokens++;
			if(combinedSentiment>0)numberOfPositiveTokens++;
		}
		features.add(new Feature("sentiment_sentencePolarity", sentencePolarity));
		features.add(new Feature("sentiment_numberOfPositiveToken", numberOfPositiveTokens));
		features.add(new Feature("sentiment_numberOfNegativeTokens", numberOfNegativeTokens));
		
		return features;
	}

	private float getCombinedSentiment(Sentiment sentiment) {
		return sentiment.getBingLiuSentiment()+sentiment.getMpqaSentiment()+sentiment.getNrcSentiment();
	}

}
