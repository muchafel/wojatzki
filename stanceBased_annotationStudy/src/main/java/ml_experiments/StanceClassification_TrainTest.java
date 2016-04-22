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
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.ngram.LuceneCharacterNGramDFE;
import org.dkpro.tc.features.ngram.LuceneNGramDFE;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.fstore.filter.UniformClassDistributionFilter;
import org.dkpro.tc.ml.ExperimentSaveModel;
import org.dkpro.tc.ml.ExperimentTrainTest;
import org.dkpro.tc.ml.report.BatchCrossValidationUsingTCEvaluationReport;
import org.dkpro.tc.ml.report.BatchTrainTestDetailedOutcomeReport;
import org.dkpro.tc.ml.report.BatchTrainTestUsingTCEvaluationReport;
import org.dkpro.tc.weka.WekaClassificationAdapter;
import org.dkpro.tc.weka.WekaClassificationUsingTCEvaluationAdapter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import edu.stanford.nlp.util.ConfusionMatrix;
import io.ConfusionMatrixOutput;
import io.SubStanceReader;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

public class StanceClassification_TrainTest implements Constants {

	private static ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList("secularism", "Same-sex marriage",
			"religious_freedom", "Conservative_Movement", "Freethinking", "Islam", "No_evidence_for_religion", "USA",
			"Supernatural_Power_Being", "Life_after_death", "Christianity"));
	
	public static final String LANGUAGE_CODE = "en";
	public static final String TARGET_LABLE = "ATHEISM"; //,67
//	public static final String TARGET_LABLE = "Original_Stance"; //need to get that info from original xmls
//	public static final String TARGET_LABLE = "Supernatural_Power_Being"; //.76
//	public static final String TARGET_LABLE = "Christianity"; //.8
//	public static final String TARGET_LABLE = "Freethinking"; // XX
//	public static final String TARGET_LABLE = "Islam";  // ,95
//	public static final String TARGET_LABLE = "Life_after_death";  // ,97
//	public static final String TARGET_LABLE = "No_evidence_for_religion"; // XX
//	public static final String TARGET_LABLE = "religious_freedom"; //XX
//	public static final String TARGET_LABLE = "USA"; //XX
//	public static final String TARGET_LABLE = "secularism";
//	public static final String TARGET_LABLE = "Same-sex marriage";

//	private static final String FilteringPostfix = "_wo_irony_understandability";
	private static final String FilteringPostfix = "";

	private static final String modelOutputFolder = "src/main/resources/models";
	
	public static int N_GRAM_MIN=1;
	public static int N_GRAM_MAX=3;
	public static int N_GRAM_MAXCANDIDATES=1000;
	
	public static String[] FES={
			StackedNGramAnnotatorDFE.class.getName(),
//		    LuceneNGramDFE.class.getName(),
//		    LuceneCharacterNGramDFE.class.getName(),
		    OracleSubTargetDFE.class.getName(),
//		    ClassifiedSubTargetDFE.class.getName()
		    };
	
	
	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: "+ baseDir);
		StanceClassification_TrainTest experiment = new StanceClassification_TrainTest();

		ParameterSpace pSpace = experiment.setup(baseDir,TARGET_LABLE);
		experiment.runTrainTest(pSpace, "stanceExperiment");
//		for(String subTarget: subTargets){
//			experiment.saveModel(experiment.setup(baseDir,subTarget), subTarget,subTarget.replace(" ", "")+"_saveModel");
//		}
	}

	
	@SuppressWarnings("unchecked")
	private ParameterSpace setup(String baseDir, String subTarget) {
		// configure reader dimension
		Map<String, Object> dimReaders= getDimReaders(baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/train"+FilteringPostfix,baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/test"+FilteringPostfix, subTarget);
        
        Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
                asList(new String[] { SMO.class.getName()})
//                ,
//                asList(new String[] { RandomForest.class.getName() })
                );
        Dimension<List<Object>> dimPipelineParameters= getPipelineParameters(baseDir);
        
        Dimension<List<String>> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, Arrays.asList(FES));
       
        //bundle parameterspace
        ParameterSpace pSpace= bundleParameterSpace(dimReaders,dimPipelineParameters,dimFeatureSets,dimClassificationArgs);
        
		return pSpace;
	}
	
	
	
	
	private Map<String, Object> getDimReaders(String trainDir, String testDir, String subTarget) {
		String inputTrainFolder = trainDir;
		String inputTestFolder = testDir;
		Map<String, Object> dimReaders = new HashMap<String, Object>();
	
			dimReaders.put(DIM_READER_TRAIN, SubStanceReader.class);
			dimReaders.put(DIM_READER_TRAIN_PARAMS, Arrays.asList(
					SubStanceReader.PARAM_SOURCE_LOCATION, inputTrainFolder,
					SubStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE,
					SubStanceReader.PARAM_PATTERNS, "*.bin",
					SubStanceReader.PARAM_TARGET_LABEL, subTarget));
			
			dimReaders.put(DIM_READER_TEST, SubStanceReader.class);
			dimReaders.put(DIM_READER_TEST_PARAMS, Arrays.asList(
					SubStanceReader.PARAM_SOURCE_LOCATION, inputTestFolder,
					SubStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE,
					SubStanceReader.PARAM_PATTERNS, "*.bin",
					SubStanceReader.PARAM_TARGET_LABEL, subTarget));
		return dimReaders;
	}


	/**
	 * bundle paramterSpace (implement feature Selection here)
	 * @param dimReaders
	 * @param dimPipelineParameters
	 * @param dimFeatureSets
	 * @param dimClassificationArgs
	 * @param dimFeatureFilters 
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
                		StackedNGramAnnotatorDFE.PARAM_ID2OUTCOME_FILE_PATH,"src/main/resources/lists/id2outcome.txt"
                })
        );
		return dimPipelineParameters;
	}

	private void runTrainTest(ParameterSpace pSpace, String experimentName) throws Exception {
		ExperimentTrainTest batch = new ExperimentTrainTest(
				experimentName, WekaClassificationUsingTCEvaluationAdapter.class);
		
		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		batch.addInnerReport(ConfusionMatrixOutput.class);
		batch.addReport(BatchTrainTestUsingTCEvaluationReport.class);
		batch.addReport(BatchTrainTestDetailedOutcomeReport.class);
		
		
		// Run
		Lab.getInstance().run(batch);
		
	}


	private AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(ArktweetTokenizer.class),createEngineDescription(Stacked_SubTargetClassification.class));
	}

	private void saveModel(ParameterSpace pSpace, String subtarget, String experimentName) throws Exception {
		ExperimentSaveModel batch = new ExperimentSaveModel(
				experimentName, WekaClassificationUsingTCEvaluationAdapter.class, new File(modelOutputFolder+"/"+subtarget));
		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);

		// Run
		Lab.getInstance().run(batch);
	}
	
	
}
