package de.uni_due.ltl.featureExtractors.subdebates;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.ltl.util.TargetSets;

public class ClassifiedSubdebateDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	protected ArrayList<String> explicitTargets_SET1 = TargetSets.targets_Set1;
	protected ArrayList<String> explicitTargets_SET2 = TargetSets.targets_Set2;

	protected Map<String, Map<String, Integer>> explcitTarget_SET1_2id2Outcome;
	protected Map<String, Map<String, Integer>> explcitTarget_SET2_2id2Outcome;

	public static final String PARAM_ID2OUTCOME_SUBTARGETS_FOLDER_PATH = "id2outcomeExplicitTargetsFolderPath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_SUBTARGETS_FOLDER_PATH, mandatory = true)
	protected String id2outcomeSubTargetFolderPath;
	
	public static final String PARAM_USE_SET1 = "useTargetSet1ForClassification";
	@ConfigurationParameter(name = PARAM_USE_SET1, mandatory = true)
	protected boolean useSet1;
	
	public static final String PARAM_USE_SET2 = "useTargetSet1ForClassification";
	@ConfigurationParameter(name = PARAM_USE_SET2, mandatory = true)
	protected boolean useSet2;
	
	public static final String PARAM_USE_ORACLE = "useOracleClassificationOfSubdebates";
	@ConfigurationParameter(name = PARAM_USE_ORACLE, mandatory = true)
	protected boolean oracle;
	
	public static final String PARAM_ORACLE_DROPOUT = "oracleDropout";
	@ConfigurationParameter(name = PARAM_ORACLE_DROPOUT, mandatory = true)
	protected int oracleDropout;
	
	public static final String PARAM_IDENTIFIER="ClassifiedSubdebateDFE_identifier";
	@ConfigurationParameter(name = PARAM_IDENTIFIER, mandatory = true)
	protected String identifier;
	
	Random random= new Random();

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
						Id2OutcomeUtil.getId2OutcomeMap(id2outcomeSubTargetFolderPath + "/set1/" + target + ".txt"));
			}
		}
		
		if(useSet2){
			explcitTarget_SET2_2id2Outcome = new HashMap<String, Map<String, Integer>>();
			for (String target : explicitTargets_SET2) {
				System.out.println(id2outcomeSubTargetFolderPath + "/set2/" + target);
				explcitTarget_SET2_2id2Outcome.put(target,
						Id2OutcomeUtil.getId2OutcomeMap(id2outcomeSubTargetFolderPath + "/set2/" + target + ".txt"));
			}
		}
		return true;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
//		System.out.println(unit.getCoveredText());
		if(useSet1){
			for (String target : explicitTargets_SET1) {
				try {
					int resultForTarget = getClassificationOutcome(unit, jcas, target,
							explcitTarget_SET1_2id2Outcome.get(target),true);
					featList.add(new Feature(identifier+"_ClassifiedSubTarget_"+target, resultForTarget));
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
					featList.add(new Feature(identifier+"_ClassifiedSubTarget_"+target, resultForTarget));
				} catch (Exception e) {
					throw new TextClassificationException(e);
				}
			}
		}
		return featList;
	}

	/**
	 * returns the predicted polarity value (from the id2outcome map) or the annotated oracle polarity
	 * @param unit
	 * @param jcas
	 * @param target
	 * @param debate_id2Outcome
	 * @param targetSet1
	 * @return
	 * @throws Exception
	 */
	private int getClassificationOutcome(TextClassificationTarget unit, JCas jcas, String target, Map<String, Integer> debate_id2Outcome, boolean targetSet1) throws Exception {
		if(oracle){
			return getOraclePolarity(unit,jcas,target,targetSet1);
		}else{
			String id2OutcomeKey=JCasUtil.selectSingle(jcas, JCasId.class).getId()+"_"+unit.getId();
			if(!debate_id2Outcome.containsKey(id2OutcomeKey)){
				throw new Exception(id2OutcomeKey+" not in id2OutcomeMap");
			}
			return debate_id2Outcome.get(id2OutcomeKey);
		}
	}
	
	/**
	 * method returns oracle polarity wrt to the target set
	 * @param unit
	 * @param jcas
	 * @param targetLabel
	 * @param targetSet1
	 * @return
	 * @throws Exception
	 */
	private int getOraclePolarity(TextClassificationTarget unit, JCas jcas, String targetLabel, boolean targetSet1) throws Exception {
		if(oracleDropout!=0){
			int chance=random.nextInt(100);
			if(targetSet1){
				return flipByChance(getOraclePolaritySet1(unit,jcas,targetLabel),chance);
			}else{
				return flipByChance(getOraclePolaritySet2(unit,jcas,targetLabel),chance);
			}
		}
		
		if(targetSet1){
			return getOraclePolaritySet1(unit,jcas,targetLabel);
		}else{
			return getOraclePolaritySet2(unit,jcas,targetLabel);
		}
	}

	private int flipByChance(int oraclePolaritySet1, int chance) {
		if(chance>(100-oracleDropout)){
			return 0;
		}else {
			return oraclePolaritySet1;
		}
	}

	/**
	 * get oracle polarity for target set1
	 * @param unit
	 * @param jcas
	 * @param targetLabel
	 * @return
	 * @throws Exception
	 */
	private int getOraclePolaritySet1(TextClassificationTarget unit, JCas jcas, String targetLabel) throws Exception {
		for(curated.Explicit_Stance_Set1 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,unit)){
			if(targetLabel.equals(subTarget.getTarget())){
//				System.out.println(subTarget.getTarget()+" "+subTarget.getPolarity());
				String polarity=subTarget.getPolarity();
				return Id2OutcomeUtil.resolvePolarityThreeway(polarity);
			}
		}
		return 0;
	}

	/**
	 * get oracle polarity for target set2
	 * @param unit
	 * @param jcas
	 * @param targetLabel
	 * @return
	 * @throws Exception
	 */
	private int getOraclePolaritySet2(TextClassificationTarget unit, JCas jcas, String targetLabel) throws Exception {
		for(curated.Explicit_Stance_Set2 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set2.class,unit)){
			if(targetLabel.equals(subTarget.getTarget())){
//				System.out.println(subTarget.getTarget()+" "+subTarget.getPolarity());
				String polarity=subTarget.getPolarity();
				return Id2OutcomeUtil.resolvePolarityThreeway(polarity);
			}
		}
		return 0;
	}

}
