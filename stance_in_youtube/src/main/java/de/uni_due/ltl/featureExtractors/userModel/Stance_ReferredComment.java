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
import preprocessing.CommentType;

public class Stance_ReferredComment extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	private Map<String, Integer> debate_id2Outcome;

	public static final String PARAM_USE_ORACLE = "useOracleReferredComment";
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
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		CommentType users=JCasUtil.selectCovered(jcas, CommentType.class,unit).iterator().next();
		if(users.getCommentNotReply()){
			featList.add(new Feature("STANCE_REFERRED_",0)); 
		}else{
			TextClassificationTarget referredComment=getReferredComment(jcas,unit);
			int referredPolarity = 0;
			try {
				referredPolarity = getClassificationOutcome(referredComment, jcas);
			} catch (Exception e) {
				e.printStackTrace();
			}
			featList.add(new Feature("STANCE_REFERRED_",referredPolarity));
		}
		return featList;
	}

	private TextClassificationTarget getReferredComment(JCas jcas, TextClassificationTarget unit_current) {
		TextClassificationTarget previousUnit = null;
		for (TextClassificationTarget unit : JCasUtil.select(jcas, TextClassificationTarget.class)) {
			if (unit.getAddress() == unit_current.getAddress()) {
				return previousUnit;
			}
			if(isComment(jcas,unit)){
				previousUnit = unit;
			}
		}
		return null;
	}

	

	private boolean isComment(JCas jcas, TextClassificationTarget unit) {
		CommentType users=JCasUtil.selectCovered(jcas, CommentType.class,unit).iterator().next();
		if(users.getCommentNotReply()){
			return true;
		}else{
			return false;
		}
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
