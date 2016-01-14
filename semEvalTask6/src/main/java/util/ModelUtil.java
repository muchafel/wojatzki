package util;

import java.util.ArrayList;
import java.util.Arrays;

import de.tudarmstadt.ukp.dkpro.tc.features.style.LongWordsFeatureExtractor;
import featureExtractors.ModalVerbFeaturesDFE;
import featureExtractors.RepeatedPunctuationDFE;
import featureExtractors.SimpleNegationDFE;
import featureExtractors.StackedFeatureDFE;
import featureExtractors.sentiment.SimpleSentencePolarityDFE;
import featureExtractors.stacking.StackedConceptClassificationDFE;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Hashtags_normalized;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Tokens_normalized;

public class ModelUtil {

	public static String[] FES_HillaryStanceVSNone = {
//			StanceLexiconDFE_Tokens_normalized.class.getName(), //M
			StanceLexiconDFE_Hashtags_normalized.class.getName(), //M
			SimpleNegationDFE.class.getName(), //M
			RepeatedPunctuationDFE.class.getName(), //M
	  		LongWordsFeatureExtractor.class.getName(), //M //configure to 6!
//	  		ModalVerbFeaturesDFE.class.getName(), //M
//			StackedFeatureDFE.class.getName(), //M
//			StackedFeatureDFE.class.getName(), //M
//			StackedBi_Tri_GramStanceNoneDFE.class.getName(), //--> just for saving the model
			StackedConceptClassificationDFE.class.getName()//--> just for saving the model};
			};
	public static String[] FES_AtheismStanceVSNone = {
			StanceLexiconDFE_Tokens_normalized.class.getName(), //M
			StanceLexiconDFE_Hashtags_normalized.class.getName(), //M
//			SimpleNegationDFE.class.getName(), //M
			RepeatedPunctuationDFE.class.getName(), //M
//	  		LongWordsFeatureExtractor.class.getName(), //M //configure to 6!
//	  		ModalVerbFeaturesDFE.class.getName(), //M
//			StackedFeatureDFE.class.getName(), //M
//			StackedBi_Tri_GramStanceNoneDFE.class.getName(), //--> just for saving the model
			StackedConceptClassificationDFE.class.getName()//--> just for saving the model};
			};
	public static String[] FES_AbortionStanceVSNone = {
			StanceLexiconDFE_Tokens_normalized.class.getName(), //M
			StanceLexiconDFE_Hashtags_normalized.class.getName(), //M
			SimpleNegationDFE.class.getName(), //M
			RepeatedPunctuationDFE.class.getName(), //M
	  		LongWordsFeatureExtractor.class.getName(), //M //configure to 6!
	  		ModalVerbFeaturesDFE.class.getName(), //M
			StackedFeatureDFE.class.getName(), //M
//			StackedBi_Tri_GramStanceNoneDFE.class.getName(), //--> just for saving the model
			StackedConceptClassificationDFE.class.getName()//--> just for saving the model};
			};
	public static String[] FES_ClimaStanceVSNone= {
			StanceLexiconDFE_Tokens_normalized.class.getName(), //M
			StanceLexiconDFE_Hashtags_normalized.class.getName(), //M
			SimpleNegationDFE.class.getName(), //M
			RepeatedPunctuationDFE.class.getName(), //M
	  		LongWordsFeatureExtractor.class.getName(), //M //configure to 6!
	  		ModalVerbFeaturesDFE.class.getName(), //M
			StackedFeatureDFE.class.getName(), //M
//			StackedBi_Tri_GramStanceNoneDFE.class.getName(), //--> just for saving the model
			StackedConceptClassificationDFE.class.getName()//--> just for saving the model};
			};
	public static String[] FES_FeminismStanceVSNone = {
			StanceLexiconDFE_Tokens_normalized.class.getName(), //M
			StanceLexiconDFE_Hashtags_normalized.class.getName(), //M
			SimpleNegationDFE.class.getName(), //M
			RepeatedPunctuationDFE.class.getName(), //M
	  		LongWordsFeatureExtractor.class.getName(), //M //configure to 6!
	  		ModalVerbFeaturesDFE.class.getName(), //M
			StackedFeatureDFE.class.getName(), //M
//			StackedBi_Tri_GramStanceNoneDFE.class.getName(), //--> just for saving the model
			StackedConceptClassificationDFE.class.getName()//--> just for saving the model};
			};
	
	public static String[] getOptimizedModelNoneVsStance(String target) throws Exception {
		if(target.equals("HillaryClinton"))return FES_HillaryStanceVSNone;
		else if(target.equals("Atheism"))return FES_AtheismStanceVSNone;
		else if(target.equals("FeministMovement"))return FES_FeminismStanceVSNone;
		else if(target.equals("LegalizationofAbortion"))return FES_AbortionStanceVSNone;
		else if(target.equals("ClimateChangeisaRealConcern"))return FES_ClimaStanceVSNone;
		else throw new Exception("NO Model Found for "+target);
	}

}
