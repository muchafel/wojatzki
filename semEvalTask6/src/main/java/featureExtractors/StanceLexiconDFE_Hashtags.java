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
import featureExtractors.BinCasMetaDependent.RelevantTokens;

public class StanceLexiconDFE_Hashtags extends SummedStance_base{
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
			if (useHashtags) {
				hashTagStanceLexicon = readLexicon(binCasDir,RelevantTokens.HASHTAG);
			}
		} catch (IOException | UIMAException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {

		Set<Feature> features = new HashSet<Feature>();
		float hashTagPolarity = 0;
		int numberOfPositiveHashtags=0;
		int numberOfNegativeHashtags=0;
		
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			if (useStances) {
				float stance=hashTagStanceLexicon.getStance(token.getCoveredText());
				hashTagPolarity += stance;
				if(stance>0)numberOfPositiveHashtags++;
				else if(stance<0)numberOfNegativeHashtags++;
			}
		}
		
		features.add(new Feature("SummedHashTagPolarity", hashTagPolarity));
		features.add(new Feature("numberOfPositiveHashtags", numberOfPositiveHashtags));
		features.add(new Feature("numberOfNegativeHashtags", numberOfNegativeHashtags));
		return features;
	}

}
