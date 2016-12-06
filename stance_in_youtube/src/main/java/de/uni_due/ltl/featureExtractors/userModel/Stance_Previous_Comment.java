package de.uni_due.ltl.featureExtractors.userModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

import de.uni_due.ltl.util.Id2OutcomeUtil;
import preprocessing.Users;

public class Stance_Previous_Comment extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	private Map<String, Integer> debate_id2Outcome;

	public static final String PARAM_USE_ORACLE = "useOraclePrevious";
	@ConfigurationParameter(name = PARAM_USE_ORACLE, mandatory = true, defaultValue="false")
	private boolean useOracle;
	
	public static final String PARAM_ID2OUTCOME_FOLDER_PATH = "id2outcomeTargetFolderPath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_FOLDER_PATH, mandatory = true)
	private String id2outcomeTargetFolderPath;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		debate_id2Outcome = Id2OutcomeUtil.getId2OutcomeMap(
				id2outcomeTargetFolderPath + "/debateStance" + "/id2homogenizedOutcome.txt");
		// subtarget2id2Outcome_word_ngram= new HashMap<String,Map<String,
		// Integer>>();
		// for (String target : subTargets) {
		// System.out.println(id2outcomeSubTargetFolderPath+"/"+target);
		// subtarget2id2Outcome_word_ngram.put(target,
		// getId2OutcomeMap(id2outcomeSubTargetFolderPath+"/"+target+".txt"));
		// }
		return true;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit_current) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		TextClassificationTarget unit_before = getUnitBefore(jcas, unit_current);
		if (unit_before != null) {
			int previousPolarity = 0;
			try {
				previousPolarity = getClassificationOutcome(unit_before, jcas);
			} catch (Exception e) {
				throw new TextClassificationException(e);
			}
			featList.add(new Feature("PREVIOUS_STANCE", previousPolarity));
		}else{
			//in case there is no previous element
			try {
				featList.add(new Feature("PREVIOUS_STANCE", Id2OutcomeUtil.resolvePolarityThreeway("NONE")));
			} catch (Exception e) {
				throw new TextClassificationException(e);
			}
		}
		return featList;
	}

	/**
	 * returns the previous unit in case it is the first Unit null is returned
	 * 
	 * @param jcas
	 * @param unit_current
	 * @return
	 */
	private TextClassificationTarget getUnitBefore(JCas jcas, TextClassificationTarget unit_current) {
		TextClassificationTarget previousUnit = null;
		for (TextClassificationTarget unit : JCasUtil.select(jcas, TextClassificationTarget.class)) {
			if (unit.getAddress() == unit_current.getAddress()) {
				return previousUnit;
			}
			previousUnit = unit;
		}
		return null;
	}


	private int getClassificationOutcome(TextClassificationTarget unit, JCas jcas) throws Exception {
		if (useOracle) {
			return Id2OutcomeUtil.resolvePolarityThreeway(
					JCasUtil.selectCovered(jcas, curated.Debate_Stance.class, unit).get(0).getPolarity());
		} else {
			String id2OutcomeKey = JCasUtil.selectSingle(jcas, JCasId.class).getId() + "_" + unit.getId();
			if (!debate_id2Outcome.containsKey(id2OutcomeKey)) {
				System.err.println(id2OutcomeKey + " not in id2OutcomeMap");
				return 0;
				// throw new Exception(id2OutcomeKey+" not in id2OutcomeMap");
			}
			return debate_id2Outcome.get(id2OutcomeKey);
		}
	}
}
