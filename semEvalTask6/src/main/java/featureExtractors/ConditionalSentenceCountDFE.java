package featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.NegationAnnotation;

public class ConditionalSentenceCountDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor{

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> features= new HashSet<Feature>();
		
		int numOfConds=0;
		for(Sentence sentence: JCasUtil.select(jcas, Sentence.class)){
			Token t=JCasUtil.selectCovered(Token.class,sentence).iterator().next();
			//more advanced detection of conditional sentences ??? --> assuming that...
			if(t.getCoveredText().toLowerCase().equals("if"))numOfConds++;
		}
		features.add(new Feature("NUMBER_OF_CONDITIONAL_SENTENCES", numOfConds));
		return features;
	}

}
