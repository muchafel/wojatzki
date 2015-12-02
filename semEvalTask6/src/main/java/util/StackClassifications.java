package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.Id2Outcome;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.SingleOutcome;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.evaluator.EvaluatorBase;
import de.tudarmstadt.ukp.dkpro.tc.evaluation.evaluator.EvaluatorFactory;
import opennlp.tools.util.eval.FMeasure;

public class StackClassifications implements Constants {

	public static void main(String[] args)
			throws NumberFormatException, UnsupportedEncodingException, IOException, TextClassificationException {
//		String stanceVsNonePath="src/main/resources/evaluation/stanceVsNone/id2homogenizedOutcome_smo_lex.txt";
//		String stanceVsNonePath="src/main/resources/evaluation/stanceVsNone/id2homogenizedOutcome_smo_lex_normalized.txt";
//		String stanceVsNonePath="src/main/resources/evaluation/stanceVsNone/id2homogenizedOutcome_j48_lex_normalized.txt";
//		String stanceVsNonePath="src/main/resources/evaluation/stanceVsNone/id2homogenizedOutcome_smo_ngrams.txt";
		String stanceVsNonePath="src/main/resources/evaluation/stanceVsNone/id2homogenizedOutcome_j48_lex_normalized_alleFeatures.txt";
		
		
		
		String favorVsAgainstPath="src/main/resources/evaluation/favorVsAgainst/id2homogenizedOutcome_smo_lex.txt";
//		String favorVsAgainstPath="src/main/resources/evaluation/favorVsAgainst/id2homogenizedOutcome_j48_lex_normalized.txt";
//		String favorVsAgainstPath="src/main/resources/evaluation/favorVsAgainst/id2homogenizedOutcome_smo_ngrams.txt";
		
		
		Map<String, String> correct = readGold("src/main/resources/evaluation/gold/id2homogenizedOutcome.txt");
		Map<String, String> stanceVsNone = readPrediction(stanceVsNonePath);
		System.out.println("Stance Vs None Measures:");
		printEvaluationMeasures(new File(stanceVsNonePath));
		System.out.println("-----------");
		
		Map<String, String> favorVsAgainst = readPrediction(favorVsAgainstPath);
		System.out.println("Favor Vs Against Measures:");
		printEvaluationMeasures(new File(favorVsAgainstPath));
		System.out.println("-----------");
		Map<String, String> merged = merge(stanceVsNone, favorVsAgainst);

		File tempId2Outcome = createTempFile(merged, correct);

//		printFile(tempId2Outcome);
		System.out.println("merged:");
		printEvaluationMeasures(tempId2Outcome);
	}

	private static void printEvaluationMeasures(File tempId2Outcome) throws IOException, TextClassificationException {
		Id2Outcome id2Outcome = new Id2Outcome(tempId2Outcome, LM_SINGLE_LABEL);
		EvaluatorBase evaluator = EvaluatorFactory.createEvaluator(id2Outcome, true, false);
		Map<String, Double> resultTempMap = evaluator.calculateEvaluationMeasures();
		for (String key : resultTempMap.keySet()) {
			Double value = resultTempMap.get(key);
			System.out.println(key + " " + String.valueOf(value));
		}
	}

	private static void printFile(File tempId2Outcome) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(tempId2Outcome));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			}
		
	}

	private static File createTempFile(Map<String, String> merged, Map<String, String> correct) {
		File temp = null;
		try {
			temp = File.createTempFile("src/main/resources/evaluation/tempId2Outcome", ".tmp");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write("#ID=PREDICTION;GOLDSTANDARD;THRESHOLD");
			bw.newLine();
			bw.write("#labels 0=AGAINST 1=FAVOR 2=NONE");
			bw.newLine();
			bw.write("#Thu Nov 26 XX:XX:XX CET 2015");
			bw.newLine();
		
			for(String gold : correct.keySet()){
				String toWrite=gold+"="+toVector(getPredictionById(gold,merged))+toVector(correct.get(gold))+"-1.0";
				bw.write(toWrite);
				bw.newLine();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	private static String toVector(String lable) {
		if(lable.equals("AGAINST"))return "1,0,0;";
		else if(lable.equals("FAVOR"))return "0,1,0;";
		else if(lable.equals("NONE"))return "0,0,1;";
		System.err.println("UNKNOWN LABLE "+lable);
		return lable;
	}

	private static String getPredictionById(String gold, Map<String, String> merged) {
		for(String key: merged.keySet()){
			if(gold.equals(key))return merged.get(key);
		}
		System.err.println(gold+ "not found in merged");
		return null;
	}

	private static Map<String, String> merge(Map<String, String> stanceVsNone, Map<String, String> favorVsAgainst) {
		HashMap<String, String> merged = new HashMap<String, String>();
		for (String svn : stanceVsNone.keySet()) {
			if (stanceVsNone.get(svn).equals("NONE")){
				merged.put(svn, "NONE");
			}else{
				boolean foundInfavorVsAgainst=false; 
				for (String fvn : favorVsAgainst.keySet()) {
					if (fvn.equals(svn)) {
						merged.put(fvn, favorVsAgainst.get(fvn));
						foundInfavorVsAgainst=true;
					}
				}
				if(!foundInfavorVsAgainst){
					System.out.println("UNDECIDED");
//					merged.put(svn, "UNDECIDED");
				}
			}
				
			
		}
		return merged;
	}

	private static Map<String, String> readPrediction(String path)
			throws NumberFormatException, UnsupportedEncodingException, IOException {
		HashMap<String, String> prediction = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line = "";
		List<String> labelList = null;
		while ((line = br.readLine()) != null) {
			// this needs to happen at the beginning of the loop
			if (line.startsWith("#labels")) {
				labelList = getLabels(line);
			} else if (!line.startsWith("#")) {
				if (labelList == null) {
					br.close();
					throw new IOException("Wrong file format.");
				}
				// line might contain several '=', split at the last one
				int idxMostRightHandEqual = line.lastIndexOf("=");
				String evaluationData = line.substring(idxMostRightHandEqual + 1);
				String id = line.split("=")[0];
				String[] splittedEvaluationData = evaluationData.split(";");
				String[] predictionS = splittedEvaluationData[0].split(",");

				for (int i = 0; i < predictionS.length; i++) {
					if (predictionS[i].equals("1")) {
//						System.out.println("prediction " + id + " " + labelList.get(i));
						prediction.put(id, labelList.get(i));
					}
				}
			}
		}
		br.close();

		return prediction;
	}

	private static Map<String, String> readGold(String path)
			throws NumberFormatException, UnsupportedEncodingException, IOException {
		HashMap<String, String> gold = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line = "";
		List<String> labelList = null;
		while ((line = br.readLine()) != null) {
			// this needs to happen at the beginning of the loop
			if (line.startsWith("#labels")) {
				labelList = getLabels(line);
			} else if (!line.startsWith("#")) {
				if (labelList == null) {
					br.close();
					throw new IOException("Wrong file format.");
				}
				// line might contain several '=', split at the last one
				int idxMostRightHandEqual = line.lastIndexOf("=");
				String evaluationData = line.substring(idxMostRightHandEqual + 1);
				String id = line.split("=")[0];
				String[] splittedEvaluationData = evaluationData.split(";");
				String[] predictionS = splittedEvaluationData[0].split(",");
				String[] goldS = splittedEvaluationData[1].split(",");

				for (int i = 0; i < predictionS.length; i++) {
					if (goldS[i].equals("1")) {
						gold.put(id, labelList.get(i));
//						System.out.println("gold " + id + " " + labelList.get(i));
					}
				}
			}
		}
		br.close();

		return gold;
	}

	public static List<String> getLabels(String line) throws UnsupportedEncodingException {
		String[] numberedClasses = line.split(" ");
		List<String> labels = new ArrayList<String>();

		// filter #labels out and collect labels
		for (int i = 1; i < numberedClasses.length; i++) {
			// split one more time and take just the part with class name
			// e.g. 1=NPg, so take just right site
			String className = numberedClasses[i].split("=")[1];
			labels.add(URLDecoder.decode(className, "UTF-8"));
		}
		return labels;
	}

}
