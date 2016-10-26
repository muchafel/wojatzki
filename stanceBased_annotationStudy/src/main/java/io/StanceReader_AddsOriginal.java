package io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationOutcome_Type;

import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import types.OriginalResource;
import types.StanceAnnotation;

public class StanceReader_AddsOriginal extends BinaryCasReader{

    public static final String PARAM_TARGET_LABEL = "TargetLabel";
    @ConfigurationParameter(name = PARAM_TARGET_LABEL, mandatory = true)
	protected String targetLabel;
    
    private Map<String,String> idToOriginalStance;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
    		throws ResourceInitializationException {

		idToOriginalStance = new HashMap<>();
		List<String> lines = null;
		try {
			lines = FileUtils
					.readLines(new File("/Users/michael/DKPRO_HOME/semevalTask6/tweetsTaskA/mapping/test.txt"));
			for (String line : lines) {
				idToOriginalStance.put(line.split("\t")[0] + ".xml", line.split("\t")[3]);
			}
			lines = FileUtils
					.readLines(new File("/Users/michael/DKPRO_HOME/semevalTask6/tweetsTaskA/mapping/train.txt"));
			for (String line : lines) {
				idToOriginalStance.put("tweets"+line.split("\t")[0] + ".xml", line.split("\t")[3]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(idToOriginalStance);

		return super.initialize(aSpecifier, aAdditionalParams);
    }
    
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
		StanceAnnotation anno = new StanceAnnotation(jcas,0, jcas.getDocumentText().length());
		anno.setTarget("ATHEISM");
		System.out.println(DocumentMetaData.get(jcas).getDocumentId()+" "+idToOriginalStance.get(DocumentMetaData.get(jcas).getDocumentId()));
		anno.setStance(idToOriginalStance.get(DocumentMetaData.get(jcas).getDocumentId()));
		anno.addToIndexes();
		
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
