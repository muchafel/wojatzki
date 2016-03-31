package featureExtractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.util.FeatureUtil;
import types.TwitterSpecificPOS;

public class HashTagDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {

	
	private List<String> hashTags;
	
	public static final String PARAM_HASHTAGS_FILE_PATH = "simpleHashTagFilePath";
    @ConfigurationParameter(name = PARAM_HASHTAGS_FILE_PATH, mandatory = true, defaultValue="src/main/resources/lists/hashTags.txt")
    private String hashTagFilePath;
	
    
    public static final String PARAM_VARIANT = "hashTagVariant";
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false, defaultValue="default")
    private String variant;
	
   
    
	@Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        
        try {
			hashTags=FileUtils.readLines(new File(hashTagFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return true;
    }
	

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
//		System.out.println(jcas.getDocumentText());
		Set<Feature> features= new HashSet<Feature>();
		
		if(variant.equals("default"))features= getContainedHashtags(features,jcas,JCasUtil.select(jcas, TwitterSpecificPOS.class));
		else if(variant.equals("hashTagsAtTheEnd")) features= getHashTagsAtPostsEnd(features,jcas);
		
		return features;
	}


	private Set<Feature> getHashTagsAtPostsEnd(Set<Feature> features, JCas jcas) {
		
		List<TwitterSpecificPOS> candidateList= new ArrayList<TwitterSpecificPOS>();
		List<TwitterSpecificPOS> htList= new ArrayList<TwitterSpecificPOS>();
		htList.addAll(JCasUtil.select(jcas, TwitterSpecificPOS.class));
		//find all hastags at the end of an comment
		for(int j = htList.size() - 1; j >= 0; j--){
			 if(htList.get(j).getIsTokenTwitterSpecific()){
				 if(htList.get(j).getTag().equals("#"))candidateList.add(htList.get(j));
			 }else break;
		}
		
		return getContainedHashtags(features, jcas, candidateList);
	}


	private Set<Feature> getContainedHashtags(Set<Feature> features, JCas jcas, Collection<TwitterSpecificPOS> candidates) {
		for(String hashTag: hashTags){
			boolean hashTagContained=false;
			for(TwitterSpecificPOS anno: candidates){
				if(hashTag.equals(anno.getCoveredText().toLowerCase())){
//					System.out.println(hashTag);
					hashTagContained=true;
				}
			}
			if(hashTagContained)features.add(new Feature(hashTag.replace("#", "")+"_hashtag", 1));
			else features.add(new Feature(hashTag.replace("#", "")+"_hashtag", 0));
		}
		
		return features;
	}

}
