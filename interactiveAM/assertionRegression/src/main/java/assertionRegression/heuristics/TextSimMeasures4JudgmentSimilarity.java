package assertionRegression.heuristics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import assertionRegression.io.AssertionSimilarityPairReader_deep;
import assertionRegression.judgmentPrediction.EmbeddingSimilarityMeasure;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;

public class TextSimMeasures4JudgmentSimilarity {
	public static void main(String[] args) throws ResourceInitializationException, IOException, SimilarityException {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
		ArrayList<TextSimilarityMeasure> measures = new ArrayList<TextSimilarityMeasure>(Arrays.asList(
//				new WordNGramJaccardMeasure(1),
//				new WordNGramJaccardMeasure(2),
//				new WordNGramJaccardMeasure(3),
//				new WordNGramJaccardMeasure(4)
//				, 
//				new EmbeddingSimilarityMeasure(baseDir + "/UCI/data/pruned/wiki.en.vec"),
//				new GreedyStringTiling(5),
				new GreedyStringTiling(4),
				new GreedyStringTiling(3),
				new GreedyStringTiling(2)
//				new LongestCommonSubstringComparator()
				));
		
//		TextSimilarityMeasure measure= new WordNGramJaccardMeasure();
		ArrayList<String> similarityMatrixes = new ArrayList<String>(Arrays.asList(
				"Climate Change"
				, 
				"Vegetarian & Vegan Lifestyle"
				,
				"Black Lives Matter"
				, 
				"Creationism in school curricula"
				, 
				"Foreign Aid"
				, 
				"Gender Equality"
				, 
				"Gun Rights"
				,
				"Legalization of Marijuana"
				, 
				"Legalization of Same-sex Marriage"
				, 
				"Mandatory Vaccination"
				, 
				"Media Bias"
				,
				"Obama Care -- Affordable Health Care Act"
				,
				"US Engagement in the Middle East"
				,
				"US Electoral System"
				,
				"US Immigration"
				, 
				"War on Terrorism"
				));
		
		


	
			
			
			for (TextSimilarityMeasure measure: measures) {
				System.out.println("new ME");
				for(String simil:similarityMatrixes) {
					List<Double> gold_array= new ArrayList();
					List<Double> predicted_array= new ArrayList();
//					List<Double> gold_array_all= new ArrayList();
//					List<Double> predicted_array_all= new ArrayList();
//					System.out.println(simil+ " ----");
					CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
			                AssertionSimilarityPairReader_deep.class, AssertionSimilarityPairReader_deep.PARAM_SOURCE_LOCATION,baseDir + "/UCI/similarityMatrices/" + simil + ".tsv");
					for (JCas jcas : new JCasIterable(reader)) {
						String[] text=jcas.getDocumentText().split(" \\$ ");
//						System.out.println(text[0]);
//						System.out.println(text[1]);
//						System.out.println(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
						double gold=Double.valueOf(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
						double sim = measure.getSimilarity(text[0].split(" "), text[1].split(" "));
						gold_array.add(gold);
						predicted_array.add(sim);
	//					gold_array_all.add(gold);
	//					predicted_array_all.add(sim);
	//					System.out.println(sim+"\t"+gold);
					}
					double[] gold=toPrimitive(gold_array);
					double[] predicted=toPrimitive(predicted_array);
					double corr = new PearsonsCorrelation().correlation(gold,predicted);
					double rounded = Math.round(corr*100.0)/100.0;
					System.out.println(rounded);
				}
//				System.out.println("----");
//				double[] gold_all=toPrimitive(gold_array_all);
//				double[] predicted_all=toPrimitive(predicted_array_all);
				
//				double corr_all = new PearsonsCorrelation().correlation(gold_all,predicted_all);
//				System.out.println(corr_all);
				
				

//				correlations.add(corr);
			}

		

	}
	private static double[] toPrimitive(List<Double> doubles) {
		double[] target = new double[doubles.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = doubles.get(i);               
		 }
		return target;
	}
}
