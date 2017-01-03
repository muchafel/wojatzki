package de.uni_due.subdebateInfluence;

import static de.uni_due.ltl.util.TargetSets.targets_Set1;
import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.TcFeature;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.util.ExperimentUtil;
import org.dkpro.tc.features.length.AvgNrOfCharsPerSentence;
import org.dkpro.tc.features.length.AvgNrOfCharsPerToken;
import org.dkpro.tc.features.length.NrOfSentences;
import org.dkpro.tc.features.length.NrOfTokens;
import org.dkpro.tc.features.length.NrOfTokensPerSentence;
import org.dkpro.tc.features.ngram.LuceneCharacterNGram;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.LuceneSkipNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.style.ContextualityMeasureFeatureExtractor;
import org.dkpro.tc.features.style.TokenRatioFeatureExtractor;
import org.dkpro.tc.fstore.filter.UniformClassDistributionFilter;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentSaveModel;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;
import org.springframework.util.Log4jConfigurer;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni_due.ltl.featureExtractors.CommentTypeFE;
import de.uni_due.ltl.featureExtractors.PredictedStance;
import de.uni_due.ltl.featureExtractors.SocherSentimentFE;
import de.uni_due.ltl.featureExtractors.commentNgrams.CommentNGram;
import de.uni_due.ltl.featureExtractors.explcitVocab.LDA_TopicWordsFE;
import de.uni_due.ltl.featureExtractors.externalResources.ExternalEmbeddingSimilarityDFE;
import de.uni_due.ltl.featureExtractors.externalResources.ExternalVocabularyDFE;
import de.uni_due.ltl.featureExtractors.subdebates.ClassifiedSubdebateDFE;
import de.uni_due.ltl.featureExtractors.subdebates.ClassifiedSubdebateSentencesDFE;
import de.uni_due.ltl.featureExtractors.userModel.ContainsRefereeFE;
import de.uni_due.ltl.featureExtractors.userModel.RecurrentAuthor;
import de.uni_due.ltl.featureExtractors.userModel.Stance_Previous_Comment;
import de.uni_due.ltl.featureExtractors.userModel.Stance_RecurrentAuthor;
import de.uni_due.ltl.featureExtractors.userModel.Stance_ReferredComment;
import de.uni_due.ltl.featureExtractors.userModel.UserName_FE;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingDFE;
import de.uni_due.ltl.util.None_Filter;
import de.uni_due.ltl.util.TargetSets;
import io.ConfusionMatrixOutput;
import io.CrossValidationReport;
import io.RemoveSentenceAnnotations;
import io.YouTubeReader;
import io.YouTube_RemoveSignal_Reader;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;


public class SubdebateSignalFiltered_Stance_CrossValidation implements Constants{
	
		/**
		 * XXX CONSTANTS
		 */
		public static final String LANGUAGE_CODE = "en";
		public static boolean filterNONE = false; // for  filtering (be careful when using this)
		public static int WORD_N_GRAM_MIN = 1;
		public static int WORD_N_GRAM_MAX = 3;
		public static int CHAR_N_GRAM_MIN = 2;
		public static int CHAR_N_GRAM_MAX = 5;
		public static int N_GRAM_MAXCANDIDATES = 6500;
		private static final int NUM_FOLDS = 6;
		private static final String TARGET_LABLE = "DEATH PENALTY";
		private static final String TARGET_Set = "1";
		
		private static boolean useOracle=false;
		
		private boolean ablation=false;

		public static TcFeatureSet featureSet = new TcFeatureSet(
//				
				TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
						CommentNGram.PARAM_NGRAM_MIN_N, 1, CommentNGram.PARAM_NGRAM_MAX_N, 1, CommentNGram.PARAM_UNIQUE_NAME, "A")
				,TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
						CommentNGram.PARAM_NGRAM_MIN_N, 2, CommentNGram.PARAM_NGRAM_MAX_N, 2, CommentNGram.PARAM_UNIQUE_NAME, "B")
				,TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
						CommentNGram.PARAM_NGRAM_MIN_N, 3, CommentNGram.PARAM_NGRAM_MAX_N, 3 , CommentNGram.PARAM_UNIQUE_NAME, "C")
				,TcFeatureFactory.create(SocherSentimentFE.class)
				);

		public static void main(String[] args) throws Exception {
			String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
			System.out.println("DKPRO_HOME: " + baseDir);
			SubdebateSignalFiltered_Stance_CrossValidation experiment = new SubdebateSignalFiltered_Stance_CrossValidation();
			for (String explicitTarget : TargetSets.targets_Set1) {
				ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/youtubeStance/corpus_curated/bin_preprocessed/", TARGET_LABLE,"1",featureSet,explicitTarget);
				String experimentName = getValidName(explicitTarget.replace("-", ""));
				System.out.println(experimentName);
				System.out.println();
				experiment.runCrossValidation(pSpace, "exclude2_"+experimentName);
			}
			for (String explicitTarget : TargetSets.targets_Set2) {
				ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/youtubeStance/corpus_curated/bin_preprocessed/", TARGET_LABLE,"2",featureSet,explicitTarget);
				String experimentName = getValidName(explicitTarget.replace("-", ""));
				System.out.println(experimentName);
				System.out.println();
				experiment.runCrossValidation(pSpace, "exclude2_"+experimentName);
			}
		}

		

		/**
		 * runs the classification pipeline with added reports XXX reports print
		 * classification result to console for every fold and write them to excel
		 * files in org.dkpro.lab
		 * 
		 * @param pSpace
		 * @param experimentName
		 * @throws Exception
		 */
		public void runCrossValidation(ParameterSpace pSpace, String experimentName) throws Exception {
			ExperimentCrossValidation batch = new ExperimentCrossValidation(experimentName, WekaClassificationAdapter.class,
					NUM_FOLDS);

			batch.setPreprocessing(getPreprocessing());
			batch.setParameterSpace(pSpace);
			batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
			batch.addInnerReport(ConfusionMatrixOutput.class);
			batch.addReport(CrossValidationReport.class);

			// Run
			Lab.getInstance().run(batch);
		}
		

		/**
		 * settings for CV (calls getters for readers, pipeline params (feature
		 * extractor params), set the ML algorithm)
		 * 
		 * @param dataLocation
		 * @param subTarget
		 * @param targetSet 
		 * @param featureSet 
		 * @param explicitTargetToRemove 
		 * @return
		 * @throws ResourceInitializationException 
		 */
		@SuppressWarnings("unchecked")
		public ParameterSpace setupCrossValidation(String dataLocation, String subTarget, String targetSet, TcFeatureSet featureSet, String explicitTargetToRemove) throws ResourceInitializationException {
			// configure reader dimension
			Map<String, Object> dimReaders = getDimReaders(dataLocation, subTarget,targetSet,explicitTargetToRemove);

			// XXX uncomment/comment other ML Algorithms (SMO, J48 are relevant for
			// the paper; ZeroR is majority class classifier)
			Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
					asList(new String[] { SMO.class.getName() })
			);

			Dimension<TcFeatureSet> dimFeatureSets=null;
			if(ablation){
				dimFeatureSets= ExperimentUtil.getAblationTestFeatures(featureSet.toArray(new TcFeature[0]));
			}else{
				dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);
			}

			// bundle parameterspace
			ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, dimClassificationArgs);

			return pSpace;
		}

		private Map<String, Object> getDimReaders(String dir, String subTarget, String targetSet, String explicitTargetToRemove) throws ResourceInitializationException {
			String inputTrainFolder = dir;
			Map<String, Object> dimReaders = new HashMap<String, Object>();
			System.out.println("read from "+inputTrainFolder);
			dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(YouTube_RemoveSignal_Reader.class, YouTube_RemoveSignal_Reader.PARAM_BINCAS_LOCATION, inputTrainFolder, YouTube_RemoveSignal_Reader.PARAM_LANGUAGE,
					LANGUAGE_CODE, YouTube_RemoveSignal_Reader.PARAM_PATTERNS, "*.bin", YouTube_RemoveSignal_Reader.PARAM_TARGET_LABEL,subTarget, YouTube_RemoveSignal_Reader.PARAM_TARGET_SET,targetSet, YouTube_RemoveSignal_Reader.PARAM_EXPLICIT_TARGET_SIGNAL_TO_REMOVE,explicitTargetToRemove));

			return dimReaders;
		}

		/**
		 * bundle paramterSpace (implement feature Selection here) use filtering if
		 * flag set
		 * 
		 * @param dimReaders
		 * @param dimFeatureSets
		 * @param dimClassificationArgs
		 * @param dimFeatureFilters
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders, Dimension<TcFeatureSet> dimFeatureSets,
				Dimension<List<String>> dimClassificationArgs) {

			if (filterNONE) {
				Dimension<List<String>> dimFeatureFilters = Dimension.create(DIM_FEATURE_FILTERS,
						Arrays.asList(new String[] { None_Filter.class.getName() }));

				return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
						Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL),
						Dimension.create(DIM_FEATURE_MODE, FM_UNIT), dimFeatureSets, dimFeatureFilters,
						dimClassificationArgs);
			} else {
				return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
						Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL),
						Dimension.create(DIM_FEATURE_MODE, FM_UNIT), dimFeatureSets, dimClassificationArgs);
			}
		}

		private AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
//			return createEngineDescription(
//					createEngineDescription(RemoveSentenceAnnotations.class),
//					createEngineDescription(OpenNlpSegmenter.class,OpenNlpSegmenter.PARAM_WRITE_TOKEN,false,OpenNlpSegmenter.PARAM_WRITE_SENTENCE,true),
//					createEngineDescription(ExplicitStanceSentencePredictions.class,ExplicitStanceSentencePredictions.PARAM_ID2OUTCOME_SUBTARGETS_FOLDER_PATH,"src/main/resources/id2outcome/sentenceStances/lucene")
			return createEngineDescription(createEngineDescription(NoOpAnnotator.class)
			);
		}
		private static String getValidName(String experimentName) {
			experimentName = experimentName.replace(" ", "");
			experimentName = experimentName.replace(".", "");
			experimentName = experimentName.replace(",", "");
			experimentName = experimentName.replace("!", "");
			experimentName = experimentName.replace("(", "");
			experimentName = experimentName.replace(")", "");
			experimentName = experimentName.replace(";", "");
			experimentName = experimentName.replace(":", "");
			experimentName = experimentName.replace("’", "");
			experimentName.replaceAll("[^a-zA-Z]+", "");
			return experimentName;
		}
	
}
