package conceptPolarityClassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.CharUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.LuceneCharacterNGramDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.LuceneNGramDFE;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import de.tudarmstadt.ukp.dkpro.tc.ml.ExperimentCrossValidation;
import de.tudarmstadt.ukp.dkpro.tc.ml.report.BatchCrossValidationUsingTCEvaluationReport;
import de.tudarmstadt.ukp.dkpro.tc.weka.WekaClassificationUsingTCEvaluationAdapter;
import featureExtractors.HashTagDFE;
import featureExtractors.SimpleNegationDFE;
import featureExtractors.sentiment.SimpleSentencePolarityDFE;
import featureExtractors.stanceLexicon.SummedStanceDFE_staticLexicon;
import io.ConfusionMatrixOutput;
import io.TaskATweetReader;
import util.PreprocessingPipeline;
import util.SimilarityHelper;
import util.concepts.ConceptUtils;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.task.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;

public class ConceptPolarityClassification_Experiment implements Constants {

	public static final String LANGUAGE_CODE = "en";
	public static final int NUM_FOLDS = 5;
	public static final String TOPIC_FOLDERS = "/semevalTask6/targets/";
	public static int N_GRAM_MIN = 1;
	public static int N_GRAM_MAX = 3;
	public static int N_GRAM_MAXCANDIDATES = 200;
	public static AnalysisEngineDescription preProcessing;

	public static String[] FES = {
			// ContexmDFE.class.getName(),
			LuceneNGramDFE.class.getName(),
			SimpleSentencePolarityDFE.class.getName(), 
			SimpleNegationDFE.class.getName(),
			};

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		preProcessing = PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno();

		for (File folder : getTopicFolders(baseDir + TOPIC_FOLDERS)) {

			List<String> stopwords=init("src/main/resources/lists/stop-words_english_6_en.txt");
			
			Set<String> concepts = ConceptUtils.getConcepts(folder,10,stopwords);
			concepts=removeStrictlyPolarConcepts(concepts,folder);
			
			System.out.println("Normalized: "+concepts);
			for(String concept: concepts){
				System.out.println("experiments for " + folder.getName() +" "+concept+"_ConceptPolarityClassification");
				ConceptPolarityClassification_Experiment experiment = new ConceptPolarityClassification_Experiment();
				ParameterSpace pSpace = experiment.setup(baseDir, folder,concept);
				experiment.runCrossValidation(pSpace, folder.getName() +"_"+ removeSpecialChars(concept) +"_ConceptPolarityClassification");
			}
		}

	}

	private static Set<String> removeStrictlyPolarConcepts(Set<String> concepts, File folder) {
		Set<String> removed= new HashSet<String>(); 
		System.out.println(concepts);
		for(String concept: concepts){
			if(!ConceptUtils.getStrictlyPolarConcepts(folder.getName()).contains(concept)){
				removed.add(concept);
			}
		}
		System.out.println(removed);
		return removed;
	}

	private static String removeSpecialChars(String concept) {
		concept=concept.replace("@", "");
		concept=concept.replace("#", "");
		concept=concept.replace("~", "");
		return concept;
	}

	/**
	 * read in a file and return a list of strings
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected static List<String> init(String path) throws IOException {
		List<String> stopwords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
		}
		return stopwords;
	}

	private static List<File> getTopicFolders(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<File> folders = new ArrayList<File>();
		for (File f : listOfFiles) {
//			if (!f.getName().equals("HillaryClinton")) {
//				continue;
//			}
			if (!f.getName().equals("ClimateChangeisaRealConcern")) {
				continue;
			}
//			if (!f.getName().equals("Atheism")) {
//				continue;
//			}
//			if (!f.getName().equals("FeministMovement")) {
//				continue;
//			}
//			if (!f.getName().equals("LegalizationofAbortion")) {
//				continue;
//			}
			if (f.isDirectory())
				folders.add(f);
		}
		return folders;
	}

	private void runCrossValidation(ParameterSpace pSpace, String experimentName) throws Exception {

		ExperimentCrossValidation batch = new ExperimentCrossValidation(experimentName,
				WekaClassificationUsingTCEvaluationAdapter.class, NUM_FOLDS);
		batch.setPreprocessing(preProcessing);
		// batch.addInnerReport(WekaClassificationReport.class);
		// batch.addInnerReport(WekaFeatureValuesReport.class);
		batch.addInnerReport(ConfusionMatrixOutput.class);
		// batch.addInnerReport(InnerBatchUsingTCEvaluationReport.class);
		// batch.addInnerReport(WekaOutcomeIDReport.class);
		batch.setParameterSpace(pSpace);

		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		// batch.addReport(BatchCrossValidationReport.class);
		batch.addReport(BatchCrossValidationUsingTCEvaluationReport.class);
		// batch.addReport(BatchTrainTestUsingTCEvaluationReport.class);

		// Run
		Lab.getInstance().run(batch);

	}

	@SuppressWarnings("unchecked")
	private ParameterSpace setup(String baseDir, File folder,String concept) {
		// configure reader dimension
		Map<String, Object> dimReaders = getDimReaders(baseDir, folder,concept);
		// add/configure classifiers
		Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
				Arrays.asList(new String[] {
						 J48.class.getName(),
//						SMO.class.getName(),
//						MultilayerPerceptron.class.getName(),
				// ZeroR.class.getName()
		}));

		Dimension<List<Object>> dimPipelineParameters = getPipelineParameters(baseDir, folder.getName());

		Dimension<List<String>> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, Arrays.asList(FES));
		// bundle parameterspace
		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimPipelineParameters, dimFeatureSets,
				dimClassificationArgs);

		return pSpace;
	}

	/**
	 * bundle paramterSpace (implement feature Selection here)
	 * 
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
				Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL), Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
				dimPipelineParameters, dimFeatureSets, dimClassificationArgs);
	}

	private Dimension<List<Object>> getPipelineParameters(String baseDir, String target) {
		@SuppressWarnings("unchecked")
		Dimension<List<Object>> dimPipelineParameters = Dimension.create(DIM_PIPELINE_PARAMS,
				Arrays.asList(new Object[] { NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
						NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, N_GRAM_MIN,
						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, N_GRAM_MAX, HashTagDFE.PARAM_HASHTAGS_FILE_PATH,
						"src/main/resources/lists/targetSpecific/" + target + "/hashTags.txt", HashTagDFE.PARAM_VARIANT,
						"hashTagsAtTheEnd", SummedStanceDFE_staticLexicon.PARAM_USE_STANCE_LEXICON, "true",
						SummedStanceDFE_staticLexicon.PARAM_USE_HASHTAG_LEXICON, "true" }));
		return dimPipelineParameters;
	}

	private Map<String, Object> getDimReaders(String baseDir, File folder,String concept) {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, TaskAConceptReaderTweetReader.class);
		dimReaders.put(DIM_READER_TRAIN_PARAMS,
				Arrays.asList(TaskAConceptReaderTweetReader.PARAM_SOURCE_LOCATION, folder.getAbsolutePath(),
						TaskAConceptReaderTweetReader.PARAM_LANGUAGE, LANGUAGE_CODE, TaskAConceptReaderTweetReader.PARAM_PATTERNS, "*.xml",
						TaskAConceptReaderTweetReader.PARAM_CONCEPT,concept));
		return dimReaders;
	}

}
