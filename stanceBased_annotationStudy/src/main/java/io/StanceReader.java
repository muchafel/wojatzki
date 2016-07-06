package io;

import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import types.StanceAnnotation;

public class StanceReader extends BinaryCasReader{

    public static final String PARAM_TARGET_LABEL = "TargetLabel";
    @ConfigurationParameter(name = PARAM_TARGET_LABEL, mandatory = true)
	protected String targetLabel;

    
    @Override
	public void getNext(CAS cas) throws IOException, CollectionException {
		super.getNext(cas);
		
		JCas jcas;
		try {
			jcas = cas.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}
		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
	    try {
			outcome.setOutcome(getTextClassificationOutcome(jcas));
		} catch (Exception e) {
			e.printStackTrace();
		}
	    outcome.addToIndexes();
	}


	private String getTextClassificationOutcome(JCas jcas) throws Exception {
		if(targetLabel.equals("ATHEISM")){
			return JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity();
		}else if(targetLabel.equals("Original_Stance")){
			return JCasUtil.selectSingle(jcas, StanceAnnotation.class).getStance();
		}
		else {
			return getTextClassificationOutcome(jcas, targetLabel);
		}
	}


	private String getTextClassificationOutcome(JCas jcas, String targetLabel2) throws Exception {
		for(CuratedSubTarget subTarget: JCasUtil.select(jcas, CuratedSubTarget.class)){
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
