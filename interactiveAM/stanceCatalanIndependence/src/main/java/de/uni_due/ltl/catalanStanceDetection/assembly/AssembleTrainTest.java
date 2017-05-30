package de.uni_due.ltl.catalanStanceDetection.assembly;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.resource.ResourceInitializationException;

import de.uni_due.ltl.catalanStanceDetection.dl_Util.Id2OutcomeUtil;

public class AssembleTrainTest {

	static String LANGUAGE_CODE = "es"
			+ ""
			+ ""
			+ ""
			+ "";

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		Map<String, String> id2Outcome_lstm = getId2OutcomeMap_String(
				"src/main/resources/result/" + LANGUAGE_CODE + "_lstm_prediction");
		Map<String, String> id2Outcome_svm = getId2OutcomeMap_String(
				"src/main/resources/result/" + LANGUAGE_CODE + "_svm_prediction");
		Map<String, String> id2Outcome_type = getId2OutcomeMap_String(
				"src/main/resources/" + LANGUAGE_CODE + "_typePred.txt");

		ressolveTypes(id2Outcome_lstm, id2Outcome_svm, id2Outcome_type);

	}

	private static void ressolveTypes(Map<String, String> id2Outcome_lstm, Map<String, String> id2Outcome_svm,
			Map<String, String> id2Outcome_type) throws IOException {
		for(String id: id2Outcome_type.keySet()){
			String outcome=ressolvePrediction(id2Outcome_type.get(id), id2Outcome_lstm.get(id), id2Outcome_svm.get(id));
			String result= id+","+outcome+","+"DUMMY";
			FileUtils.write(new File("src/main/resources/result/"+LANGUAGE_CODE+"_hybrid_prediction"), result+System.lineSeparator(), "UTF-8", true);
		}

	}

	private static Map<String, String> getId2OutcomeMap_String(String string) throws IOException {
		Map<String, String> result = new HashMap<>();

		for (String line : FileUtils.readLines(new File(string))) {
			result.put(line.split(",")[0], line.split(",")[1]);
		}

		return result;
	}
	
	private static String ressolvePrediction(String typePrediction, String lstmPrediction, String svmPredcition) {
		if (typePrediction.equals("LSTM")  ) {
			return lstmPrediction;
		} else if (typePrediction.equals("SVM")) {
			return svmPredcition;
		}else if(typePrediction.equals("UNK")){
			return svmPredcition;
		}
		else{
			return svmPredcition;
		}
	}

}
