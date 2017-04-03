package de.uni_due.ltl.catalanStanceDetection.comparison;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.CohenKappaAgreement;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;

import de.uni_due.ltl.catalanStanceDetection.dl_Util.Id2OutcomeUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.io.TcId2OutcomeReader;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class CompareSVMandLSTM {

	public static void main(String[] args) throws Exception {
		File targetFileCA_LSTM= new File("src/main/resources/id2outcome/ca_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		File targetFileES_LSTM= new File("src/main/resources/id2outcome/es_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		File targetFileCA_SVM= new File("src/main/resources/id2outcome/ca_char_word_id2homogenizedOutcome.txt");
		File targetFileES_SVM= new File("src/main/resources/id2outcome/es_char_word_id2homogenizedOutcome.txt");
		
		Map<String,String> id2Outcome_LSTM= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/ca_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		Map<String,String> id2Outcome_SVM= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/id2outcome/ca_char_word_id2homogenizedOutcome.txt");
		Map<String,Boolean> correctSVM= Id2OutcomeUtil.getId2Correct("src/main/resources/id2outcome/es_char_word_id2homogenizedOutcome.txt",true);
		Map<String,Boolean> correctLSTM= Id2OutcomeUtil.getId2Correct("src/main/resources/id2outcome/es_activation_tanh_opimizeradam_lstmUnits_100result_dropout_0.3_epochs_numberEpochs5_id2Outcome.txt");
		id2Outcome_SVM= removeUnitArtifact(id2Outcome_SVM);
//		compare(id2Outcome_LSTM, id2Outcome_SVM);
		svm_or_lstm_map(correctSVM,correctLSTM);
	}

	private static Map<String, String> svm_or_lstm_map(Map<String, Boolean> correctSVM, Map<String, Boolean> correctLSTM) {
		Map<String,String> result= new HashMap<String, String>();
		int countLstm=0;
		for(String id: correctSVM.keySet()){
			System.out.println(correctSVM.get(id));
			System.out.println(correctLSTM.get(id));
			System.out.println(id);
			if(correctSVM.get(id) && correctLSTM.get(id) ){
				
				result.put(id, "SVM");
			}
			else if(correctSVM.get(id) && !correctLSTM.get(id) ){
				result.put(id, "SVM");
			}
			else if(!correctSVM.get(id) && correctLSTM.get(id) ){
				countLstm++;
				result.put(id, "LSTM");
			}else{
				result.put(id, "SVM");
			}
		}
		System.out.println(countLstm+ " "+ (correctSVM.keySet().size()-countLstm));
		return result;
		
	}

	private static Map<String, String> removeUnitArtifact(Map<String, String> id2Outcome_SVM) {
		Map<String,String> result= new HashMap<String, String>();
		for(String id: id2Outcome_SVM.keySet()){
			result.put(id.split("_")[0], id2Outcome_SVM.get(id));
		}
		
		return result;
	}

	private static void compare(Map<String,String> result_LSTM, Map<String,String>result_SVM) {
		int agree=0;
		int disagree=0;
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		for(String id: result_LSTM.keySet()){
//			System.out.println(result_LSTM.get(id)+" "+ result_SVM.get(id));
			study.addItem(result_LSTM.get(id), result_SVM.get(id));
			if(result_LSTM.get(id).equals(result_SVM.get(id))){
				agree++;
			}else{
				disagree++;
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		
		System.out.println("PA "+ pa.calculateAgreement());
		System.out.println("KAPPA "+ kappa.calculateAgreement());
		System.out.println("# agree: "+agree+ " #disagree: "+disagree);
	}

	
	

}
