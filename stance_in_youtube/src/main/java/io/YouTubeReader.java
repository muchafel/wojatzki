package io;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationSequence;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;

public class YouTubeReader extends BinaryCasReader{
	 public static final String PARAM_TARGET_LABEL = "TargetLabel";
	    @ConfigurationParameter(name = PARAM_TARGET_LABEL, mandatory = true)
		protected String targetLabel;
	 
	    public static final String PARAM_TARGET_SET = "TargetSet";
	    @ConfigurationParameter(name = PARAM_TARGET_SET, mandatory = true)
		protected String targetSet;

	    private int tcId = 0;
	    
	    @Override
		public void getNext(CAS cas) throws IOException, CollectionException {
			super.getNext(cas);
			
			JCas jcas;
			try {
				jcas = cas.getJCas();
			} catch (CASException e) {
				throw new CollectionException(e);
			}
			
			//make sure there is no outcome artifact
			Collection<TextClassificationOutcome> outcomes= JCasUtil.select(jcas,TextClassificationOutcome.class);
			if(!outcomes.isEmpty()){
				for(TextClassificationOutcome outcome:outcomes){
//					System.out.println("oucome detected");
					outcome.removeFromIndexes();
				}
			}
			Collection<TextClassificationTarget> targets= JCasUtil.select(jcas,TextClassificationTarget.class);
			if(!targets.isEmpty()){
				for(TextClassificationTarget target:targets){
//					System.out.println("target detected");
					target.removeFromIndexes();
				}
			}
			
					
//			TextClassificationSequence sequence = new TextClassificationSequence(jcas,0, jcas.getDocumentText().length());
//            sequence.addToIndexes();
			 for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
		            
		          TextClassificationTarget unit = new TextClassificationTarget(jcas, sentence.getBegin(),
		        		  sentence.getEnd());
		                unit.setId(tcId++);
		                unit.addToIndexes();

		                TextClassificationOutcome outcome = new TextClassificationOutcome(jcas,sentence.getBegin(), sentence.getEnd());
		                try {
							outcome.setOutcome(getTextClassificationOutcome(jcas, unit));
						} catch (Exception e) {
							throw new IOException(e);
						}
		                outcome.addToIndexes();

		        }
			
//			if(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class) != null){
//				outcome = JCasUtil.selectSingle(jcas, TextClassificationOutcome.class);
//			}else{
//				 outcome = new TextClassificationOutcome(jcas);
//			}
//			
//		    try {
//				outcome.setOutcome(getTextClassificationOutcome(jcas));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		    outcome.addToIndexes();
		}


		private String getTextClassificationOutcome(JCas jcas, TextClassificationTarget unit) throws Exception {
			if(targetLabel.equals("DEATH PENALTY")){
				return JCasUtil.selectCovered(jcas, curated.Debate_Stance.class,unit).get(0).getPolarity();
			}
			else {
				if(targetSet.equals("1")){
					return getTextClassificationOutcome_Set1(jcas,unit, targetLabel);
				}else if(targetSet.equals("2")){
					return getTextClassificationOutcome_Set2(jcas,unit, targetLabel);
				}else{
					throw new Exception("Lable set for explicit stances "+targetSet+ " not known!");
				}
			}
		}


		protected String getTextClassificationOutcome_Set2(JCas jcas, TextClassificationTarget unit, String targetLabel2) throws Exception {
			for(curated.Explicit_Stance_Set2 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set2.class,unit)){
				if(targetLabel2.equals(subTarget.getTarget())){
					String polarity=subTarget.getPolarity();
					if(!polarity.equals("FAVOR") && !polarity.equals("AGAINST")){
						return "NONE";
					}
					System.out.println(polarity);
					return polarity;
				}
			}
			throw new Exception("target Lable not annotated");
		}


		protected String getTextClassificationOutcome_Set1(JCas jcas, TextClassificationTarget unit, String targetLabel) throws Exception {
			for(curated.Explicit_Stance_Set1 subTarget: JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,unit)){
				if(targetLabel.equals(subTarget.getTarget())){
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
