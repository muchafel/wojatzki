package assertionRegression.wordembeddings;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class EmbeddingCoverage extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String PARAM_WORDEMBEDDINGLOCATION = "embeddingsLocation";
	@ConfigurationParameter(name = PARAM_WORDEMBEDDINGLOCATION, mandatory = true)
	private String embeddingsLocation;
	
	private WordEmbeddingLexicon lexicon;

	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		System.out.println("use embedding "+embeddingsLocation);
			try {
				lexicon = new WordEmbeddingLexicon(embeddingsLocation);
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		return true;
	}
	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		List<Token> tokens= JCasUtil.selectCovered(Token.class, target);
		int contained=0;
		for(Token t: tokens){
			if(lexicon.getLexicon().containsKey(t.getCoveredText().toLowerCase())){
				contained++;
			}
		}
		double coverageNormalized=(double)contained/(double)tokens.size();
		featList.add(new Feature("embeddingCoverage", coverageNormalized));
		return featList;
	}

}
