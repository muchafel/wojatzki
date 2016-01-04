package stanceClassificationTaskA;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import annotators.ModalVerbAnnotator;
import dataInspection.PreprocessingTokenizationInspector;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.features.length.NrOfTokensDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.length.NrOfTokensPerSentenceDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.LuceneNGramDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.LuceneSkipNGramDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import de.tudarmstadt.ukp.dkpro.tc.features.style.ContextualityMeasureFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.style.LongWordsFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.style.TypeTokenRatioFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.twitter.EmoticonRatioDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.twitter.NumberOfHashTagsDFE;
import de.tudarmstadt.ukp.dkpro.tc.ml.ExperimentCrossValidation;
import de.tudarmstadt.ukp.dkpro.tc.ml.report.BatchCrossValidationReport;
import de.tudarmstadt.ukp.dkpro.tc.ml.report.BatchCrossValidationUsingTCEvaluationReport;
import de.tudarmstadt.ukp.dkpro.tc.ml.report.BatchTrainTestUsingTCEvaluationReport;
import de.tudarmstadt.ukp.dkpro.tc.weka.WekaClassificationAdapter;
import de.tudarmstadt.ukp.dkpro.tc.weka.WekaClassificationUsingTCEvaluationAdapter;
import de.tudarmstadt.ukp.dkpro.tc.weka.report.WekaClassificationReport;
import de.tudarmstadt.ukp.dkpro.tc.weka.report.WekaFeatureValuesReport;
import de.tudarmstadt.ukp.dkpro.tc.weka.report.WekaOutcomeIDReport;
import edu.berkeley.nlp.syntax.Trees.PunctuationStripper;
import featureExtractors.ClassifiedConceptDFE;
import featureExtractors.ConditionalSentenceCountDFE;
import featureExtractors.HashTagDFE;
import featureExtractors.LuceneNgramInspection;
import featureExtractors.ModalVerbFeaturesDFE;
import featureExtractors.RepeatedPunctuationDFE;
import featureExtractors.SimpleNegationDFE;
import featureExtractors.StackedFeatureDFE;
import featureExtractors.SummedStanceDFE;
import featureExtractors.TopicDFE;
import featureExtractors.sentiment.SimpleSentencePolarityDFE;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Hashtags;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Hashtags_normalized;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Tokens;
import featureExtractors.stanceLexicon.StanceLexiconDFE_Tokens_normalized;
import featureExtractors.stanceLexicon.SummedStanceDFE_functionalParts;
import featureExtractors.stanceLexicon.SummedStanceDFE_staticLexicon;
import io.ConfusionMatrixOutput;
import io.TaskATweetReader;
import util.NoneTrainFilter;
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
import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.task.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;

public class FavorVsAgainst_Experiment_TopicWise implements Constants {

	public static final String LANGUAGE_CODE = "en";
	public static final int NUM_FOLDS = 10;
	public static final String TOPIC_FOLDERS = "/semevalTask6/targets/";
	public static int N_GRAM_MIN = 2;
	public static int N_GRAM_MAX = 3;
	public static int N_GRAM_MAXCANDIDATES = 500;
	public static AnalysisEngineDescription preProcessing;

	public static String[] FES = {
// 			ContextualityMeasureFeatureExtractor.class.getName(),
//			LuceneNGramDFE.class.getName(), 
			StackedFeatureDFE.class.getName(), //M ---> configured to model only bi-and trigrams
			StanceLexiconDFE_Tokens.class.getName(), //M --> un-normalized
			StanceLexiconDFE_Hashtags.class.getName(), //M --> un-normalized
			SimpleSentencePolarityDFE.class.getName(),	//M
//			SummedStanceDFE_functionalParts.class.getName(),
//			HashTagDFE.class.getName(),
//			LuceneNGramDFE.class.getName(),
			SimpleNegationDFE.class.getName(), //M
			ConditionalSentenceCountDFE.class.getName(), //M
			RepeatedPunctuationDFE.class.getName(), //M
//			EmoticonRatioDFE.class.getName(),
//			LuceneNgramInspection.class.getName(),
//			NrOfTokensDFE.class.getName(),
		  	LongWordsFeatureExtractor.class.getName(), //configure to 6?
//			NrOfTokensPerSentenceDFE.class.getName(),
	  		ModalVerbFeaturesDFE.class.getName(), //M
//			TypeTokenRatioFeatureExtractor.class.getName(),
			ClassifiedConceptDFE.class.getName() //M
	};

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir );
		preProcessing=PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno();
		
		for (File folder : getTopicFolders(baseDir+TOPIC_FOLDERS)) {
			System.out.println("experiments for "+folder.getName()+"_stanceDetection");
			FavorVsAgainst_Experiment_TopicWise experiment = new FavorVsAgainst_Experiment_TopicWise();
			ParameterSpace pSpace = experiment.setup(baseDir,folder);
			experiment.runCrossValidation(pSpace, folder.getName()+"_favorVsAgainst");
		}
	}

	private static List<File> getTopicFolders(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<File> folders= new ArrayList<File>();
		for(File f: listOfFiles){
//			if(!f.getName().equals("FeministMovement")){
//				continue;
//			}
//			if(!f.getName().equals("LegalizationofAbortion")){
//				continue;
//			}
//			if(!f.getName().equals("HillaryClinton")){
//				continue;
//			}
//			if(!f.getName().equals("Atheism")){
//				continue;
//			}
//			if(!f.getName().equals("ClimateChangeisaRealConcern")){
//				continue;
//			}
			System.out.println(f.getName());
			if(f.isDirectory())folders.add(f);
		}
		return folders;
	}

	private void runCrossValidation(ParameterSpace pSpace, String experimentName) throws Exception {

		ExperimentCrossValidation batch = new ExperimentCrossValidation(experimentName, WekaClassificationUsingTCEvaluationAdapter.class,
				NUM_FOLDS);
		batch.setPreprocessing(preProcessing);
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
	private ParameterSpace setup(String baseDir, File folder) {
		// configure reader dimension
		Map<String, Object> dimReaders = getDimReaders(baseDir,folder);
		// add/configure classifiers
		Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
				Arrays.asList(new String[] { 
//						J48.class.getName(),
						SMO.class.getName(),
//						MultilayerPerceptron.class.getName(),
//				 ZeroR.class.getName()
		}));

		
		 Dimension<List<String>> dimFeatureFilters = Dimension.create(DIM_FEATURE_FILTERS,
	            Arrays.asList(new String[] { NoneTrainFilter.class.getName() }));
		
		Dimension<List<Object>> dimPipelineParameters = getPipelineParameters(baseDir, folder.getName());

		Dimension<List<String>> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, Arrays.asList(FES));
		// bundle parameterspace
		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimPipelineParameters, dimFeatureSets,
				dimClassificationArgs,dimFeatureFilters);

		return pSpace;
	}

	/**
	 * bundle paramterSpace (implement feature Selection here)
	 * 
	 * @param dimReaders
	 * @param dimPipelineParameters
	 * @param dimFeatureSets
	 * @param dimClassificationArgs
	 * @param dimFeatureFilters 
	 * @return
	 */
	private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders,
			Dimension<List<Object>> dimPipelineParameters, Dimension<List<String>> dimFeatureSets,
			Dimension<List<String>> dimClassificationArgs, Dimension<List<String>> dimFeatureFilters) {

		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL), Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
				dimPipelineParameters, dimFeatureSets,dimFeatureFilters, dimClassificationArgs);
	}

	private Dimension<List<Object>> getPipelineParameters(String baseDir, String target) {
		@SuppressWarnings("unchecked")
		Dimension<List<Object>> dimPipelineParameters = Dimension.create(DIM_PIPELINE_PARAMS,
				Arrays.asList(new Object[] { NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
						NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, N_GRAM_MIN,
						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, N_GRAM_MAX, 
						HashTagDFE.PARAM_HASHTAGS_FILE_PATH,"src/main/resources/lists/targetSpecific/"+target+"/hashTags.txt",
						HashTagDFE.PARAM_VARIANT,"hashTagsAtTheEnd",
						SummedStanceDFE_staticLexicon.PARAM_USE_STANCE_LEXICON,"true",
						SummedStanceDFE_staticLexicon.PARAM_USE_HASHTAG_LEXICON, "true",
						StackedFeatureDFE.PARAM_ID2OUTCOME_FILE_PATH,"src/main/resources/ngram_stacking/favorVsAgainst/"+target+"/id2homogenizedOutcome.txt",
						ClassifiedConceptDFE.PARAM_TARGET,target
				}));
		return dimPipelineParameters;
	}

	private Map<String, Object> getDimReaders(String baseDir, File folder) {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, TaskATweetReader.class);
		dimReaders.put(DIM_READER_TRAIN_PARAMS, Arrays.asList(TaskATweetReader.PARAM_SOURCE_LOCATION, folder.getAbsolutePath(),
				TaskATweetReader.PARAM_LANGUAGE, LANGUAGE_CODE, TaskATweetReader.PARAM_PATTERNS, "*.xml"));
		return dimReaders;
	}

}
