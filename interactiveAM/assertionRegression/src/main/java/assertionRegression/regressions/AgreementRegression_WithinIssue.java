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
import org.dkpro.tc.features.ngram.WordNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.style.AdjectiveEndingFeatureExtractor;
import org.dkpro.tc.features.syntax.POSRatioFeatureExtractor;
import org.dkpro.tc.features.syntax.QuestionsRatioFeatureExtractor;
import org.dkpro.tc.features.twitter.EmoticonRatio;
import org.dkpro.tc.features.twitter.NumberOfHashTags;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentTrainTest;

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
import org.dkpro.tc.ml.report.InnerBatchReport;
import org.dkpro.tc.ml.report.ScatterplotReport;

public class AgreementRegression_WithinIssue implements Constants {

	private static final int NUM_FOLDS = 70;
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
//				, "Climate Change"
//				,"Creationism in school curricula"
//				, "Foreign Aid", "Gender Equality"
//				, "Gun Rights"
//				,"Legalization of Marijuana"
//				, "Legalization of Same-sex Marriage"
//				, "Mandatory Vaccination"
//				, "Media Bias"
//				,"Obama Care -- Affordable Health Care Act"
//				, "US Electoral System"
//				, "US Engagement in the Middle East"
//				,"US Immigration"
//				, "Vegetarian & Vegan Lifestyle"
//				, "War on Terrorism"
				));
		
		
		TcFeatureSet featureSet = new TcFeatureSet(
				TcFeatureFactory.create(WordNGram.class, NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
						N_GRAM_MAXCANDIDATES, NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
						NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),

				TcFeatureFactory.create(NRCSentiment.class, NRCSentiment.PARAM_PREDICTION_FILE_CLASSES,
						baseDir + "/UCI/sentimentPredictions/assertions-preds.txt",
						NRCSentiment.PARAM_PREDICTION_FILE_SCORES,
						baseDir + "/UCI/sentimentPredictions/assertions-preds-scores.txt"));
		
		for(String issue: issues) {
			AgreementRegression_WithinIssue experiment = new AgreementRegression_WithinIssue();

			ParameterSpace pSpace = experiment.setupCrossValidation(baseDir + "/UCI/data/data.tsv", "Agreement",featureSet,issue);
			String issueNameCleaned= cleanName(issue);
			System.out.println(issueNameCleaned);
			experiment.runCrossValidation(pSpace, "issueInnerCrossValidation_"+NUM_FOLDS+"_",issueNameCleaned);
		}

	}

	private static String cleanName(String issue) {
		String result=issue.replace(" ", "");
		result=issue.replaceAll("[^A-Za-z0-9]", "");
		return result;
	}

	private void runCrossValidation(ParameterSpace pSpace, String title, String issue) throws Exception {
		ExperimentCrossValidation batch = new ExperimentCrossValidation(title+""+issue,NUM_FOLDS);

		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		batch.addReport(BatchCrossValidationReport.class);
		batch.addReport(ScatterplotReport.class);
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

	private ParameterSpace setupCrossValidation(String path, String target, TcFeatureSet featureSet, String issue) throws ResourceInitializationException {
		Map<String, Object> dimReaders = getDimReaders(path, target,issue);
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

	private Map<String, Object> getDimReaders(String path, String targetClass, String issue) throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read " + path);
		dimReaders.put(DIM_READER_TRAIN,
				CollectionReaderFactory.createReaderDescription(AssertionIssueSpecificReaderTrainTest.class,
						AssertionIssueSpecificReaderTrainTest.PARAM_SOURCE_LOCATION, path,
						AssertionIssueSpecificReaderTrainTest.PARAM_LANGUAGE, "en",
						AssertionIssueSpecificReaderTrainTest.PARAM_TARGETCLASS, targetClass,AssertionIssueSpecificReaderTrainTest.PARAM_IS_TRAIN, false, AssertionIssueSpecificReaderTrainTest.PARAM_ISSUE, issue));
		

		return dimReaders;
	}

}
