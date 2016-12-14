package io;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

public class YouTubeSubDebateReader extends YouTubeReader {

	public static final String PARAM_MERGE_TO_BINARY = "MergeToBinary";
	@ConfigurationParameter(name = PARAM_MERGE_TO_BINARY, mandatory = true)
	protected boolean mergeToBInary;
	
	public static final String PARAM_EXCLUDE_NONE_DEBATE_STANCE = "ExlcludeNoneDebateStance";
	@ConfigurationParameter(name = PARAM_EXCLUDE_NONE_DEBATE_STANCE, mandatory = true)
	protected boolean exludeNoneDebateStances;

	
	@Override
	protected void addUnitAndOutComeToIndex(TextClassificationTarget unit, TextClassificationOutcome outcome) {
		if(exludeNoneDebateStances){
			//add unit only if debate stance != NONE
			if(!JCasUtil.selectCovered(curated.Debate_Stance.class,unit).get(0).getPolarity().equals("NONE")){
				super.addUnitAndOutComeToIndex(unit, outcome);
			}
		}else{
			super.addUnitAndOutComeToIndex(unit, outcome);
		}
	};
	
	@Override
	protected String getTextClassificationOutcome_Set2(JCas jcas, TextClassificationTarget unit, String targetLabel2) throws Exception {
		for(curated.Explicit_Stance_Set2 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set2.class,unit)){
			if(targetLabel2.equals(subTarget.getTarget())){
				String polarity=subTarget.getPolarity();
				if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
					return "NONE";
				}
				if(mergeToBInary){
					return "PRESENT";
				}
				return subTarget.getPolarity();
			}
		}
		throw new Exception("target Lable not annotated");
	}


	@Override
	protected String getTextClassificationOutcome_Set1(JCas jcas, TextClassificationTarget unit, String targetLabel) throws Exception {
		for(curated.Explicit_Stance_Set1 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,unit)){
			if(targetLabel.equals(subTarget.getTarget())){
				String polarity=subTarget.getPolarity();
				if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
					return "NONE";
				}
				if(mergeToBInary){
					return "PRESENT";
				}
				return subTarget.getPolarity();
			}
		}
		throw new Exception("target Lable not annotated");
	}
}
