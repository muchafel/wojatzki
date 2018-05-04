package hatespeechPrediction.FEs.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.features.meta.MetaCollectorConfiguration;
import org.dkpro.tc.api.features.meta.MetaDependent;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.features.ngram.WordNGram;
import org.dkpro.tc.features.ngram.meta.WordNGramMC;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class SimilarityPredictionFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor,MetaDependent{

	
	public static final String PARAM_N_MOST_SIMILAR = "nmostSimilar";
    @ConfigurationParameter(name = PARAM_N_MOST_SIMILAR, mandatory = true, defaultValue = "1")
    protected int topN;

    public static final String PARAM_USE_HS_SCORE= "useHsScore";
    @ConfigurationParameter(name = PARAM_USE_HS_SCORE, mandatory = true, defaultValue = "true")
    protected boolean useHSScore;
    
    
    public static final String PARAM_USE_GOLD= "useGoldSim";
    @ConfigurationParameter(name = PARAM_USE_GOLD, mandatory = true, defaultValue = "true")
    protected boolean useGold;
    
    public static final String PARAM_ID2OUTCOME_FILE= "id2OutcomeSimFile";
    @ConfigurationParameter(name = PARAM_ID2OUTCOME_FILE, mandatory = true)
    protected String path2Id2OutcomeFile;
    
    
    public static final String PARAM_PATH2_SCORES_FILE= "path2ScoresFile";
    @ConfigurationParameter(name = PARAM_PATH2_SCORES_FILE, mandatory = true)
    protected String path2scoresFile;
	
    private Map<String,Double> pair2Sim;
    private Map<String,Double> pair2Scores;
    
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
    		throws ResourceInitializationException {
    	super.initialize(aSpecifier, aAdditionalParams);
    	
    	pair2Sim= getMapping(path2Id2OutcomeFile,useGold);
//    	System.out.println(pair2Sim);
    	pair2Scores= getScoresMapping(path2scoresFile, useHSScore);
    	
    	System.out.println();
    	return true;
   
    }
    
    
	private Map<String, Double> getScoresMapping(String path, boolean useHS) {
		Map<String, Double> result= new HashMap<>();
		try {
			for(String line: FileUtils.readLines(new File(path), "UTF-8")) {
				double score;
				String[] parts= line.split("\t");
				if(useHS) {
					score=Double.valueOf(parts[1]);
				}else {
					score=Double.valueOf(parts[2]);
				}
				result.put(parts[0], score);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	private Map<String, Double> getMapping(String path2File, boolean useGold) {
		
		
		Map<String, Double> result= new HashMap<>();
		try {
			for(String line: FileUtils.readLines(new File(path2File), "UTF-8")) {
				line = line.replace("\\ ", " ");
				double similarity;
				String[] parts= line.split(";");
				if(useGold) {
					similarity=Double.valueOf(parts[0].split("=")[1]);
				}else {
					similarity=Double.valueOf(parts[1]);
				}
				result.put(parts[0].split("=")[0], similarity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget arg1) throws TextClassificationException {
		
		Set<Feature> features = new HashSet<Feature>();
		
		//create a mapping from most similar assertions to their HS scores resp. tehir AD scores (configured via PARAM_USE_HS_SCORE), the map is sorted according to similarity
		LinkedHashMap<String, Double> assertion2Scores= null;
		try {
			assertion2Scores = getMostSimilarAssertions2Score(topN,jcas.getDocumentText(),useHSScore,useGold);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//create a feature for the top n most similar assertions with their HS/AD value as value
		/**
		 * TODO check if sort order is right
		 */
		int i=0;
		double sum = 0;
		for (Entry<String, Double> entry : assertion2Scores.entrySet()) {
			if(i>topN) break;
		    
			String assertion = entry.getKey();
		    double score = entry.getValue();
		   
		    if(useHSScore) {
				features.add(new Feature("HS_SCORE_OF_"+String.valueOf(i), score,FeatureType.NUMERIC));
			}else {
				features.add(new Feature("AD_SCORE_OF_"+String.valueOf(i), score,FeatureType.NUMERIC));
			}
		    sum+=score;
		    i++;
		}
		  if(useHSScore) {
				features.add(new Feature("HS_AVERAGED", sum/(double)topN,FeatureType.NUMERIC));
			}else {
				features.add(new Feature("AD_AVERAGED", sum/(double)topN,FeatureType.NUMERIC));
			}
		
		
		return features;
	}

	private LinkedHashMap<String, Double> getMostSimilarAssertions2Score(int topN, String currentAssertion, boolean useHSScore, boolean useGold2) throws IOException {
		
		String baseDir = null;
		try {
			baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LinkedHashMap<String, Double> result= new LinkedHashMap<>();
		
		Map<String, Double> assertions2Similarity= new HashMap<>();
		
		//iterate over all assertions
		for(String line: FileUtils.readLines(new File(baseDir+"/hateSpeechGender/mapping.txt"),"UTF-8")) {
			assertions2Similarity.put(line, getSimilarity(currentAssertion,line));
		}
		
		//sort assertions according to similarity
		assertions2Similarity=sortByValue(assertions2Similarity);
//		System.out.println(assertions2Similarity);
		
		//get the n most similar assertions and store them with their scores in the map
		double i=0;
		
		for (Entry<String, Double> entry : assertions2Similarity.entrySet()) {
			if(i>topN) break;
			String assertion = entry.getKey();
		    double score = pair2Scores.get(assertion);
			result.put(assertion, score);
			i++;
		}
//		System.out.println(result);
		return result;
	}

	private double getSimilarity(String assertion1, String assertion2) throws IOException {
		
//		assertion1= assertion1.replace(".", " ");
//		assertion2= assertion2.replace(".", " ");
		if(pair2Sim.containsKey(assertion1+"_"+assertion2)) {
			return pair2Sim.get(assertion1+"_"+assertion2);
		}else if(pair2Sim.containsKey(assertion2+"_"+assertion1)) {
			return pair2Sim.get(assertion2+"_"+assertion1);
		}else {
			return 0.0;
//			throw new IOException(assertion1+"_"+assertion2+" not in mapping");
		}
	}


	private LinkedHashMap<String, Double> sortByValue(Map<String, Double> unsortedMap) {
		 List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortedMap.entrySet());

	        //   switch the o1 o2 position for a different order
	        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
	            public int compare(Map.Entry<String, Double> o1,
	                               Map.Entry<String, Double> o2) {
	                return (o2.getValue()).compareTo(o1.getValue());
	            }
	        });

	        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	        for (Map.Entry<String, Double> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }


	        return sortedMap;
	}


	@Override
	public List<MetaCollectorConfiguration> getMetaCollectorClasses(Map<String, Object> parameterSettings)
			throws ResourceInitializationException {
		return Arrays.asList(new MetaCollectorConfiguration(TextMetaCollector.class, parameterSettings));
	}

}
