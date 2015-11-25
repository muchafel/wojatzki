package featureExtractors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;

public class SummedStanceDFE extends SummedStance_base {


	
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			//TODO parammeterize stopwords
			if(useStopwords){
				stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
			}
			
			if (useStances) {
				wordStanceLexicon = readLexicon(binCasDir,RelevantTokens.ALL);
			}
		} catch (IOException | UIMAException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {

		Set<Feature> features = new HashSet<Feature>();
		float tokenPolarity = 0;

		for (Token token : JCasUtil.select(jcas, Token.class)) {
			if (useStances) {
				tokenPolarity += wordStanceLexicon.getStance(token.getCoveredText());
			}

		}

		features.add(new Feature("SummedTokenPolarity", tokenPolarity));
		return features;
	}

}
