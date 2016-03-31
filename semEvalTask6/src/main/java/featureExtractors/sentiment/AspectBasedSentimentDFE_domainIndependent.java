package featureExtractors.sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import featureExtractors.stanceLexicon.BinCasMetaDependent;
import types.Sentiment;
import util.CollocationMeasureHelper;

public class AspectBasedSentimentDFE_domainIndependent extends BinCasMetaDependent {

	private List<String> aspects;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			aspects = getAspectsFromTrainingData();
			System.out.println(aspects);
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private List<String> getAspectsFromTrainingData() throws CollectionException, UIMAException, IOException {
		List<String> aspects = new ArrayList<String>();

		FrequencyDistribution<String> stanceNouns = new FrequencyDistribution<String>();
		FrequencyDistribution<String> nonStanceNouns = new FrequencyDistribution<String>();

		CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		// iterate over all CASes that have been stored by the meta collector
		while (reader.hasNext()) {
			JCas jcas = JCasFactory.createJCas();
			reader.getNext(jcas.getCas());
			// get only nouns in the jcas
			Collection<Token> relevantTokens = getRelevantTokens(jcas, RelevantTokens.SENTENCE_NOUNS);
			String outcome = JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome();

			// add nouns to fds
			// TODO only Nouns? --> Bi-grams?
			if (outcome.equals("STANCE") || outcome.equals("AGAINST") || outcome.equals("FAVOR")) {
				stanceNouns.incAll(JCasUtil.toText(relevantTokens));
			} else {
				nonStanceNouns.incAll(JCasUtil.toText(relevantTokens));
			}
		}
		System.out.println(stanceNouns.getN());
		return getRelevantAspects(stanceNouns, nonStanceNouns);
	}

	private List<String> getRelevantAspects(FrequencyDistribution<String> stanceNouns,
			FrequencyDistribution<String> nonStanceNouns) {
		CollocationMeasureHelper helper = new CollocationMeasureHelper(stanceNouns, nonStanceNouns);
		List<String> relevantAspects = new ArrayList<String>();
		for (String cand : stanceNouns.getKeys()) {
			// TODO use only nouns with count >1 ???
			if (stanceNouns.getCount(cand)>1 && helper.getDiffOfGMeans(cand) > 0)
				relevantAspects.add(cand.toLowerCase());
		}
		return relevantAspects;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
//		System.out.println(jcas.getDocumentText());
		
		// relevant tokens for this tweet
		Collection<Token> relevantTokens = getRelevantTokens(jcas, RelevantTokens.SENTENCE_NOUNS);
		List<String> textArray = lowerTextArray(relevantTokens);
		
		float sentiment = 0;
		for (String aspect : aspects) {
			if (textArray.contains(aspect)) {
//				System.out.println(aspect+ " "+getDomainIndependentSentiment(aspect,relevantTokens,jcas));
				float aspectSentiment=
						getDomainIndependentSentiment(aspect, relevantTokens, jcas);
				features.add(new Feature("aspect_" + aspect + "_sentiment",aspectSentiment));
				sentiment+= aspectSentiment;
			} else {
				features.add(new Feature("aspect_" + aspect + "_sentiment", 0));
			}
		}
		features.add(new Feature("sentiment", sentiment));
		return features;
	}

	private List<String> lowerTextArray(Collection<Token> relevantTokens) {
		List<String> textArray = new ArrayList<String>();
		for(Token t: relevantTokens){
			textArray.add(t.getCoveredText().toLowerCase());
		}
//		System.out.println(textArray);
		return textArray;
	}

	/**
	 * calculates the polarity for each instance of the aspect in the jcas (for
	 * instance there may be two utterances of the same aspect in one tweet)
	 * 
	 * @param aspect
	 * @param relevantTokens
	 * @param jcas
	 * @return
	 */
	private float getDomainIndependentSentiment(String aspect, Collection<Token> relevantTokens, JCas jcas) {
		float result = 0;
		for (Token t : relevantTokens) {
			if (t.getCoveredText().equals(aspect)) {
				result += getDomainIndependentSentiment(t, jcas);
			}
		}
		return result;
	}

	/**
	 * calculates the polarity of an aspect according to surrounding words and words in the parsed context
	 * @param aspect
	 * @param jcas
	 * @return
	 */
	private float getDomainIndependentSentiment(Token aspect, JCas jcas) {
		float result = 0;
		for (Sentiment sentiment : JCasUtil.select(jcas, Sentiment.class)) {

			//sentiment from surrounding words
			result += getSentimentFromWordContext(sentiment, aspect, sentiment.getMpqaSentiment());
			result += getSentimentFromWordContext(sentiment, aspect, sentiment.getBingLiuSentiment());
			result += getSentimentFromWordContext(sentiment, aspect, sentiment.getNrcSentiment());

			//sentiment from parsing context
			result += getSentimentFromParsingContext(sentiment, aspect, sentiment.getMpqaSentiment(), jcas);
			result += getSentimentFromParsingContext(sentiment, aspect, sentiment.getBingLiuSentiment(), jcas);
			result += getSentimentFromParsingContext(sentiment, aspect, sentiment.getNrcSentiment(), jcas);

		}
		return result;
	}


	private float getSentimentFromWordContext(Sentiment sentiment, Token token, float sentimentValue) {
		int wordsBetween = JCasUtil.selectBetween(Token.class, sentiment, token).size();
		//too long context
		if(wordsBetween>3||wordsBetween==0)return 0;
		//TODO  use the words polarity itself?
//		return sentimentValue;
		
		//handling of negation
		boolean negation=false;
		for(Dependency d :JCasUtil.selectCovering(Dependency.class,sentiment)){
			if(d.getDependencyType().equals("neg")){
				negation=true;
				break;
			}
		}
		if(negation){
			return -(sentimentValue / (wordsBetween + 1));
		}else{
			return sentimentValue / (wordsBetween + 1);
		}
		
	}

	private float getSentimentFromParsingContext(Annotation sentiment, Token token, float sentimentValue, JCas jcas) {
//		System.out.println(jcas.getDocumentText());
//		System.out.println(sentiment.getCoveredText() + " " + token.getCoveredText());
		boolean negation=false;
		for(Dependency d :JCasUtil.selectCovering(Dependency.class,sentiment)){
			if(d.getDependencyType().equals("neg")){
				negation=true;
				break;
			}
		}
		for (Dependency dep : JCasUtil.select(jcas, Dependency.class)) {
			if (dep.getGovernor().equals(token) && dep.getDependent().equals(sentiment))
				if(negation){
					return sentimentValue * -0.5f;
				}else{
					return sentimentValue * 0.5f;
				}
				
			if (dep.getGovernor().equals(sentiment) && dep.getDependent().equals(token))
				if(negation){
					return sentimentValue * -0.5f;
				}else{
					return sentimentValue * 0.5f;
				}
			// TODO parsing path 2nd order, 3rd order...
		}
		return 0;
	}

}
