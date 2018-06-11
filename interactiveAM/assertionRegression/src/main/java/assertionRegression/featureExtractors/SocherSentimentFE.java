package assertionRegression.featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.sentiment.type.StanfordSentimentAnnotation;

public class SocherSentimentFE extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		
		double veryPositive = 0;
		double positive = 0;
		double neutral = 0;
		double negative = 0;
		double veryNegative = 0;
		
		for(StanfordSentimentAnnotation sentiment:JCasUtil.selectCovered(StanfordSentimentAnnotation.class,target)){
			veryPositive+=sentiment.getVeryPositive();
			positive+=sentiment.getPositive();
			neutral+=sentiment.getNeutral();
			negative+=sentiment.getNegative();
			veryNegative+=sentiment.getVeryNegative();
		}
		featList.add(new Feature("SOCHER_SENTIMENT_VERY_POSITIVE",veryPositive, FeatureType.NUMERIC));
		featList.add(new Feature("SOCHER_SENTIMENT_POSITIVE",positive, FeatureType.NUMERIC));
		featList.add(new Feature("SOCHER_SENTIMENT_NEUTRAL",neutral, FeatureType.NUMERIC));
		featList.add(new Feature("SOCHER_SENTIMENT_NEGATIVE",negative, FeatureType.NUMERIC));
		featList.add(new Feature("SOCHER_SENTIMENT_VERY_NEGATIVE",veryNegative, FeatureType.NUMERIC));
		
		return featList;
	}

}
