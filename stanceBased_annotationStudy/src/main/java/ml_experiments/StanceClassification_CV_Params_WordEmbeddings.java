package ml_experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.features.ngram.LuceneCharacterNGram;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import featureExtractors.BrownClusterMembershipDFE;
import featureExtractors.WordEmbeddingClusterMembershipDFE;
import featureExtractors.WordEmbeddingDFE;

public class StanceClassification_CV_Params_WordEmbeddings {
	public enum EmbeddingVariant {
		WORD2VEC_RAW,
		KMEANS_1000,
	    KMEANS_10000,
	    BROWNCLUSTERS,
	}
	public static boolean useUniformClassDistributionFilering = false; // for  filtering (be careful when using this)
	public static int WORD_N_GRAM_MIN = 1;
	public static int WORD_N_GRAM_MAX = 3;
	public static int CHAR_N_GRAM_MIN = 1;
	public static int CHAR_N_GRAM_MAX = 3;
	
	public static int N_GRAM_MAXCANDIDATES = 1000;
	
	
	/**
	 * XXX specify target here (TARGET_LABLE or Array)
	 */
	private static ArrayList<String> targets = new ArrayList<String>(Arrays.asList("ATHEISM","secularism",
			"Same-sex marriage", "religious_freedom", "Conservative_Movement", "Freethinking", "Islam",
			"No_evidence_for_religion", "USA", "Supernatural_Power_Being", "Life_after_death", "Christianity"));
	
	private static final String FilteringPostfix = "_wo_irony_understandability";
	
	public static void main(String[] args) throws Exception {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
		ArrayList<String> years = new ArrayList<String>(Arrays.asList(
//				"2015",
//				"2016",
				"2015_2016"
				));
		ArrayList<String> embeddingsDimensions = new ArrayList<String>(Arrays.asList(
				"75"
//				, 
//				"150"
				));
		for(String year: years){
			for(String dimension: embeddingsDimensions){
				for (EmbeddingVariant variant: EmbeddingVariant.values()) {
					for(String target : targets){
						 TcFeatureSet featureSet= getFeatureSet(variant,dimension,year,baseDir,target);
						 StanceClassification_CrossValidation experiment = new StanceClassification_CrossValidation();
						 ParameterSpace parameterSpace = experiment.setupCrossValidation(baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all" + FilteringPostfix, target, featureSet);
						 String experimentName = target.replace("-", "");
						 experimentName = target.replace(" ", "");
						 experimentName= experimentName.toLowerCase();
						 experiment.runCrossValidation(parameterSpace, "stanceExperiment_" + experimentName);
					}
				}
			}
		}
	}

	private static TcFeatureSet getFeatureSet(EmbeddingVariant variant, String dimension, String year,String basedir, String target) {
		TcFeatureSet featureSet=null; 
		String param="/"+year+"/"+dimension+"/";
		 if(variant.equals(EmbeddingVariant.WORD2VEC_RAW)){
			   String embeddingName=getEmbeddingName(dimension,target);
			   featureSet = new TcFeatureSet(
						TcFeatureFactory.create(WordEmbeddingDFE.class, WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION,basedir+"/semevalTask6/word2Vec_raw"+param+embeddingName),
						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
						);
		   }
//		if(variant.equals(EmbeddingVariant.KMEANS_1000)){
//			 featureSet = new TcFeatureSet(
//						TcFeatureFactory.create(WordEmbeddingClusterMembershipDFE.class, WordEmbeddingClusterMembershipDFE.WORD_TO_CLUSTER_FILE,"", WordEmbeddingClusterMembershipDFE.NUMBER_OF_CLUSTERS,1000),
//						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
//								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
//						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
//								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
//						);
//			   
//		   }
//		   if(variant.equals(EmbeddingVariant.KMEANS_10000)){
//			   featureSet = new TcFeatureSet(
//						TcFeatureFactory.create(WordEmbeddingClusterMembershipDFE.class, WordEmbeddingClusterMembershipDFE.WORD_TO_CLUSTER_FILE,"", WordEmbeddingClusterMembershipDFE.NUMBER_OF_CLUSTERS,1000),
//						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
//								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
//						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
//								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
//						);
//		   }
//		   if(variant.equals(EmbeddingVariant.BROWNCLUSTERS)){
//			   featureSet = new TcFeatureSet(
//						TcFeatureFactory.create(BrownClusterMembershipDFE.class, BrownClusterMembershipDFE.PARAM_BROWN_CLUSTERS_LOCATION,"")
//						,
//						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
//								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
//						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
//								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
//						);
//		   }
		 if(featureSet == null){
			 throw new IllegalArgumentException("no feature set mapped to your configuration "+variant+" ("+target+", "+dimension+", "+year+")");
		 }
		return featureSet;
	}

	private static String getEmbeddingName(String dimension, String target) {
		switch (target) {
        case "ATHEISM":
            return "atheism_word2Vec_"+dimension+".txt";
        case "Same-sex marriage":
        	return "same_sex_marriage_word2Vec_"+dimension+".txt";
        case "religious_freedom":
        	return "religious_freedom_word2Vec_"+dimension+".txt";
        case "Conservative_Movement":
        	return "conservative_movement_word2Vec_"+dimension+".txt";
        case "Freethinking":
        	return "freethinking_word2Vec_"+dimension+".txt";
        case "Islam":
        	return "islam_word2Vec_75"+dimension+".txt";
        case "No_evidence_for_religion":
        	return "no_evidence_for_religion_word2Vec"+dimension+".txt";
        case "USA":
        	return "usa_word2Vec_"+dimension+".txt";
        case "Supernatural_Power_Being":
        	return "supernatural_being_word2Vec_"+dimension+".txt";
        case "Life_after_death":
        	return "lifeafterdeath_word2Vec_"+dimension+".txt";
        case "Christianity":
        	return "christianity_word2Vec_"+dimension+".txt";
        default:
            throw new IllegalArgumentException("Invalid target "+target);
		}
	}

}
