package de.uni_due.ltl.featureExtractors;

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

public class PredictedStance extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	public static final String PARAM_ID2OUTCOME_FOLDER_PATH = "id2outcomeTargetFolderPath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_FOLDER_PATH, mandatory = true)
	private String id2outcomeTargetFolderPath;
	
	private Map<String, Integer> debate_id2Outcome;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		debate_id2Outcome= Id2OutcomeUtil.getId2OutcomeMap(id2outcomeTargetFolderPath+"/debateStance"+"/id2homogenizedOutcome.txt");
		return true;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		int classifcationOutcome;
		try {
			classifcationOutcome = getPredictedOutcome(unit,jcas);
		} catch (Exception e) {
			throw new TextClassificationException(e);
		}
		featList.add(new Feature("STACKED_OUTCOME", classifcationOutcome));
		return featList;
	}

	private int getPredictedOutcome(TextClassificationTarget unit, JCas jcas) throws Exception {
		
		String id2OutcomeKey = JCasUtil.selectSingle(jcas, JCasId.class).getId() + "_" + unit.getId();
		if (!debate_id2Outcome.containsKey(id2OutcomeKey)) {
			throw new Exception(id2OutcomeKey+" not in id2OutcomeMap");
//			System.err.println(id2OutcomeKey + " not in id2OutcomeMap");
//			return 0;
			// throw new Exception(id2OutcomeKey+" not in id2OutcomeMap");
		}
		return debate_id2Outcome.get(id2OutcomeKey);
		
	}
	
}
