package stanceClassificationTaskA;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import annotators.ModalVerbAnnotator;
import dataInspection.PreprocessingTokenizationInspector;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.length.NrOfTokensDFE;
import org.dkpro.tc.features.length.NrOfTokensPerSentenceDFE;
import org.dkpro.tc.features.ngram.LuceneNGramDFE;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.style.ContextualityMeasureFeatureExtractor;
import org.dkpro.tc.features.style.LongWordsFeatureExtractor;
import org.dkpro.tc.features.style.TypeTokenRatioFeatureExtractor;
import org.dkpro.tc.features.twitter.EmoticonRatioDFE;
import org.dkpro.tc.features.twitter.NumberOfHashTagsDFE;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.BatchCrossValidationUsingTCEvaluationReport;
import org.dkpro.tc.ml.report.BatchTrainTestUsingTCEvaluationReport;
import org.dkpro.tc.weka.WekaClassificationAdapter;
import org.dkpro.tc.weka.WekaClassificationUsingTCEvaluationAdapter;
import org.dkpro.tc.weka.report.WekaClassificationReport;
import org.dkpro.tc.weka.report.WekaFeatureValuesReport;
import org.dkpro.tc.weka.report.WekaOutcomeIDReport;
import featureExtractors.HashTagDFE;
import featureExtractors.LuceneNgramInspection;
import featureExtractors.ModalVerbFeaturesDFE;
import featureExtractors.SimpleNegationDFE;
import featureExtractors.StackedFeatureDFE;
import featureExtractors.TopicDFE;
import featureExtractors.sentiment.SimpleSentencePolarityDFE;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Hashtags;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Tokens;
import featureExtractors.stanceLexicon.SummedStanceDFE_staticLexicon;
import io.ConfusionMatrixOutput;
import io.TaskATweetReader;
import util.PreprocessingPipeline;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.berkeleyparser.BerkeleyParser;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpDependencyParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;

public class TaskA_Experiment implements Constants {

	public static final String LANGUAGE_CODE = "en";
	public static final int NUM_FOLDS = 3;
	public static final String CORPUS = "/semevalTask6/tweetsTaskA"; 
	public static int N_GRAM_MIN=1;
	public static int N_GRAM_MAX=3;
	public static int N_GRAM_MAXCANDIDATES=1000;
	
	public static String[] FES={
//								ContextualityMeasureFeatureExtractor.class.getName(),
//							    LuceneNGramDFE.class.getName(),
//							    HashTagDFE.class.getName(),
//							    TopicDFE.class.getName(),
								StanceLexiconDFE_Tokens.class.getName(),
								StanceLexiconDFE_Hashtags.class.getName(),
								SimpleSentencePolarityDFE.class.getName(),	
//							    SimpleNegationDFE.class.getName(),
//							    EmoticonRatioDFE.class.getName(),
//							    LuceneNgramInspection.class.getName(),
//							    NrOfTokensDFE.class.getName(),
//							    LongWordsFeatureExtractor.class.getName(), //configure to 6!
//							    NrOfTokensPerSentenceDFE.class.getName(),
//							    ModalVerbFeaturesDFE.class.getName()
//							    ,TypeTokenRatioFeatureExtractor.class.getName(),
							    };
	
	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
    	System.out.println("DKPRO_HOME: "+ baseDir);
    	
    	TaskA_Experiment experiment = new TaskA_Experiment();
        ParameterSpace pSpace = experiment.setup(baseDir);
        experiment.runCrossValidation(pSpace, "stanceDetection");

	}
	
	private void runCrossValidation(ParameterSpace pSpace, String experimentName) throws Exception {
		
		ExperimentCrossValidation batch = new ExperimentCrossValidation(experimentName, WekaClassificationUsingTCEvaluationAdapter.class,
				NUM_FOLDS);
		batch.setPreprocessing(PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno());
		// batch.addInnerReport(WekaClassificationReport.class);
//		batch.addInnerReport(WekaFeatureValuesReport.class);
		batch.addInnerReport(ConfusionMatrixOutput.class);
//		batch.addInnerReport(WekaOutcomeIDReport.class);
		batch.setParameterSpace(pSpace);

		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
//		batch.addReport(BatchCrossValidationReport.class);
		batch.addReport(BatchCrossValidationUsingTCEvaluationReport.class);
		// batch.addReport(BatchTrainTestUsingTCEvaluationReport.class);

		// Run
		Lab.getInstance().run(batch);
		
	}


	@SuppressWarnings("unchecked")
	private ParameterSpace setup(String baseDir) {
		// configure reader dimension
		Map<String, Object> dimReaders= getDimReaders(baseDir);
		//add/configure classifiers
        Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
                Arrays.asList(new String[] { 
                		J48.class.getName() 
//                		ZeroR.class.getName()
                		}));
        
        Dimension<List<Object>> dimPipelineParameters= getPipelineParameters(baseDir);
        
        Dimension<List<String>> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, Arrays.asList(FES));
        //bundle parameterspace
        ParameterSpace pSpace= bundleParameterSpace(dimReaders,dimPipelineParameters,dimFeatureSets,dimClassificationArgs);
        
		return pSpace;
	}

	/**
	 * bundle paramterSpace (implement feature Selection here)
	 * @param dimReaders
	 * @param dimPipelineParameters
	 * @param dimFeatureSets
	 * @param dimClassificationArgs
	 * @return
	 */
	private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders,
			Dimension<List<Object>> dimPipelineParameters, Dimension<List<String>> dimFeatureSets,
			Dimension<List<String>> dimClassificationArgs) {
		
		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
                Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL),
                Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
                dimPipelineParameters,
                dimFeatureSets,
                dimClassificationArgs
        );	}

	private Dimension<List<Object>> getPipelineParameters(String baseDir) {
		@SuppressWarnings("unchecked")
		Dimension<List<Object>> dimPipelineParameters = Dimension.create(
                DIM_PIPELINE_PARAMS,
                Arrays.asList(new Object[] {
                		NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
                		NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, N_GRAM_MIN,
                		NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, N_GRAM_MAX,
                		HashTagDFE.PARAM_HASHTAGS_FILE_PATH,"src/main/resources/lists/hashTags.txt",
                		SummedStanceDFE_staticLexicon.PARAM_USE_STANCE_LEXICON,"true",
						SummedStanceDFE_staticLexicon.PARAM_USE_HASHTAG_LEXICON, "true",
						StackedFeatureDFE.PARAM_ID2OUTCOME_FILE_PATH,"src/main/resources/ngram_stacking/favor_against/id2homogenizedOutcome.txt"
                })
        );
		return dimPipelineParameters;
	}

	private Map<String, Object> getDimReaders(String baseDir) {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		 dimReaders.put(DIM_READER_TRAIN, TaskATweetReader.class);
         dimReaders.put(DIM_READER_TRAIN_PARAMS,
                         Arrays.asList(TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir+CORPUS,
                        		 TaskATweetReader.PARAM_LANGUAGE, LANGUAGE_CODE,
                        		 TaskATweetReader.PARAM_PATTERNS, "*.xml"
                        		));
		return dimReaders;
	}

}
