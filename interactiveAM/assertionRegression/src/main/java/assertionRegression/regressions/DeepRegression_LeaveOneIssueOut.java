package assertionRegression.regressions;

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
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.DeepLearningConstants;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.style.AdjectiveEndingFeatureExtractor;
import org.dkpro.tc.features.syntax.POSRatioFeatureExtractor;
import org.dkpro.tc.features.syntax.QuestionsRatioFeatureExtractor;
import org.dkpro.tc.features.twitter.EmoticonRatio;
import org.dkpro.tc.features.twitter.NumberOfHashTags;
import org.dkpro.tc.ml.DeepLearningExperimentCrossValidation;
import org.dkpro.tc.ml.DeepLearningExperimentTrainTest;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentTrainTest;
import org.dkpro.tc.ml.keras.KerasAdapter;

import assertionRegression.featureExtractors.CorpusFrequency;
import assertionRegression.featureExtractors.NRCSentiment;
import assertionRegression.featureExtractors.SocherSentimentFE;
import assertionRegression.featureExtractors.WordEmbeddingDFE;
import assertionRegression.io.AssertionIssueSpecificReader;
import assertionRegression.io.AssertionIssueSpecificReaderTrainTest;
import assertionRegression.io.AssertionReader;
import assertionRegression.preprocessing.StanfordSentimentAnnotator;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.rules.ZeroR;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.BatchTrainTestReport;
import org.dkpro.tc.ml.report.ScatterplotReport;

public class DeepRegression_LeaveOneIssueOut implements Constants {

	private static final int NUM_FOLDS = 16;
	public static int WORD_N_GRAM_MIN = 1;
	public static int WORD_N_GRAM_MAX = 3;
	public static int CHAR_N_GRAM_MIN = 2;
	public static int CHAR_N_GRAM_MAX = 5;

	public static int N_GRAM_MAXCANDIDATES = 1000;

	

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
		
		ArrayList<String> issues = new ArrayList<String>(Arrays.asList(
				"Black Lives Matter"
				, "Climate Change"
				,"Creationism in school curricula"
				, "Foreign Aid", "Gender Equality"
				, "Gun Rights"
				,"Legalization of Marijuana"
				, "Legalization of Same-sex Marriage"
				, "Mandatory Vaccination"
				, "Media Bias"
				,"Obama Care -- Affordable Health Care Act"
				, "US Electoral System"
				, "US Engagement in the Middle East"
				,"US Immigration"
				, "Vegetarian & Vegan Lifestyle"
				, "War on Terrorism"
				));
		
		
		
		for(String issue: issues) {
			DeepRegression_LeaveOneIssueOut experiment = new DeepRegression_LeaveOneIssueOut();

			ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/UCI/data/data.tsv", "Agreement",issue, baseDir + "/UCI/data/wiki.en.vec",baseDir + "/regression_conv/2.py");

//			ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/UCI/data/data.tsv", "Agreement",issue, baseDir + "/UCI/data/wiki.en.vec",  "src/main/resources/kerasCode/regression_conv/A.py");
			String issueNameCleaned= cleanName(issue);
			System.out.println(issueNameCleaned);
			experiment.runTrainTest(pSpace, "issueTransfer",issueNameCleaned);
		}

	}

	private static String cleanName(String issue) {
		String result=issue.replace(" ", "");
		result=issue.replaceAll("[^A-Za-z0-9]", "");
		return result;
	}

	private void runTrainTest(ParameterSpace pSpace, String title, String issue) throws Exception {
		DeepLearningExperimentTrainTest batch = new DeepLearningExperimentTrainTest(title+""+issue,KerasAdapter.class);
        batch.setPreprocessing(getPreprocessing());
        batch.setParameterSpace(pSpace);
        batch.addReport(BatchCrossValidationReport.class);        
        batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
        batch.addReport(ScatterplotReport.class);

        // Run
        Lab.getInstance().run(batch);

	}

	protected AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)
		// ,createEngineDescription(ClearNlpPosTagger.class)
		// ,
		// createEngineDescription(StanfordSentimentAnnotator.class)
		);
	}

	private ParameterSpace setupCrossValidation(String path, String target, String issue,String embeddingPath,String pythonCode) throws ResourceInitializationException {
		Map<String, Object> dimReaders = getDimReaders(path, target,issue);
		ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT),
				Dimension.create(DIM_LEARNING_MODE, Constants.LM_REGRESSION),
				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/local/bin/python3"),
//				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_USER_CODE, pythonCode),
				Dimension.create(DeepLearningConstants.DIM_MAXIMUM_LENGTH, 50),
				Dimension.create(DeepLearningConstants.DIM_VECTORIZE_TO_INTEGER, true),
				Dimension.create(DeepLearningConstants.DIM_PRETRAINED_EMBEDDINGS, embeddingPath)

		);

		return pSpace;
	}

	private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders, Dimension<TcFeatureSet> dimFeatureSets,
			Dimension<List<String>> dimClassificationArgs) {
		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION), Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
				dimFeatureSets, dimClassificationArgs,Dimension.create( DIM_CROSS_VALIDATION_MANUAL_FOLDS, true));
	}

	private Map<String, Object> getDimReaders(String path, String targetClass, String issue) throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read " + path);
		dimReaders.put(DIM_READER_TRAIN,
				CollectionReaderFactory.createReaderDescription(AssertionIssueSpecificReaderTrainTest.class,
						AssertionIssueSpecificReaderTrainTest.PARAM_SOURCE_LOCATION, path,
						AssertionIssueSpecificReaderTrainTest.PARAM_LANGUAGE, "en",
						AssertionIssueSpecificReaderTrainTest.PARAM_TARGETCLASS, targetClass,AssertionIssueSpecificReaderTrainTest.PARAM_IS_TRAIN, true, AssertionIssueSpecificReaderTrainTest.PARAM_ISSUE, issue));
		
		dimReaders.put(DIM_READER_TEST,
				CollectionReaderFactory.createReaderDescription(AssertionIssueSpecificReaderTrainTest.class,
						AssertionIssueSpecificReaderTrainTest.PARAM_SOURCE_LOCATION, path,
						AssertionIssueSpecificReaderTrainTest.PARAM_LANGUAGE, "en",
						AssertionIssueSpecificReaderTrainTest .PARAM_TARGETCLASS, targetClass,AssertionIssueSpecificReaderTrainTest.PARAM_IS_TRAIN, false, AssertionIssueSpecificReaderTrainTest.PARAM_ISSUE, issue));

		return dimReaders;
	}

}
