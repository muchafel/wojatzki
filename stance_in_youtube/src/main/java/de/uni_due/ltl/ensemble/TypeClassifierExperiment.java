package de.uni_due.ltl.ensemble;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
import org.dkpro.tc.api.features.TcFeature;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.util.ExperimentUtil;
import org.dkpro.tc.features.length.NrOfTokens;
import org.dkpro.tc.features.length.NrOfTokensPerSentence;
import org.dkpro.tc.features.style.TypeTokenRatioFeatureExtractor;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.uni_due.ltl.featureExtractors.PredictedStance;
import de.uni_due.ltl.featureExtractors.SocherSentimentFE;
import de.uni_due.ltl.featureExtractors.commentNgrams.CommentNGram;
import de.uni_due.ltl.featureExtractors.subdebates.ClassifiedSubdebateDFE;
import de.uni_due.ltl.featureExtractors.wordembeddings.EmbeddingCoverage;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingDFE;
import de.uni_due.ltl.simpleClassifications.SimpleStance_CrossValidation;
import de.uni_due.ltl.util.None_Filter;
import io.ConfusionMatrixOutput;
import io.CrossValidationReport;
import io.YouTubeReader;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;

public class TypeClassifierExperiment implements Constants{
	/**
	 * XXX CONSTANTS
	 */
	public static final String LANGUAGE_CODE = "en";
	
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
	public static boolean filterNONE = false;

	public static TcFeatureSet featureSet = new TcFeatureSet(
			TcFeatureFactory.create(CommentNGram.class, CommentNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
					CommentNGram.PARAM_NGRAM_MIN_N, 1, CommentNGram.PARAM_NGRAM_MAX_N, 3, CommentNGram.PARAM_UNIQUE_NAME, "A")
			,TcFeatureFactory.create(SocherSentimentFE.class)
			,TcFeatureFactory.create(NrOfTokensPerSentence.class)
			,TcFeatureFactory.create(NrOfTokens.class)
			,TcFeatureFactory.create(TypeTokenRatioFeatureExtractor.class)
			,TcFeatureFactory.create(WordEmbeddingDFE.class, WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION,"src/main/resources/list/prunedEmbeddings.84B.300d.txt")
			,TcFeatureFactory.create(EmbeddingCoverage.class,EmbeddingCoverage.PARAM_WORDEMBEDDINGLOCATION,"src/main/resources/list/prunedEmbeddings.84B.300d.txt")
			);

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		TypeClassifierExperiment experiment = new TypeClassifierExperiment();
		ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/youtubeStance/corpus_curated/bin_preprocessed/", TARGET_LABLE,TARGET_Set,featureSet);
		experiment.runCrossValidation(pSpace, "simple_ensemble");
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
//		 ,
//		 asList(new String[] { J48.class.getName() }),
//		 asList(new String[] { BayesNet.class.getName() }),
//		 asList(new String[] { MultilayerPerceptron.class.getName() })
//		 ,
//		 asList(new String[] { Logistic.class.getName() })
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
		dimReaders.put(DIM_READER_TRAIN,
				CollectionReaderFactory.createReaderDescription(YouTubeClassificationTypeReader.class,
						YouTubeReader.PARAM_SOURCE_LOCATION, inputTrainFolder, YouTubeReader.PARAM_LANGUAGE,
						LANGUAGE_CODE, YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,
						subTarget, YouTubeReader.PARAM_TARGET_SET, targetSet,
						YouTubeClassificationTypeReader.PARAM_SVM_ID2OUTCOME_FILE_PATH,
						"src/main/resources/id2outcome/debateStance/curated/ngrams_sentiment.txt",
						YouTubeClassificationTypeReader.PARAM_LSTM_ID2OUTCOME_FILE_PATH,
						"src/main/resources/id2outcome/debateStance/curated/lstm_100_fixed_random2.txt"));

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
		return createEngineDescription(createEngineDescription(NoOpAnnotator.class)
		);
	}

}
