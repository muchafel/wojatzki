package de.uni_due.ltl.catalanStanceDetection.io;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase.Resource;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.uni_due.ltl.catalanStanceDetection.dl_Util.Id2OutcomeUtil;

public class CatalanStanceSVMorSVMTypeReader extends CatalanStanceReader{

	public static final String PARAM_SVM_PREDICTION_FILE = "PARAM_SVM_PREDICTION_FILE";
    @ConfigurationParameter(name = PARAM_SVM_PREDICTION_FILE, mandatory = true)
    private String svmPredictionFilePath;
    
    public static final String PARAM_LSTM_PREDICTION_FILE = "PARAM_LSTM_PREDICTION_FILE";
    @ConfigurationParameter(name = PARAM_LSTM_PREDICTION_FILE, mandatory = true)
    private String lstmPredictionFilePath;
	
    
    private Map<String,Boolean> correctSVM;
    private Map<String,Boolean> correctLSTM;
    
    
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		correctSVM= Id2OutcomeUtil.getId2Correct(svmPredictionFilePath,true);
		correctLSTM= Id2OutcomeUtil.getId2Correct(lstmPredictionFilePath);

	}
	
	 @Override
	protected TextClassificationOutcome getTextClassificationOutcome(JCas jcas, Sentence sentence, String docId)
			throws IOException {
		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas, sentence.getBegin(), sentence.getEnd());
		DocumentMetaData md = JCasUtil.selectSingle(jcas, DocumentMetaData.class);
		try {
			outcome.setOutcome(getTextClassificationOutcome(md.getDocumentId()));
		} catch (Exception e) {
			throw new IOException(e);
		}
		return outcome;
	}
	
	
	private String getTextClassificationOutcome(String docId) {
		if(correctSVM.get(docId) && correctLSTM.get(docId) ){
			return "UNK";
		}
		else if(correctSVM.get(docId) && !correctLSTM.get(docId) ){
			return "SVM";
		}
		else if(!correctSVM.get(docId) && correctLSTM.get(docId) ){
			return "LSTM";
		}else{
			return "UNK";
		}
	}
	
}
