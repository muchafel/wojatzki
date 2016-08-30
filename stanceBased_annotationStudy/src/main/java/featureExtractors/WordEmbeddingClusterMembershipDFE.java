package featureExtractors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.ant.IvyMakePom.Mapping;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;


public class WordEmbeddingClusterMembershipDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {


	public static final String NUMBER_OF_CLUSTERS = "numOfWordEmbeddingsClusters";
	@ConfigurationParameter(name = NUMBER_OF_CLUSTERS, mandatory = true)
	private int numberOfClusters;
	
	public static final String WORD_TO_CLUSTER_FILE = "wordToClusterFilePath";
	@ConfigurationParameter(name = WORD_TO_CLUSTER_FILE, mandatory = true)
	private String wordToClusterFile;
	
	private Map<String, String> wordToCluster;
	
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			wordToCluster=readMappingFromFile(wordToClusterFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	private Map<String, String> readMappingFromFile(String wordToClusterFile) throws IOException {
		Map<String, String> mapping= new HashMap<String, String>();
		File file = new File(wordToClusterFile);
		List<String> lines = FileUtils.readLines(file, "UTF-8");
		for(String line : lines){
			String[] lineParts= line.split(" > ");
			mapping.put(lineParts[0], lineParts[1]);
		}
		return mapping;
	}


	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
		Set<String> clusterIds= new HashSet<>();
		/**
		 * TODO treat OOV 
		 */
		for(Token t: JCasUtil.select(jcas, Token.class)){
			String tokenText= t.getCoveredText().toLowerCase();
			if(wordToCluster.containsKey(tokenText)){
				clusterIds.add(wordToCluster.get(tokenText));
//				System.out.println(tokenText+" contained in cluster"+ wordToCluster.get(tokenText));
			}
		}
//		System.out.println(jcas.getDocumentText());
//		System.out.println(clusterIds);
		for(int i=0; i<=numberOfClusters; i++){
			if(clusterIds.contains(String.valueOf(i))){
				features.add(new Feature("WordEmbeddingCluster_"+String.valueOf(i),1));
			}else{
				features.add(new Feature("WordEmbeddingCluster_"+String.valueOf(i),0));
			}
		}
		return features;
	}

}
