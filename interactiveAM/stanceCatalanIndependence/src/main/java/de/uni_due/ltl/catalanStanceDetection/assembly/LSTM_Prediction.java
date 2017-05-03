package de.uni_due.ltl.catalanStanceDetection.assembly;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.resource.ResourceInitializationException;

import de.uni_due.ltl.catalanStanceDetection.dl_Util.Id2OutcomeUtil;

public class LSTM_Prediction {
	
	static String LANGUAGE_CODE = "ca";
	
	public static void main(String[] args) throws IOException, ResourceInitializationException {
		File input= new File("src/main/resources/"+LANGUAGE_CODE+"_activation_tanh_dropOut_0.2_sparse5_units_64_id2Outcome.txt");
		Map<String,String> id2Outcome= Id2OutcomeUtil.getId2OutcomeMap_String("src/main/resources/"+LANGUAGE_CODE+"_activation_tanh_dropOut_0.2_sparse5_units_64_id2Outcome.txt");
//		for(String line : FileUtils.readLines(input)){
//			String id= line.split("=")[0];
//			Id2OutcomeUtil.getId2OutcomeMap_String(path)
//		}
		for(String id: id2Outcome.keySet()){
			System.out.println(id+ " "+id2Outcome.get(id));
			String result= id+","+id2Outcome.get(id)+","+"DUMMY";
			FileUtils.write(new File("src/main/resources/result/"+LANGUAGE_CODE+"_lstm_prediction"), result+System.lineSeparator(), "UTF-8", true);
		}
	}
}
