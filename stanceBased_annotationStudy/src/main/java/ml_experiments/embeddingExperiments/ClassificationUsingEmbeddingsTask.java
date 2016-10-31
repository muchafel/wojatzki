package ml_experiments.embeddingExperiments;

import java.io.File;
import java.io.IOException;

import org.dkpro.lab.engine.TaskContext;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.lab.task.Discriminator;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.lab.task.impl.ExecutableTaskBase;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.features.ngram.LuceneCharacterNGram;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import featureExtractors.BrownClusterMembershipDFE;
import featureExtractors.WordEmbeddingClusterMembershipDFE;
import featureExtractors.WordEmbeddingDFE;
import ml_experiments.StanceClassification_CrossValidation;
import ml_experiments.StanceClassification_CV_Params_WordEmbeddings.EmbeddingVariant;

public class ClassificationUsingEmbeddingsTask extends ExecutableTaskBase{

	public static boolean useUniformClassDistributionFilering = false; // for  filtering (be careful when using this)
	public static int WORD_N_GRAM_MIN = 1;
	public static int WORD_N_GRAM_MAX = 3;
	public static int CHAR_N_GRAM_MIN = 2;
	public static int CHAR_N_GRAM_MAX = 5;
	public static int N_GRAM_MAXCANDIDATES = 1000;

	
	@Discriminator
	private String variant;
	
	@Discriminator
	private String targets;
	
	@Discriminator
	private String years;
	
	@Discriminator
	private String word2vecDims;
		
	public void execute(TaskContext context) throws Exception {
		System.out.println(variant + " - " + targets + " - " + years + " - " + word2vecDims);
        String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();

		
		 TcFeatureSet featureSet= getFeatureSet(variant,word2vecDims,years,baseDir,targets);
		 System.out.println(featureSet.toString());
		 StanceClassification_CrossValidation experiment = new StanceClassification_CrossValidation();
		 ParameterSpace parameterSpace = experiment.setupCrossValidation(baseDir + "/semevalData", targets, featureSet);
		 String experimentName = targets.replace("-", "");
		 experimentName = experimentName.replace(" ", "");
		 experimentName= experimentName.toLowerCase();
		 experimentName+="_"+years+"_"+word2vecDims+"_"+variant;
		 experiment.runCrossValidation(parameterSpace, "stanceExperiment_" + experimentName);
	
		
	}
	private static TcFeatureSet getFeatureSet(String variant, String dimension, String year,String basedir, String target) throws IOException {
		TcFeatureSet featureSet=null; 
		String param="/"+year+"/"+dimension+"/";
		 if(variant.equals("Word2Vec_raw")){
			  target= getTargetName(target);
			   featureSet = new TcFeatureSet(
						TcFeatureFactory.create(WordEmbeddingDFE.class, WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION,DkproContext.getContext().getWorkspace().getAbsolutePath()+param+target+year+".txt_word2Vec_"+dimension+".txt"),
						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
						);
		   }
		if(variant.equals("Word2Vec_raw_atheism_embeddings")){
			//set embedding file to Atheism
			 target= getTargetName("ATHEISM");
			 featureSet = new TcFeatureSet(
						TcFeatureFactory.create(WordEmbeddingDFE.class, WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION,DkproContext.getContext().getWorkspace().getAbsolutePath()+param+target+year+".txt_word2Vec_"+dimension+".txt"),
						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
						);
			   
		   }
		   if(variant.equals("Word2Vec_clustered1000")){
			   target= getTargetName(target);
			   featureSet = new TcFeatureSet(
						TcFeatureFactory.create(WordEmbeddingClusterMembershipDFE.class, WordEmbeddingClusterMembershipDFE.WORD_TO_CLUSTER_FILE,DkproContext.getContext().getWorkspace().getAbsolutePath()+"/"+year+"/"+dimension+"/cluster_1000/"+target+year+".txt_word2Vec_"+dimension+".txt_1000.cluster", WordEmbeddingClusterMembershipDFE.NUMBER_OF_CLUSTERS,1000),
						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
						);
		   }
		   if(variant.equals("Word2Vec_clustered1000_atheism_Embeddings")){
			 //set embedding file to Atheism
			   target= getTargetName("ATHEISM");
			   featureSet = new TcFeatureSet(
						TcFeatureFactory.create(WordEmbeddingClusterMembershipDFE.class, WordEmbeddingClusterMembershipDFE.WORD_TO_CLUSTER_FILE,DkproContext.getContext().getWorkspace().getAbsolutePath()+"/"+year+"/"+dimension+"/cluster_1000/"+target+year+".txt_word2Vec_"+dimension+".txt_1000.cluster", WordEmbeddingClusterMembershipDFE.NUMBER_OF_CLUSTERS,1000),
						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
						);
		   }
		   if(variant.equals(EmbeddingVariant.BROWNCLUSTERS)){
			   featureSet = new TcFeatureSet(
						TcFeatureFactory.create(BrownClusterMembershipDFE.class, BrownClusterMembershipDFE.PARAM_BROWN_CLUSTERS_LOCATION,"")
						,
						TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
						TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
								N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
								NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
						);
		   }
		 if(featureSet == null){
			 throw new IllegalArgumentException("no feature set mapped to your configuration "+variant+" ("+target+", "+dimension+", "+year+")");
		 }
		return featureSet;
	}
	
	
	private static String getTargetName(String target) {
		switch (target) {
        case "ATHEISM":
            return "atheism";
        case "Original_Stance":
        	 return "atheism";
        case "Same-sex marriage":
        	return "same_sex_marriage";
        case "religious_freedom":
        	return "religious_freedom";
        case "Conservative_Movement":
        	return "conservative_movement";
        case "Freethinking":
        	return "freethinking";
        case "Islam":
        	return "islam";
        case "No_evidence_for_religion":
        	return "no_evidence_for_religion";
        case "USA":
        	return "usa";
        case "Supernatural_Power_Being":
        	return "supernatural_being";
        case "Life_after_death":
        	return "lifeafterdeath";
        case "Christianity":
        	return "christianity";
        case "secularism":
        	return "secularism";
        default:
            throw new IllegalArgumentException("Invalid target "+target);
		}
	}
	

	private static String getEmbeddingName(String dimension, String target) {
		switch (target) {
        case "ATHEISM":
            return "atheism_word2Vec_"+dimension+".txt";
        case "Original_Stance":
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
        case "secularism":
        	return "christianity_word2Vec_"+dimension+".txt";
        default:
            throw new IllegalArgumentException("Invalid target "+target);
		}
	}
	
}
