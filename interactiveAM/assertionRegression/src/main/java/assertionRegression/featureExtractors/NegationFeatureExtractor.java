package assertionRegression.featureExtractors;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * taken from https://www.grammarly.com/blog/negatives/
 * @author michael
 *
 */
public class NegationFeatureExtractor  extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	 //Negative words:
	 //no, none, nobody, nothing, neither, nowhere, never, noone
	 List<String> listOfNegativeWords = Arrays.asList("no", "none", "nobody","nothing", "neither", "nowhere", "never", "noone");
	 
	 //Negative Adverbs:
	 //hardly, scarcely, barely
	 List<String> listOfNegativeAdverbs = Arrays.asList("hardly", "scarcely", "barely");
	 
	 //Negative verbs
	 //doesn't, isn't, wasn't, shouldn't, wouldn't, couldn't, won't, can't, don't
	 List<String> listOfNegativeVerbs = Arrays.asList("doesn't", "isn't", "wasn't", "shouldn't", "wouldn't", "couldn't", "won't", "can't", "don't");
	
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Collection<Token> tokens=JCasUtil.select(jcas, Token.class);
		int countOfNegativeWords=0;
		int countOfNegativeVerbs=0;
		int countOfNegativeAdverbs=0;
		int countOfNot=0;
		
		for(Token t: tokens) {
			String tokenString=t.getCoveredText().toLowerCase();
			if(tokenString.equals("not")) {
				countOfNot++;
			}
			if(listOfNegativeWords.contains(tokenString)) {
				countOfNegativeWords++;
			}
			if(listOfNegativeAdverbs.contains(tokenString)) {
				countOfNegativeAdverbs++;
			}
			if(listOfNegativeVerbs.contains(tokenString)) {
				countOfNegativeVerbs++;
			}
		}
		
		Set<Feature> featList = new HashSet<Feature>();
		featList.add(new Feature("notRatio",(double)countOfNegativeWords/tokens.size()));
		featList.add(new Feature("negationWordsRatio",(double)countOfNot/tokens.size()));
		featList.add(new Feature("negativeAdverbsRatio",(double)countOfNegativeAdverbs/tokens.size()));
		featList.add(new Feature("negativeVerbs",(double)countOfNegativeVerbs/tokens.size()));
//		System.out.println((double)countOfNegativeWords/tokens.size()+ " "+(double)countOfNot/tokens.size()+" "+(double)countOfNegativeAdverbs/tokens.size());
		
		return featList;
	}

}
