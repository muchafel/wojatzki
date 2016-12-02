package de.uni_due.ltl.simpleClassifications;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static de.uni_due.ltl.util.TargetSets.targets_Set1;
import static de.uni_due.ltl.util.TargetSets.targets_Set2;

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
import org.dkpro.tc.fstore.filter.UniformClassDistributionFilter;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentSaveModel;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;
import org.springframework.util.Log4jConfigurer;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.uni_due.ltl.featureExtractors.SocherSentimentFE;
import de.uni_due.ltl.featureExtractors.commentNgrams.CommentNGram;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingDFE;
import io.ConfusionMatrixOutput;
import io.CrossValidationReport;
import io.YouTubeReader;
import io.YouTubeSubDebateReader;
import weka.classifiers.functions.SMO;

public class ExplicitStance_CrossValidation implements Constants {

	/**
	 * XXX CONSTANTS
	 */
	public static final String LANGUAGE_CODE = "en";
	public static boolean useUniformClassDistributionFilering = true; // for
																		// filtering
																		// (be
																		// careful
																		// when
																		// using
																		// this)
	public static int WORD_N_GRAM_MIN = 1;
	public static int WORD_N_GRAM_MAX = 3;
	public static int CHAR_N_GRAM_MIN = 2;
	public static int CHAR_N_GRAM_MAX = 5;
	public static int N_GRAM_MAXCANDIDATES = 6500;
	private static final int NUM_FOLDS = 6;
	private static final String TARGET_LABLE = "DEATH PENALTY";
	private static final String TARGET_Set = "1";

	private boolean ablation = false;

	public static TcFeatureSet featureSet = new TcFeatureSet(

	TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
			CommentNGram.PARAM_NGRAM_MIN_N, 1, CommentNGram.PARAM_NGRAM_MAX_N, 1, CommentNGram.PARAM_UNIQUE_NAME, "A"),
			TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
					CommentNGram.PARAM_NGRAM_MIN_N, 2, CommentNGram.PARAM_NGRAM_MAX_N, 2,
					CommentNGram.PARAM_UNIQUE_NAME, "B"),
			TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, 500,
					CommentNGram.PARAM_NGRAM_MIN_N, 3, CommentNGram.PARAM_NGRAM_MAX_N, 3,
					CommentNGram.PARAM_UNIQUE_NAME, "C")
					//
					// ,TcFeatureFactory.create(LuceneCharacterNGram.class,
					// NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
					// N_GRAM_MAXCANDIDATES,
					// NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N,
					// CHAR_N_GRAM_MIN,
					// NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N,
					// CHAR_N_GRAM_MAX)
	, TcFeatureFactory.create(SocherSentimentFE.class), TcFeatureFactory.create(WordEmbeddingDFE.class,
			WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION, "src/main/resources/list/prunedEmbeddings.84B.300d.txt")

	// ,
	// ,TcFeatureFactory.create(UserName_FE.class,UserName_FE.PARAM_USER_LIST,"src/main/resources/list/clearNameMapping.txt")
	// ,TcFeatureFactory.create(RecurrentAuthor.class)
	// ,TcFeatureFactory.create(Stance_RecurrentAuthor.class,
	// Stance_RecurrentAuthor.PARAM_USE_ORACLE,
	// true,Stance_RecurrentAuthor.PARAM_ID2OUTCOME_FOLDER_PATH,"src/main/resources/id2outcome/")
	// ,TcFeatureFactory.create(ContainsRefereeFE.class)
	// ,TcFeatureFactory.create(CommentTypeFE.class)
	// TcFeatureFactory.create(LuceneCharacterNGram.class,
	// NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
	// N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N,
	// CHAR_N_GRAM_MIN,
	// NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
	// ,
	// lenght features
	// ,TcFeatureFactory.create(NrOfTokens.class)
	// ,
	// TcFeatureFactory.create(NrOfTokensPerSentence.class),
	// TcFeatureFactory.create(AvgNrOfCharsPerToken.class),
	// TcFeatureFactory.create(AvgNrOfCharsPerSentence.class),
	// TcFeatureFactory.create(NrOfSentences.class)
	);

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		// ExplicitStance_CrossValidation experiment = new
		// ExplicitStance_CrossValidation();
		// ParameterSpace pSpace = experiment.setupCrossValidation(baseDir +
		// "/youtubeStance/corpus_minorityVote/bin/",
		// TARGET_LABLE,TARGET_Set,featureSet);
		// experiment.runCrossValidation(pSpace,
		// "debateNgram_oracle_RecurrentAuthor");

		// targets_Set2
		for (String explicitTarget : targets_Set2) {
			ExplicitStance_CrossValidation experiment = new ExplicitStance_CrossValidation();
			String experimentName = getValidName(explicitTarget.replace("-", ""));

			System.out.println(experimentName);
			ParameterSpace pSpace_explicit = experiment.setupCrossValidation(
					baseDir +"/youtubeStance/corpus_minorityVote/bin_preprocessed/", explicitTarget, "2", featureSet);
			experiment.runCrossValidation(pSpace_explicit, "stanceExperiment_subSampled_binary" + experimentName);
		}

		// targets_Set1
		for (String explicitTarget : targets_Set1) {
			ExplicitStance_CrossValidation experiment = new ExplicitStance_CrossValidation();
			String experimentName = getValidName(explicitTarget.replace("-", ""));

			System.out.println(experimentName);
			System.out.println();
			ParameterSpace pSpace_explicit = experiment.setupCrossValidation(
					baseDir +"/youtubeStance/corpus_minorityVote/bin_preprocessed/", explicitTarget, "1", featureSet);
			experiment.runCrossValidation(pSpace_explicit, "stanceExperiment_subSampled_binary" + experimentName);
		}

		/**
		 * parameter search ngram
		 */
		// for(int j=2; j<=10;j++){
		// for(int i=500; i<=10000; i+=500){
		// TcFeatureSet featureSet1 = new TcFeatureSet(
		// TcFeatureFactory.create(CommentNGram.class,
		// CommentNGram.PARAM_NGRAM_USE_TOP_K, i,
		// CommentNGram.PARAM_NGRAM_MIN_N, 1, CommentNGram.PARAM_NGRAM_MAX_N, 1,
		// CommentNGram.PARAM_UNIQUE_NAME, "A")
		// ,TcFeatureFactory.create(CommentNGram.class,
		// CommentNGram.PARAM_NGRAM_USE_TOP_K, i,
		// CommentNGram.PARAM_NGRAM_MIN_N, 2, CommentNGram.PARAM_NGRAM_MAX_N, 2,
		// CommentNGram.PARAM_UNIQUE_NAME, "B")
		// ,TcFeatureFactory.create(CommentNGram.class,
		// CommentNGram.PARAM_NGRAM_USE_TOP_K, i,
		// CommentNGram.PARAM_NGRAM_MIN_N, 3, CommentNGram.PARAM_NGRAM_MAX_N, 3
		// , CommentNGram.PARAM_UNIQUE_NAME, "C")
		// );
		//
		// SimpleStance_CrossValidation experiment = new
		// SimpleStance_CrossValidation();
		// ParameterSpace pSpace = experiment.setupCrossValidation(baseDir +
		// "/youtubeStance/corpus_minorityVote/bin/",
		// TARGET_LABLE,TARGET_Set,featureSet1);
		// experiment.runCrossValidation(pSpace,
		// "debateStance_topK-"+String.valueOf(i)+"_maxNgrams-123");
		// }
		// }

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
	 * @param target
	 * @param targetSet
	 * @param featureSet
	 * @return
	 * @throws ResourceInitializationException
	 */
	@SuppressWarnings("unchecked")
	public ParameterSpace setupCrossValidation(String dataLocation, String target, String targetSet,
			TcFeatureSet featureSet) throws ResourceInitializationException {
		// configure reader dimension
		Map<String, Object> dimReaders = getDimReaders(dataLocation, target, targetSet);
		Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
				asList(new String[] { SMO.class.getName() }));

		Dimension<TcFeatureSet> dimFeatureSets = null;
		if (ablation) {
			dimFeatureSets = ExperimentUtil.getAblationTestFeatures(featureSet.toArray(new TcFeature[0]));
		} else {
			dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);
		}

		// bundle parameterspace
		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, dimClassificationArgs);

		return pSpace;
	}

	private Map<String, Object> getDimReaders(String dir, String target, String targetSet)
			throws ResourceInitializationException {
		String inputTrainFolder = dir;
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read from " + inputTrainFolder + " target: " + target + " set: " + targetSet);
		dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(YouTubeSubDebateReader.class,
				YouTubeSubDebateReader.PARAM_SOURCE_LOCATION, inputTrainFolder, YouTubeSubDebateReader.PARAM_LANGUAGE,
				LANGUAGE_CODE, YouTubeSubDebateReader.PARAM_PATTERNS, "*.bin",
				YouTubeSubDebateReader.PARAM_TARGET_LABEL, target, YouTubeSubDebateReader.PARAM_TARGET_SET, targetSet));

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
					Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL), Dimension.create(DIM_FEATURE_MODE, FM_UNIT),
					dimFeatureSets, dimFeatureFilters, dimClassificationArgs);
		} else {
			return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
					Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL), Dimension.create(DIM_FEATURE_MODE, FM_UNIT),
					dimFeatureSets, dimClassificationArgs);
		}
	}

	private AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(FunctionalPartsAnnotator.class));
	}

}
