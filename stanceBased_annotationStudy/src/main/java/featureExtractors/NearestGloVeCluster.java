package featureExtractors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

public class NearestGloVeCluster extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String PARAM_PRETRAINEDFILE = "GloVeFileFilePath";
	@ConfigurationParameter(name = PARAM_PRETRAINEDFILE, mandatory = true)
	private String gloveFilePath;
	
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		WordVectors wordVectors = null;
		try {
			 wordVectors = WordVectorSerializer.loadTxtVectors(new File(gloveFilePath));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(wordVectors.similarWordsInVocabTo("Hillary",0.2));
		return true;
		
	}
	
	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget target) throws TextClassificationException {
		// TODO Auto-generated method stub
		return null;
	}

}
