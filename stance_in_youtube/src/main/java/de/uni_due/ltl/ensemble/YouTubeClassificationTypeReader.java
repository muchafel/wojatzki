package de.uni_due.ltl.ensemble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.JCasId;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.uni_due.ltl.util.Id2OutcomeUtil;
import io.YouTubeReader;

public class YouTubeClassificationTypeReader extends YouTubeReader{
	
	public static final String PARAM_SVM_ID2OUTCOME_FILE_PATH = "svm_id2outcomeTargetFilePath";
	@ConfigurationParameter(name = PARAM_SVM_ID2OUTCOME_FILE_PATH, mandatory = true)
	private String svm_id2outcomeTargetFilePath;
	
	public static final String PARAM_LSTM_ID2OUTCOME_FILE_PATH = "lstm_id2outcomeTargetFilePath";
	@ConfigurationParameter(name = PARAM_LSTM_ID2OUTCOME_FILE_PATH, mandatory = true)
	private String lstm_id2outcomeTargetFilePath;
	
	private Map<String, Boolean> svm_id2Outcome;
	private Map<String, Boolean> lstm_id2Outcome;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		svm_id2Outcome= Id2OutcomeUtil.getId2Correct(svm_id2outcomeTargetFilePath);
		lstm_id2Outcome= Id2OutcomeUtil.getId2Correct(lstm_id2outcomeTargetFilePath);
		
		super.initialize(aContext);
	}


	@Override
	protected String getTextClassificationOutcome(JCas jcas, TextClassificationTarget unit) throws Exception {
		boolean correctBySVM=correct(jcas, unit,svm_id2Outcome);
		boolean correctByLSTM=correct(jcas, unit,lstm_id2Outcome);
		
		if(correctByLSTM && correctBySVM){
			return "NOPREF";
		}
		if(correctByLSTM){
			return "LSTM";
		}
		if(correctBySVM){
			return "SVM";
		}
		//TODO: treat true,true different than false,false?
		return "NOPREF";
	} 

	private boolean correct(JCas jcas, TextClassificationTarget unit, Map<String, Boolean> id2Outcome) throws Exception {
//		String id2OutcomeKey = JCasUtil.selectSingle(jcas, JCasId.class).getId() + "_" + unit.getId();
		String id2OutcomeKey = String.valueOf(unit.getId());
		if (!id2Outcome.containsKey(id2OutcomeKey)) {
			throw new Exception(id2OutcomeKey+" not in id2OutcomeMap");
		}
		return id2Outcome.get(id2OutcomeKey);
	}
}
