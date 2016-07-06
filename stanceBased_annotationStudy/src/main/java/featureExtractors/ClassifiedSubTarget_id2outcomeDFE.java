package featureExtractors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * uses a generated id2outcome file to assign feature values
 * id2outcome results from a previously executed classification
 * @author michael
 *
 */
public class ClassifiedSubTarget_id2outcomeDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor{

	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList(
//			"secularism", "Same-sex marriage",
//			"religious_freedom", "Conservative_Movement", "Freethinking",
			"Islam", 
//			"No_evidence_for_religion", 
//			"USA",
			"Supernatural_Power_Being", 
//			"Life_after_death",
			"Christianity"));

	
	public static final String PARAM_ID2OUTCOME_SUBTARGET_FOLDER_PATH = "id2outcomeSubTargetsFilePath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_SUBTARGET_FOLDER_PATH, mandatory = true)
	private String id2outcomeSubTargetFolderPath;
	

	private Map<String,Map<String, Integer>> subtarget2id2Outcome_word_ngram;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		subtarget2id2Outcome_word_ngram= new HashMap<String,Map<String, Integer>>();
		for (String target : subTargets) {
		System.out.println(id2outcomeSubTargetFolderPath+"/"+target);
		subtarget2id2Outcome_word_ngram.put(target, getId2OutcomeMap(id2outcomeSubTargetFolderPath+"/"+target+".txt"));
		}
		return true;
	}
	
	/**
	 * reads a map taht stores the id2outcomes
	 * @param path
	 * @return
	 */
	private Map<String, Integer> getId2OutcomeMap(String path) {
		Map<String, Integer> id2Outcome= new HashMap<String,Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while (( line= br.readLine()) != null) {
				if(!line.startsWith("#")){
					String prediction=line.split(";")[0];
					String id=prediction.split("=")[0];
					int outCome=0;
					//handle predictions whre the split is only FAVOR NONE
					if(prediction.split("=")[1].length()==3){
						outCome=getOutcomeFromPrediction2classes(prediction.split("=")[1]);
					}
					else{
						outCome=getOutcomeFromPrediction3classes(prediction.split("=")[1]);
					}
					id2Outcome.put(id, outCome);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id2Outcome;
	}

	private int getOutcomeFromPrediction2classes(String outcome) {
//		System.out.println(outcome);
		if(outcome.equals("1,0"))return 1;
		else if(outcome.equals("0,1"))return 2;
		else return 0;
	}


	private int getOutcomeFromPrediction3classes(String outcome) {
//		System.out.println(outcome);
		if(outcome.equals("1,0,0"))return 1;
		else if(outcome.equals("0,1,0"))return 2;
		else return 0;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		String docId = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
		Set<Feature> featList = new HashSet<Feature>();
//		System.out.println(docId+ " "+ id2Outcome_word_ngram.get(docId)+" "+ id2Outcome_char_ngram.get(docId));
		for (String target : subTargets) {
			featList.add(new Feature("stacked_outcome_"+target,  subtarget2id2Outcome_word_ngram.get(target).get(docId)));
		}
		return featList;
	}

}
