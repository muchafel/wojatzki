package de.uni_due.ltl.catalanStanceDetection.comparison;

import java.io.File;
import java.util.TreeMap;

import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.io.TcId2OutcomeReader;
import de.unidue.ltl.evaluation.io.TextReader;
import de.unidue.ltl.evaluation.measure.categorial.Accuracy;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;

public class InspectGridSearch {

	public static void main(String[] args) throws Exception {
//		File folder= new File("/Users/michael/git/ucsm_git/iberStance_lstm/result/cv");
//		File folder= new File("/Users/michael/git/ucsm_git/iberStance_lstm/result/cv3");
		File folder= new File("/Users/michael/git/ucsm_git/iberStance_lstm/result/cv_lr");
//		File folder= new File("/Users/michael/git/ucsm_git/iberStance_lstm/result/cv_wiki");
//		File folder= new File("/Users/michael/Desktop/cv_dropOut");
//		File folder= new File("/Users/michael/Desktop/cv");
		
		double max= 0;
		String name= null;
		String lang="ca";
		
		TreeMap<Double, String> resultMap= new TreeMap<>();
		
		for(File file: folder.listFiles()){
			if(!file.getName().contains("id2Outcome") && (file.getName().startsWith(lang) || file.getName().startsWith("CONV_"+lang))&& !file.getName().endsWith("_id2Prob.txt")){
				System.out.println(file.getName());
				double result= evaluate(file);
				resultMap.put(result, file.getName());
				if(result>max){
					max= result;
					name= file.getName();
				}
			}
		}
		System.out.println("--------------");
		System.out.println("best config "+name+ " "+max);
		System.out.println("# "+resultMap.size());
		for(Double key: resultMap.keySet()){
			System.out.println(key+ " "+resultMap.get(key));
		}

	}

	private static double evaluate(File targetFileCA_LSTM) throws Exception {
		EvaluationData<String> evaluationData= TextReader.read(targetFileCA_LSTM);
		Fscore<String> fscore= new Fscore<>(evaluationData);
		Accuracy<String> acc= new Accuracy<>(evaluationData);
		
		System.out.println("Accuracy "+ acc.getAccuracy());
		System.out.println("MACRO_F1 "+fscore.getMacroFscore());
		System.out.println("MICRO_F1 "+fscore.getMicroFscore());
//		System.out.println("F1 (FAVOR) "+fscore.getScoreForLabel("FAVOR"));
//		System.out.println("F1 (AGAINST) "+fscore.getScoreForLabel("AGAINST"));
//		System.out.println("F1 (NEUTRAL) "+fscore.getScoreForLabel("NEUTRAL"));
		ConfusionMatrix<String> matrix = new ConfusionMatrix<>(evaluationData);

        System.out.println(matrix.toString());
        System.out.println("  ");
        return fscore.getMicroFscore();
		
	}
	
}
