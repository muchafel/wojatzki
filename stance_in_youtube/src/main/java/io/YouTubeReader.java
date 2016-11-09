package io;

import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;

public class YouTubeReader extends BinaryCasReader{
	 public static final String PARAM_TARGET_LABEL = "TargetLabel";
	    @ConfigurationParameter(name = PARAM_TARGET_LABEL, mandatory = true)
		protected String targetLabel;
	 
	    public static final String PARAM_TARGET_SET = "TargetSet";
	    @ConfigurationParameter(name = PARAM_TARGET_LABEL, mandatory = true)
		protected String targetSet;

	    
	    @Override
		public void getNext(CAS cas) throws IOException, CollectionException {
			super.getNext(cas);
			
			JCas jcas;
			try {
				jcas = cas.getJCas();
			} catch (CASException e) {
				throw new CollectionException(e);
			}
			TextClassificationOutcome outcome = null;
			if(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class) != null){
				outcome = JCasUtil.selectSingle(jcas, TextClassificationOutcome.class);
			}else{
				 outcome = new TextClassificationOutcome(jcas);
			}
			
		    try {
				outcome.setOutcome(getTextClassificationOutcome(jcas));
			} catch (Exception e) {
				e.printStackTrace();
			}
		    outcome.addToIndexes();
		}


		private String getTextClassificationOutcome(JCas jcas) throws Exception {
			if(targetLabel.equals("DEATH PENALTY")){
				return JCasUtil.selectSingle(jcas, curated.Debate_Stance.class).getPolarity();
			}
			else {
				if(targetSet.equals("1")){
					return getTextClassificationOutcome_Set1(jcas, targetLabel);
				}else if(targetSet.equals("2")){
					return getTextClassificationOutcome_Set2(jcas, targetLabel);
				}else{
					throw new Exception("Labele set for explicit stances "+targetSet+ " not known!");
				}
			}
		}


		private String getTextClassificationOutcome_Set2(JCas jcas, String targetLabel2) throws Exception {
			for(curated.Explicit_Stance_Set2 subTarget: JCasUtil.select(jcas, curated.Explicit_Stance_Set2.class)){
				if(targetLabel2.equals(subTarget.getTarget())){
					String polarity=subTarget.getPolarity();
					if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
						return "NONE";
					}
					return polarity;
				}
			}
			throw new Exception("target Lable not annotated");
		}


		private String getTextClassificationOutcome_Set1(JCas jcas, String targetLabel2) throws Exception {
			for(curated.Explicit_Stance_Set1 subTarget: JCasUtil.select(jcas, curated.Explicit_Stance_Set1.class)){
				if(targetLabel2.equals(subTarget.getTarget())){
					String polarity=subTarget.getPolarity();
					if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
						return "NONE";
					}
					return polarity;
				}
			}
			throw new Exception("target Lable not annotated");
		}
}
