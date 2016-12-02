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
		debate_id2Outcome = getId2OutcomeMap(
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
				e.printStackTrace();
			}
			featList.add(new Feature("PREVIOUS_STANCE", previousPolarity));
		}else{
			//in case the is no previous element
			featList.add(new Feature("PREVIOUS_STANCE", resolvePolarity("NONE")));
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

	/**
	 * reads a map that stores the id2outcomes
	 * 
	 * @param path
	 * @return
	 */
	private Map<String, Integer> getId2OutcomeMap(String path) {
		Map<String, Integer> id2Outcome = new HashMap<String, Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					int outCome = 0;
					// handle predictions whre the split is only FAVOR NONE
					if (prediction.split("=")[1].length() == 3) {
						outCome = getOutcomeFromPrediction2classes(prediction.split("=")[1]);
					} else {
						outCome = getOutcomeFromPrediction3classes(prediction.split("=")[1]);
					}
					id2Outcome.put(id, outCome);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id2Outcome;
	}

	private int getClassificationOutcome(TextClassificationTarget unit, JCas jcas) throws Exception {
		if (useOracle) {
			return resolvePolarity(
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

	private int getOutcomeFromPrediction2classes(String outcome) {
		// System.out.println(outcome);
		if (outcome.equals("1,0"))
			return 1;
		else if (outcome.equals("0,1"))
			return -1;
		else
			return 0;
	}

	private int getOutcomeFromPrediction3classes(String outcome) {
		// System.out.println(outcome);
		if (outcome.equals("1,0,0"))
			return 1;
		else if (outcome.equals("0,1,0"))
			return -1;
		else
			return 0;
	}

	private int resolvePolarity(String polarity) {
		if (polarity.equals("FAVOR")) {
			return 1;
		} else if (polarity.equals("AGAINST")) {
			return -1;
		} else
			return 0;
	}
}
