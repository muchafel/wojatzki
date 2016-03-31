package featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.NegationAnnotation;

public class RepeatedPunctuationDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> features= new HashSet<Feature>();
		double sentences = JCasUtil.select(jcas, Sentence.class).size();
        
		int numOfExclamationMarks=0;
		int numOfRepeatedExclamationMarks=0;
		int numOfRepeatedQuestionMarks=0;
		int numOfExclamationAndQuestionMarks=0;
		int numOfQuestionMarks=0;
		
		
		for(Token token : JCasUtil.select(jcas, Token.class)){
			if(token.getCoveredText().contains("?!")||token.getCoveredText().contains("!?"))numOfExclamationAndQuestionMarks++;
			if(token.getCoveredText().contains("!!"))numOfRepeatedExclamationMarks++;
			if(token.getCoveredText().contains("??"))numOfRepeatedQuestionMarks++;
			if(token.getCoveredText().equals("!"))numOfExclamationMarks++;
			if(token.getCoveredText().equals("?"))numOfQuestionMarks++;
		}
		
		features.add(new Feature("NUMBER_OF_EXCLAMATIONMARKS", numOfExclamationMarks/sentences));
		features.add(new Feature("NUMBER_OF_REPEATED_EXCLAMATIONMARKS", numOfRepeatedExclamationMarks/sentences));
		features.add(new Feature("NUMBER_OF_QUESTIONNMARKS", numOfQuestionMarks/sentences));
		features.add(new Feature("NUMBER_OF_REPEATED_QUESTIONNMARKS", numOfRepeatedQuestionMarks/sentences));
		features.add(new Feature("NUMBER_OF_EXCLAMATION_AND_QUESTIONNMARKS", numOfExclamationAndQuestionMarks/sentences));
		return features;
	}

}
