package de.uni_due.ltl.featureExtractors.subdebates;

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
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.JCasId;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.uni_due.ltl.util.TargetSets;

public class ClassifiedSubdebateDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	private ArrayList<String> explicitTargets_SET1 = TargetSets.targets_Set1;
	private ArrayList<String> explicitTargets_SET2 = TargetSets.targets_Set2;

	private Map<String, Map<String, Integer>> explcitTarget_SET1_2id2Outcome;
	private Map<String, Map<String, Integer>> explcitTarget_SET2_2id2Outcome;

	public static final String PARAM_ID2OUTCOME_SUBTARGET_FOLDER_PATH = "id2outcomeExplicitTargetsFolderPath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_SUBTARGET_FOLDER_PATH, mandatory = true)
	private String id2outcomeSubTargetFolderPath;
	
	public static final String PARAM_USE_SET1 = "useTargetSet1ForClassification";
	@ConfigurationParameter(name = PARAM_USE_SET1, mandatory = true)
	private boolean useSet1;
	
	public static final String PARAM_USE_SET2 = "useTargetSet1ForClassification";
	@ConfigurationParameter(name = PARAM_USE_SET2, mandatory = true)
	private boolean useSet2;
	
	public static final String PARAM_USE_ORACLE = "useOracleClassificationOfSubdebates";
	@ConfigurationParameter(name = PARAM_USE_SET2, mandatory = true)
	private boolean oracle;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
		
		if(useSet1){
			explcitTarget_SET1_2id2Outcome = new HashMap<String, Map<String, Integer>>();
			for (String target : explicitTargets_SET1) {
				System.out.println(id2outcomeSubTargetFolderPath + "/set1/" + target);
				explcitTarget_SET1_2id2Outcome.put(target,
						getId2OutcomeMap(id2outcomeSubTargetFolderPath + "/set1/" + target + ".txt"));
			}
		}
		
		if(useSet2){
			explcitTarget_SET2_2id2Outcome = new HashMap<String, Map<String, Integer>>();
			for (String target : explicitTargets_SET2) {
				System.out.println(id2outcomeSubTargetFolderPath + "/set2/" + target);
				explcitTarget_SET2_2id2Outcome.put(target,
						getId2OutcomeMap(id2outcomeSubTargetFolderPath + "/set2/" + target + ".txt"));
			}
		}
		
		return true;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		if(useSet1){
			for (String target : explicitTargets_SET1) {
				try {
					int resultForTarget = getClassificationOutcome(unit, jcas, target,
							explcitTarget_SET1_2id2Outcome.get(target),true);
					featList.add(new Feature("ClassifiedSubTarget_"+target, resultForTarget));
				} catch (Exception e) {
					throw new TextClassificationException(e);
				}
			}
		}
		if(useSet2){
			for (String target : explicitTargets_SET2) {
				try {
					int resultForTarget = getClassificationOutcome(unit, jcas, target,
							explcitTarget_SET2_2id2Outcome.get(target),false);
					featList.add(new Feature("ClassifiedSubTarget_"+target, resultForTarget));
				} catch (Exception e) {
					throw new TextClassificationException(e);
				}
			}
		}
		return featList;
	}

	private int getClassificationOutcome(TextClassificationTarget unit, JCas jcas, String target, Map<String, Integer> debate_id2Outcome, boolean targetSet1) throws Exception {
		if(oracle){
			return getOraclePolarity(unit,jcas,target,targetSet1);
		}else{
			String id2OutcomeKey=JCasUtil.selectSingle(jcas, JCasId.class).getId()+"_"+unit.getId();
			if(!debate_id2Outcome.containsKey(id2OutcomeKey)){
				System.err.println(id2OutcomeKey+" not in id2OutcomeMap");
				return 0;
//				throw new Exception(id2OutcomeKey+" not in id2OutcomeMap");
			}
			return debate_id2Outcome.get(id2OutcomeKey);
		}
	}
	
	private int getOraclePolarity(TextClassificationTarget unit, JCas jcas, String targetLabel, boolean targetSet1) throws Exception {
		for(curated.Explicit_Stance_Set1 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,unit)){
			if(targetLabel.equals(subTarget.getTarget())){
				String polarity=subTarget.getPolarity();
				if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
					return 0;
				}
				return 1;
			}
		}
		throw new Exception("target Lable not annotated");
	}

	/**
	 * reads a map that stores the id2outcomes
	 * 
	 * @param path
	 * @return
	 * @throws ResourceInitializationException 
	 */
	private Map<String, Integer> getId2OutcomeMap(String path) throws ResourceInitializationException {
		Map<String, Integer> id2Outcome = new HashMap<String, Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					int outCome = 0;
					// handle predictions where there only two classes
					if (prediction.split("=")[1].length() == 3) {
						outCome = getOutcomeFromPrediction2classes(prediction.split("=")[1]);
					} else {
						outCome = getOutcomeFromPrediction3classes(prediction.split("=")[1]);
					}
					id2Outcome.put(id, outCome);
				}
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		return id2Outcome;
	}

	private int getOutcomeFromPrediction2classes(String outcome) {
		if (outcome.equals("1,0"))
			return 1;
		else if (outcome.equals("0,1"))
			return 2;
		else
			return 0;
	}

	private int getOutcomeFromPrediction3classes(String outcome) {
		if (outcome.equals("1,0,0"))
			return 1;
		else if (outcome.equals("0,1,0"))
			return 2;
		else
			return 0;
	}
}
