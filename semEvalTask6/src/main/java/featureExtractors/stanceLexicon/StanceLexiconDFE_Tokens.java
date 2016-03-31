package featureExtractors.stanceLexicon;

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
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import featureExtractors.stanceLexicon.BinCasMetaDependent.RelevantTokens;

public class StanceLexiconDFE_Tokens extends SummedStance_base{
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			//FIXME remove stopwords from DFEs
			if (useStopwords) {
				stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
			}
			if (useStances) {
				wordStanceLexicon = readLexicon(binCasDir,RelevantTokens.SENTENCE);
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
		int numberOfPositiveTokens=0;
		int numberOfNegativeTokens=0;
		
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			if (useStances) {
				float stance= wordStanceLexicon.getStance(token.getCoveredText());
				tokenPolarity += stance;
				if(stance>0)numberOfPositiveTokens++;
				else if(stance<0)numberOfNegativeTokens++;
			}
		}
		
		features.add(new Feature("SummedTokenPolarity", tokenPolarity));
		features.add(new Feature("numberOfPositiveTokens", numberOfPositiveTokens));
		features.add(new Feature("numberOfNegativeTokens", numberOfNegativeTokens));
		return features;
	}

}
