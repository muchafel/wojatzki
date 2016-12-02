package io;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationTarget;

public class YouTubeSubDebateReader extends YouTubeReader {

	@Override
	protected String getTextClassificationOutcome_Set2(JCas jcas, TextClassificationTarget unit, String targetLabel2) throws Exception {
		for(curated.Explicit_Stance_Set2 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set2.class,unit)){
			if(targetLabel2.equals(subTarget.getTarget())){
				String polarity=subTarget.getPolarity();
				if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
					return "NONE";
				}
				return "PRESENT";
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
				return "PRESENT";
			}
		}
		throw new Exception("target Lable not annotated");
	}
}
