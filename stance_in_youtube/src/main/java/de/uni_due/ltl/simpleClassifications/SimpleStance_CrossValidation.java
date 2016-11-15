package de.uni_due.ltl.simpleClassifications;

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
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni_due.ltl.featureExtractors.CommentNgrams.CommentNGram;
import io.ConfusionMatrixOutput;
import io.CrossValidationReport;
import io.YouTubeReader;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;


public class SimpleStance_CrossValidation implements Constants{

	
		/**
		 * XXX CONSTANTS
		 */
		public static final String LANGUAGE_CODE = "en";
		public static boolean useUniformClassDistributionFilering = false; // for  filtering (be careful when using this)
		public static int WORD_N_GRAM_MIN = 1;
		public static int WORD_N_GRAM_MAX = 3;
		public static int CHAR_N_GRAM_MIN = 2;
		public static int CHAR_N_GRAM_MAX = 5;
		public static int N_GRAM_MAXCANDIDATES = 7500;
		private static final int NUM_FOLDS = 6;
		private static final String TARGET_LABLE = "DEATH PENALTY";
		private static final String TARGET_Set = "1";
		
		private boolean ablation=false;

		public static TcFeatureSet featureSet = new TcFeatureSet(
//				TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//						N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
//						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX)
				TcFeatureFactory.create(CommentNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
						N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX)
				
//				,
//				,TcFeatureFactory.create(Remebered_UsersFE.class,Remebered_UsersFE.PARAM_USER_LIST,"src/main/resources/list/clearNameMapping.txt")
//				,TcFeatureFactory.create(ContainsReferee.class)
//				,TcFeatureFactory.create(CommentTypeFE.class)
//				TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//						N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
//						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
//				,
				//lenght features
//				,TcFeatureFactory.create(NrOfTokens.class)
//				,
//				TcFeatureFactory.create(NrOfTokensPerSentence.class),
//				TcFeatureFactory.create(AvgNrOfCharsPerToken.class),
//				TcFeatureFactory.create(AvgNrOfCharsPerSentence.class),
//				TcFeatureFactory.create(NrOfSentences.class)
				);

		public static void main(String[] args) throws Exception {
			String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
			System.out.println("DKPRO_HOME: " + baseDir);
			SimpleStance_CrossValidation experiment = new SimpleStance_CrossValidation();
			ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/youtubeStance/corpus_minorityVote/bin/", TARGET_LABLE,TARGET_Set,featureSet);
			experiment.runCrossValidation(pSpace, "debateStance_conatins_referee");
		
//			for(int j=2; j<=10;j++){
//				for(int i=3500; i<=10000; i+=500){
//					TcFeatureSet featureSet1 = new TcFeatureSet(TcFeatureFactory.create(CommentNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
//							i, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
//							NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, j));
//							
//					SimpleStance_CrossValidation experiment = new SimpleStance_CrossValidation();
//					ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/youtubeStance/corpus_minorityVote/bin/", TARGET_LABLE,TARGET_Set,featureSet1);
//					experiment.runCrossValidation(pSpace, "debateStance_topK-"+String.valueOf(i)+"_maxNgrams-"+String.valueOf(j));
//				}
//			}
			
			

			// XXX run CV for each explicit target in Array
//			for (String explicitTarget : explicitTargets) {
//				ParameterSpace pSpace_explicit = experiment.setupCrossValidation(baseDir + "/semevalTask6/annotationStudy/originalDebateStanceLabels/bin", explicitTarget,featureSet);
//				String experimentName = explicitTarget.replace("-", "");
//				experimentName = explicitTarget.replace(" ", "");
//
//				experiment.runCrossValidation(pSpace_explicit, "stanceExperiment_" + experimentName);
//				if(saveModel){
//					experiment.saveModel(pSpace_explicit, experimentName);
//				}else{
//					experiment.runCrossValidation(pSpace_explicit, "stanceExperiment_" + experimentName);
//				}
//			}
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
		 * @return
		 * @throws ResourceInitializationException 
		 */
		@SuppressWarnings("unchecked")
		public ParameterSpace setupCrossValidation(String dataLocation, String subTarget, String targetSet, TcFeatureSet featureSet) throws ResourceInitializationException {
			// configure reader dimension
			Map<String, Object> dimReaders = getDimReaders(dataLocation, subTarget,targetSet);

			// XXX uncomment/comment other ML Algorithms (SMO, J48 are relevant for
			// the paper; ZeroR is majority class classifier)
			Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
					asList(new String[] { SMO.class.getName() })
//					,
//			 asList(new String[] { ZeroR.class.getName() })
//			 ,
//			 asList(new String[] { J48.class.getName() })
//			 ,
//			 asList(new String[] { RandomForest.class.getName() })
			// ,
			// asList(new String[] { Logistic.class.getName() })
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

		private Map<String, Object> getDimReaders(String dir, String subTarget, String targetSet) throws ResourceInitializationException {
			String inputTrainFolder = dir;
			Map<String, Object> dimReaders = new HashMap<String, Object>();
			System.out.println("read from "+inputTrainFolder);
			dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, inputTrainFolder, YouTubeReader.PARAM_LANGUAGE,
					LANGUAGE_CODE, YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,subTarget, YouTubeReader.PARAM_TARGET_SET,targetSet));

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

			if (useUniformClassDistributionFilering) {
				Dimension<List<String>> dimFeatureFilters = Dimension.create(DIM_FEATURE_FILTERS,
						Arrays.asList(new String[] { UniformClassDistributionFilter.class.getName() }));

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
			return createEngineDescription(createEngineDescription(FunctionalPartsAnnotator.class)
			);
		}

	
}
