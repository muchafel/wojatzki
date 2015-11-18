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

public class SummedStanceDFE_functionalParts extends SummedStance_base {

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			// TODO parammeterize stopwords
			if (useStopwords) {
				stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
			}

			if (useStances) {
				wordStanceLexicon = readLexicon(wordStanceDir,RelevantTokens.SENTENCE);
			}

			if (useHashtags) {
				hashTagStanceLexicon = readLexicon(wordStanceDir,RelevantTokens.HASHTAG);
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
		float hashTagPolarity = 0;
		
//		System.out.println(jcas.getDocumentText());
		
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			if (useStances) {
				tokenPolarity += wordStanceLexicon.getStance(token.getCoveredText());
//				System.out.println("TOKEN "+token.getCoveredText()+" "+wordStanceLexicon.getStance(token.getCoveredText()));
				hashTagPolarity += hashTagStanceLexicon.getStance(token.getCoveredText());
			}

		}
//		System.out.println("token polarity "+tokenPolarity);
//		System.out.println("# polarity "+hashTagPolarity);
		
		features.add(new Feature("SummedTokenPolarity_partitioned", tokenPolarity));
		features.add(new Feature("SummedHashTagPolarity_partitioned", hashTagPolarity));
		return features;
	}

}
