package assertionRegression.regressions;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.ngram.WordNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.ml.ExperimentTrainTest;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.BatchTrainTestReport;

import assertionRegression.featureExtractors.NRCSentiment;
import assertionRegression.io.AssertionIssueSpecificReaderTrainTest;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class AgreementRegression_IssueTransfer implements Constants {

	private static final int NUM_FOLDS = 10;
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
		
		
		TcFeatureSet featureSet = new TcFeatureSet(
				TcFeatureFactory.create(WordNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
						N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),

				TcFeatureFactory.create(NRCSentiment.class, NRCSentiment.PARAM_PREDICTION_FILE_CLASSES,
						baseDir + "/UCI/sentimentPredictions/assertions-preds.txt",
						NRCSentiment.PARAM_PREDICTION_FILE_SCORES,
						baseDir + "/UCI/sentimentPredictions/assertions-preds-scores.txt"));
		for(String issueA: issues) {
			for(String issueB: issues) {
				if(issueA.equals(issueB))continue;
				AgreementRegression_IssueTransfer experiment = new AgreementRegression_IssueTransfer();

				ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/UCI/data/data.tsv", "Agreement",featureSet,issueA,issueB);
				String issueNameACleaned= cleanName(issueA);
				String issueNameBCleaned= cleanName(issueB);
				System.out.println(issueNameACleaned+ " "+issueNameBCleaned);
				experiment.runTrainTest(pSpace, "issueTransfer_",issueNameACleaned+ "_to_"+issueNameBCleaned);
			}
		}

	}

	private static String cleanName(String issue) {
		String result=issue.replace(" ", "");
		result=issue.replaceAll("[^A-Za-z0-9]", "");
		return result;
	}

	private void runTrainTest(ParameterSpace pSpace, String title, String issue) throws Exception {
		ExperimentTrainTest batch = new ExperimentTrainTest(title+""+issue);

		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		batch.addReport(BatchTrainTestReport.class);
//		batch.addReport(BatchCrossValidationReport.class);
//		batch.addReport(ScatterplotReport.class);
//		 batch.addReport(FoldReport.class);

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

	private ParameterSpace setupCrossValidation(String path, String target, TcFeatureSet featureSet, String issueA, String issueB) throws ResourceInitializationException {
		Map<String, Object> dimReaders = getDimReaders(path, target,issueA, issueB);
		Dimension<List<Object>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
                Arrays.asList(
                        new Object[] { new LibsvmAdapter(), "-s", "4" , "-c", "100"}));
		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);

		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, dimClassificationArgs);

		return pSpace;
	}

	private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders, Dimension<TcFeatureSet> dimFeatureSets,
			Dimension<List<Object>> dimClassificationArgs) {
		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION), Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
				dimFeatureSets, dimClassificationArgs,Dimension.create( DIM_CROSS_VALIDATION_MANUAL_FOLDS, true));
	}

	private Map<String, Object> getDimReaders(String path, String targetClass, String issueA, String issueB) throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read " + path);
		dimReaders.put(DIM_READER_TRAIN,
				CollectionReaderFactory.createReaderDescription(AssertionIssueSpecificReaderTrainTest.class,
						AssertionIssueSpecificReaderTrainTest.PARAM_SOURCE_LOCATION, path,
						AssertionIssueSpecificReaderTrainTest.PARAM_LANGUAGE, "en",
						AssertionIssueSpecificReaderTrainTest.PARAM_TARGETCLASS, targetClass,AssertionIssueSpecificReaderTrainTest.PARAM_IS_TRAIN, false, AssertionIssueSpecificReaderTrainTest.PARAM_ISSUE, issueA));
		
		dimReaders.put(DIM_READER_TEST,
				CollectionReaderFactory.createReaderDescription(AssertionIssueSpecificReaderTrainTest.class,
						AssertionIssueSpecificReaderTrainTest.PARAM_SOURCE_LOCATION, path,
						AssertionIssueSpecificReaderTrainTest.PARAM_LANGUAGE, "en",
						AssertionIssueSpecificReaderTrainTest .PARAM_TARGETCLASS, targetClass,AssertionIssueSpecificReaderTrainTest.PARAM_IS_TRAIN, false, AssertionIssueSpecificReaderTrainTest.PARAM_ISSUE, issueB));

		return dimReaders;
	}

}
