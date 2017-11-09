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
import org.dkpro.tc.features.length.NrOfTokens;
import org.dkpro.tc.features.ngram.LuceneCharacterNGram;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.LucenePOSNGram;
import org.dkpro.tc.features.ngram.LucenePhoneticNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.style.TypeTokenRatioFeatureExtractor;
import org.dkpro.tc.features.syntax.SuperlativeRatioFeatureExtractor;
import org.dkpro.tc.features.twitter.EmoticonRatio;
import org.dkpro.tc.features.twitter.NumberOfHashTags;
import org.dkpro.tc.features.window.CaseExtractor;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;
import org.dkpro.tc.ml.weka.WekaRegressionAdapter;

import assertionRegression.featureExtractors.CorpusFrequency;
import assertionRegression.featureExtractors.SocherSentimentFE;
import assertionRegression.featureExtractors.WordEmbeddingDFE;
import assertionRegression.io.AssertionReader;
import assertionRegression.io.CrossValidationReport;
import assertionRegression.preprocessing.StanfordSentimentAnnotator;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.rules.ZeroR;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class AgreementRegression implements Constants {

	private static final int NUM_FOLDS = 10;
	public static int WORD_N_GRAM_MIN = 1;
	public static int WORD_N_GRAM_MAX = 3;
	public static int CHAR_N_GRAM_MIN = 2;
	public static int CHAR_N_GRAM_MAX = 5;

	public static int N_GRAM_MAXCANDIDATES = 1000;

	/**
	 * XXX specify features here we should do some experiments using the
	 * standard fetaures from dkpro-tc
	 */
	public TcFeatureSet featureSet = new TcFeatureSet(
			// TcFeatureFactory.create(LuceneNGram.class,
			// NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
			// N_GRAM_MAXCANDIDATES,
			// NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN,
			// NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX)
			// ,TcFeatureFactory.create(LuceneCharacterNGram.class,
			// NGramFeatureExtractorBase.PARAM_NGRAM_USE_TOP_K,
			// N_GRAM_MAXCANDIDATES,
			// NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
			// NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
			// TcFeatureFactory.create(WordEmbeddingDFE.class,WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION,
			// "/Users/michael/git/lang-tech-teaching/de.unidue.kbs/src/main/resources/GloVe/glove.twitter.27B.200d.txt")
			TcFeatureFactory.create(NrOfTokens.class),
			TcFeatureFactory.create(CorpusFrequency.class,CorpusFrequency.PARAM_CORPUS_FOLDER,"/Users/michael/Desktop/statuses")
	// TcFeatureFactory.create(SocherSentimentFE.class)
	);

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		AgreementRegression experiment = new AgreementRegression();

		// XXX CV for getting the id2outcome file for the DFE
		ParameterSpace pSpace = experiment.setupCrossValidation("src/main/resources/data/data.tsv", "Agreement");
		experiment.runCrossValidation(pSpace, "test");

	}

	private void runCrossValidation(ParameterSpace pSpace, String title) throws Exception {
		ExperimentCrossValidation batch = new ExperimentCrossValidation(title, WekaRegressionAdapter.class, NUM_FOLDS);

		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		batch.addReport(CrossValidationReport.class);

		// Run
		Lab.getInstance().run(batch);

	}

	protected AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)
//				,
//				createEngineDescription(StanfordSentimentAnnotator.class)
				);
	}

	private ParameterSpace setupCrossValidation(String path, String target) throws ResourceInitializationException {
		Map<String, Object> dimReaders = getDimReaders(path, target);
		Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
				asList(new String[] { SMOreg.class.getName() })
		// asList(new String[] { ZeroR.class.getName() })
		);

		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);

		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, dimClassificationArgs);

		return pSpace;
	}

	private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders, Dimension<TcFeatureSet> dimFeatureSets,
			Dimension<List<String>> dimClassificationArgs) {
		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION), Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
				dimFeatureSets, dimClassificationArgs);
	}

	private Map<String, Object> getDimReaders(String path, String targetClass) throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read " + path);
		dimReaders.put(DIM_READER_TRAIN,
				CollectionReaderFactory.createReaderDescription(AssertionReader.class,
						AssertionReader.PARAM_SOURCE_LOCATION, path, AssertionReader.PARAM_LANGUAGE, "en",
						AssertionReader.PARAM_TARGETCLASS, targetClass));

		return dimReaders;
	}

}
