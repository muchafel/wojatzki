package ml_experiments;

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
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.ngram.LuceneCharacterNGram;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.fstore.filter.UniformClassDistributionFilter;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.weka.WekaClassificationAdapter;
import org.springframework.util.Log4jConfigurer;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import featureExtractors.BrownClusterMembershipDFE;
import featureExtractors.ClassifiedSubTarget_id2outcomeDFE;
import featureExtractors.NearestGloVeCluster;
import featureExtractors.OracleSubTargetDFE;
import featureExtractors.StackedNGramAnnotator_id2outcomeDFE;
import featureExtractors.WordEmbeddingClusterMembershipDFE;
import io.ConfusionMatrixOutput;
import io.CrossValidationReport;
import io.StanceReader;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.ZeroR;

/**
 * class for executing machine learning experiments on stance data configuration
 * options are marked with an X
 * 
 * @author michael
 *
 */
public class StanceClassification_CrossValidation implements Constants {

	/**
	 * XXX CONSTANTS
	 */
	public static final String LANGUAGE_CODE = "en";
	private static final String FilteringPostfix = "_wo_irony_understandability"; // use if you want to filter irony and understandability
	// private static final String FilteringPostfix = "";
	private static final String modelOutputFolder = "src/main/resources/models";
	public static boolean useUniformClassDistributionFilering = false; // for  filtering (be careful when using this)
	public static int WORD_N_GRAM_MIN = 1;
	public static int WORD_N_GRAM_MAX = 3;
	public static int CHAR_N_GRAM_MIN = 1;
	public static int CHAR_N_GRAM_MAX = 3;
	
	public static int N_GRAM_MAXCANDIDATES = 1000;

	/**
	 * XXX specify target here (TARGET_LABLE or Array)
	 */
	private static ArrayList<String> explicitTargets = new ArrayList<String>(Arrays.asList("secularism",
			"Same-sex marriage", "religious_freedom", "Conservative_Movement", "Freethinking", "Islam",
			"No_evidence_for_religion", "USA", "Supernatural_Power_Being", "Life_after_death", "Christianity"));

//	public static final String TARGET_LABLE = "ATHEISM"; // ,67
	// public static final String TARGET_LABLE = "Original_Stance"; //need to
	// get that info from original xmls
//	 public static final String TARGET_LABLE = "Supernatural_Power_Being";
	// //.76
//	 public static final String TARGET_LABLE = "Christianity"; //.8
	// public static final String TARGET_LABLE = "Freethinking"; // XX
	 public static final String TARGET_LABLE = "Islam"; // .95
	// public static final String TARGET_LABLE = "Life_after_death"; // ,97
	// public static final String TARGET_LABLE = "No_evidence_for_religion"; //
	// XX
	// public static final String TARGET_LABLE = "religious_freedom"; //XX
	// public static final String TARGET_LABLE = "USA"; //XX
	// public static final String TARGET_LABLE = "secularism";
	// public static final String TARGET_LABLE = "Same-sex marriage";

	// F1 /semeval measure
	// ngram (char + word): .636 / .615
	// ngram (char + word) stacked: .56 / .37 (klassifiziert nichts als FAVOR
	// ;// char und word ngramme trennen? getrennt: .55 / .36)

	// stacked (char + word) + oracle : .88 / .85
	// oracle alone: .88 / .85
	// ngram (char + word) + oracle: .72/ .69

	// stacked (char + word) + predicted: .63 / .56
	// ngram (char + word)+ predicted: .63 / .59
	// predicted alone: .63 / .56

	/**
	 * XXX specify features here
	 */
	public TcFeatureSet featureSet = new TcFeatureSet(
			// TcFeatureFactory.create(StackedNGramAnnotator_id2outcomeDFE.class,
			// StackedNGramAnnotator_id2outcomeDFE.PARAM_ID2OUTCOME_CHARNGRAM_FILE_PATH,
			// "src/main/resources/lists/id2outcome_char_ngrams.txt",
			// StackedNGramAnnotator_id2outcomeDFE.PARAM_ID2OUTCOME_WORDNGRAM_FILE_PATH,
			// "src/main/resources/lists/id2outcome_word_ngrams.txt"),
			// TcFeatureFactory.create(OracleSubTargetDFE.class),
//			TcFeatureFactory.create(WordEmbeddingClusterMembershipDFE.class, WordEmbeddingClusterMembershipDFE.WORD_TO_CLUSTER_FILE,"src/main/resources/wordsToClusters_atheism_d_75_c_1000_w2v.txt", WordEmbeddingClusterMembershipDFE.NUMBER_OF_CLUSTERS,1000)
//			,
//			TcFeatureFactory.create(BrownClusterMembershipDFE.class, BrownClusterMembershipDFE.PARAM_BROWN_CLUSTERS_LOCATION,"src/main/resources/brown_clusters/enTweetBrownC1000F40.txt")
//			,
//			TcFeatureFactory.create(NearestGloVeCluster.class, NearestGloVeCluster.PARAM_PRETRAINEDFILE,"src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.25d.txt"),
			TcFeatureFactory.create(LuceneNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
					N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
					NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
			TcFeatureFactory.create(LuceneCharacterNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
					N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
					NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
			);

	public static void main(String[] args) throws Exception {
		
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		StanceClassification_CrossValidation experiment = new StanceClassification_CrossValidation();

		// XXX CV for getting the id2outcome file for the DFE
		ParameterSpace pSpace = experiment.setupCrossValidation(baseDir, TARGET_LABLE);
		experiment.runCrossValidation(pSpace, "stanceExperiment_"+TARGET_LABLE);

		// XXX run CV for each explicit target in Array
		// for(String explicitTarget: explicitTargets){
		// ParameterSpace pSpace_explicit =
		// experiment.setupCrossValidation(baseDir,explicitTarget);
		// String experimentName=explicitTarget.replace("-", "");
		// experimentName=explicitTarget.replace(" ", "");
		//
		// experiment.runCrossValidation(pSpace_explicit,
		// "stanceExperiment_"+experimentName);
		// }
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
	private void runCrossValidation(ParameterSpace pSpace, String experimentName) throws Exception {
		ExperimentCrossValidation batch = new ExperimentCrossValidation(experimentName, WekaClassificationAdapter.class,
				10);

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
	 * @param baseDir
	 * @param subTarget
	 * @return
	 * @throws ResourceInitializationException 
	 */
	@SuppressWarnings("unchecked")
	private ParameterSpace setupCrossValidation(String baseDir, String subTarget) throws ResourceInitializationException {
		// configure reader dimension
		Map<String, Object> dimReaders = getDimReaders(
				baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all" + FilteringPostfix, subTarget);

		// XXX uncomment/comment other ML Algorithms (SMO, J48 are relevant for
		// the paper; ZeroR is majority class classifier)
		Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
				asList(new String[] { SMO.class.getName() })
		// ,
		// asList(new String[] { ZeroR.class.getName() })
		// ,
		// asList(new String[] { RandomTree.class.getName() })
		// ,
		// asList(new String[] { RandomForest.class.getName() })
		// ,
		// asList(new String[] { Logistic.class.getName() })
		);

		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);

		// bundle parameterspace
		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, dimClassificationArgs);

		return pSpace;
	}

	/**
	 * configures the reader (STanceReader is actually a BinCas reader that
	 * assigns TextClassificationOutcome and is sensitive to different target
	 * labels)
	 * 
	 * @param dir
	 * @param subTarget
	 * @return
	 * @throws ResourceInitializationException 
	 */
	private Map<String, Object> getDimReaders(String dir, String subTarget) throws ResourceInitializationException {
		String inputTrainFolder = dir;
		Map<String, Object> dimReaders = new HashMap<String, Object>();

		dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(StanceReader.class, StanceReader.PARAM_SOURCE_LOCATION, inputTrainFolder, StanceReader.PARAM_LANGUAGE,
				LANGUAGE_CODE, StanceReader.PARAM_PATTERNS, "*.bin", StanceReader.PARAM_TARGET_LABEL,subTarget));

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
					Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT), dimFeatureSets, dimFeatureFilters,
					dimClassificationArgs);
		} else {
			return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
					Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL),
					Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT), dimFeatureSets, dimClassificationArgs);
		}

	}

	/**
	 * simple getter for Preprocessing currently XXX add more sophisticated
	 * preprocessing if feature set needs it
	 * 
	 * @return
	 * @throws ResourceInitializationException
	 */
	private AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(ArktweetTokenizer.class)
		// ,
		// createEngineDescription(Stacked_SubTargetClassification.class),
		// createEngineDescription(Stacked_NgramClassification.class)
		);
	}
}
