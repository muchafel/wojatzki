package featureExtractors.stanceLexicon;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.features.twitter.EmoticonRatioDFE;
import types.HashTagStancePolarity;
import types.WordStancePolarity;

public class SummedStanceDFE_staticLexicon extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor{

	
	public static final String PARAM_USE_STANCE_LEXICON = "useStanceLexicon_static";
    @ConfigurationParameter(name = PARAM_USE_STANCE_LEXICON, mandatory = true, defaultValue="true")
    private boolean useStances;
	
	public static final String PARAM_USE_HASHTAG_LEXICON = "useHashTagLexicon_static";
    @ConfigurationParameter(name = PARAM_USE_HASHTAG_LEXICON, mandatory = true, defaultValue="true")
    private boolean useHashtags;
	
	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		
		Set<Feature> features = new HashSet<Feature>();
		float tokenPolarity= 0;
		float hashTagPolarity= 0;
//		System.out.println(jcas.getDocumentText());
		if(useStances){
			for(WordStancePolarity polarity: JCasUtil.select(jcas, WordStancePolarity.class)){
				tokenPolarity+=polarity.getPolarity();
			}
		}
		
		if(useHashtags){
			for(HashTagStancePolarity polarity: JCasUtil.select(jcas, HashTagStancePolarity.class)){
				hashTagPolarity+=polarity.getPolarity();
			}
		}
		
		features.add(new Feature("SummedTokenPolarity", tokenPolarity));
		features.add(new Feature("SummedHashTagPolarity", hashTagPolarity));
//		System.out.println(sentencePolarity);
		return features;
	}

}
