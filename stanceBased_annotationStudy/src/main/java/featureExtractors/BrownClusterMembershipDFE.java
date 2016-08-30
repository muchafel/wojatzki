package featureExtractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

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

public class BrownClusterMembershipDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	private static final String NOT_SET = "*";

	public static final String PARAM_BROWN_CLUSTERS_LOCATION = "brownClusterLocations";
	@ConfigurationParameter(name = PARAM_BROWN_CLUSTERS_LOCATION, mandatory = true)
	private File inputFile;

//	public static final String NUMBER_OF_CLUSTERS = "numOfBrownClusters";
//	@ConfigurationParameter(name = NUMBER_OF_CLUSTERS, mandatory = true)
//	private int numberOfClusters;
	
	private HashMap<String, String> wordToCluster = null;
	private HashSet<String> distinctClusters=null;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		try {
			init();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}

	public Set<Feature> extract(JCas jcas, TextClassificationTarget aClassificationUnit)
			throws TextClassificationException {

		Set<Feature> features = new HashSet<Feature>();
		Set<String> clusterIds= new HashSet<>();
		/**
		 * TODO treat OOV 
		 */
		for(Token t: JCasUtil.select(jcas, Token.class)){
			String tokenText= t.getCoveredText().toLowerCase();
			if(wordToCluster.containsKey(tokenText)){
				clusterIds.add(wordToCluster.get(tokenText));
//				System.out.println(tokenText+" contained in cluster "+ wordToCluster.get(tokenText));
			}
		}
//		System.out.println(jcas.getDocumentText());
//		System.out.println(clusterIds);
		for(String cluster: distinctClusters){
			if(clusterIds.contains(cluster)){
				features.add(new Feature("BrownCluster_"+cluster,1));
			}else{
				features.add(new Feature("BrownCluster_"+cluster,0));
			}
		}
		return features;
	}

	private void init() throws TextClassificationException {

		if (wordToCluster != null) {
			return;
		}
		wordToCluster = new HashMap<String, String>();
		distinctClusters= new HashSet<>();

		try {

			BufferedReader bf = openFile();
			String line = null;
			while ((line = bf.readLine()) != null) {
				String[] split = line.split("\t");
				String clusterId=split[0].substring(0, split[0].length()-1);
				wordToCluster.put(split[1], clusterId);
				distinctClusters.add(clusterId);
//				wordToCluster.put(split[1], split[0]);
//				distinctClusters.add(split[0]);
			}

		} catch (Exception e) {
			throw new TextClassificationException(e);
		}
	}

	private BufferedReader openFile() throws Exception {
		InputStreamReader isr = null;
		if (inputFile.getAbsolutePath().endsWith(".gz")) {

			isr = new InputStreamReader(new GZIPInputStream(new FileInputStream(inputFile)), "UTF-8");
		} else {
			isr = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
		}
		return new BufferedReader(isr);
	}
}
