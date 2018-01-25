package assertionRegression.heuristics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bytedeco.javacpp.RealSense.context;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import com.ibm.icu.impl.locale.XCldrStub.FileUtilities;

import assertionRegression.annotationTypes.Issue;
import assertionRegression.io.AssertionReader;
import assertionRegression.svetlanasTask.AssertionJudgmentSimilarityPredictor_learnedSim;
import assertionRegression.svetlanasTask.Predictor;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class PredictAggregatedAgreement_SimGold {
	public static void main(String[] args) throws NumberFormatException, Exception {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(AssertionReader.class,
				AssertionReader.PARAM_SOURCE_LOCATION, baseDir+"/UCI/data/data.tsv", AssertionReader.PARAM_LANGUAGE, "en",
				AssertionReader.PARAM_TARGETCLASS, "Agreement");
		
		
		//"US Electoral System"
		ArrayList<String> issues = new ArrayList<String>(Arrays.asList("Climate Change", "Vegetarian & Vegan Lifestyle",
				"Black Lives Matter", "Creationism in school curricula", "Foreign Aid", "Gender Equality", "Gun Rights",
				"Legalization of Marijuana", "Legalization of Same-sex Marriage", "Mandatory Vaccination", "Media Bias",
				"Obama Care -- Affordable Health Care Act", "US Engagement in the Middle East",
				"US Immigration", "War on Terrorism"));

//		String issueToTest="Climate Change";
//		String issueToTest="Vegetarian & Vegan Lifestyle";
//		String issueToTest="Black Lives Matter";
//		String issueToTest="Creationism in school curricula";
//		String issueToTest="Foreign Aid";
//		String issueToTest="Gender Equality";
//		String issueToTest="Gun Rights";
//		String issueToTest="Legalization of Marijuana";
//		String issueToTest="Legalization of Same-sex Marriage";
//		String issueToTest="Mandatory Vaccination";
//		String issueToTest="Media Bias";
//		String issueToTest="Obama Care -- Affordable Health Care Act";
//		String issueToTest="US Electoral System";
//		String issueToTest="US Engagement in the Middle East";
//		String issueToTest="US Immigration";
//		String issueToTest="War on Terrorism";
		
		
	
		for (int j=1; j<=51;j+=1) { 
			List<Double> all_correlations= new ArrayList<>();
			List<Double> all_gold_array= new ArrayList<>();
			List<Double> all_predicted_array= new ArrayList<>();
			for(String issueToTest: issues) {
				
				
				LinkedHashMap<String, Double> aggregatedAssertions= new LinkedHashMap<>();
				int i=0;

				
				for (String line : FileUtils.readLines(new File(baseDir + "/UCI/data/data.tsv"))) {
					String issue = line.split("\t")[7];
					if (issue.equals(issueToTest)) {
						aggregatedAssertions.put(line.split("\t")[1], Double.valueOf(line.split("\t")[2]));
					}
				}
				
				AggregatedJudgmentSimilarityPredictor_Sim mostSimilarAssertionPredictor_learned= new AggregatedJudgmentSimilarityPredictor_Sim("src/main/resources/rawMatrices/"+issueToTest+".tsv",new ArrayList<String>(aggregatedAssertions.keySet()),issueToTest);
				
				List<Double> correlations= new ArrayList<>();
//				for (int j=1; j<=12;j+=1) { 
					List<Double> gold_array= new ArrayList<>();
					List<Double> predicted_array= new ArrayList<>();
					int stopper=1;
					for (String assertion : aggregatedAssertions.keySet()) {
						
						
						LinkedHashMap<String, Double> nonZeroJudgments_toTest = getAssertionsToTest(aggregatedAssertions,assertion);
						// System.out.println(Double.valueOf(line.split("\t")[2]) + "\t" +
						// mostSimilarAssertionPredictor_learned
						// .predictionOfMostSimilarAssertion(nonZeroJudgments_toTest, assertion));
						gold_array.add(aggregatedAssertions.get(assertion));
						all_gold_array.add(aggregatedAssertions.get(assertion));
						// predicted_array.add(mostSimilarAssertionPredictor_learned.predictionOfMostSimilarAssertion(nonZeroJudgments_toTest,
						// assertion));
//						System.out.println(aggregatedAssertions.get(assertion) + "\t" + mostSimilarAssertionPredictor_learned
//								.predictionOfMostSimilarAssertions(nonZeroJudgments_toTest, assertion, j));
						predicted_array.add(mostSimilarAssertionPredictor_learned.predictionOfMostSimilarAssertions(nonZeroJudgments_toTest, assertion, j));
						all_predicted_array.add(mostSimilarAssertionPredictor_learned.predictionOfMostSimilarAssertions(nonZeroJudgments_toTest, assertion, j));
						
					}
					
					double[] gold=toPrimitive(gold_array);
					
					double[] predicted=toPrimitive(predicted_array);
					double corr = new PearsonsCorrelation().correlation(gold,predicted);
					correlations.add(corr);
//				    System.out.println(issueToTest+"\t"+j+"\t"+corr);
//				}
//				System.out.println(issueToTest+ " "+StringUtils.join(correlations,"\t"));
			}
			
			double[] gold=toPrimitive(all_gold_array);
			
			double[] predicted=toPrimitive(all_predicted_array);
			double corr = new PearsonsCorrelation().correlation(gold,predicted);
		    System.out.println(j+"\t"+corr);
		}
		
		

		
		
		
		
		
		
	}

	private static double[] toPrimitive(List<Double> doubles) {
		double[] target = new double[doubles.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = doubles.get(i);               
		 }
		return target;
	}

	private static LinkedHashMap<String, Double> getAssertionsToTest(LinkedHashMap<String, Double> aggregatedAssertions,
			String assertion) {
		LinkedHashMap<String, Double> result= new LinkedHashMap<>();
		for(String key: aggregatedAssertions.keySet()) {
			if(!key.equals(assertion)) {
				result.put(key, aggregatedAssertions.get(key));
			}
		}
		return result;
	}
}
