package hatespeechPrediction.regression;

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
import org.dkpro.tc.features.ngram.CharacterNGram;
import org.dkpro.tc.features.ngram.WordNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.pair.core.ngram.LuceneNGramPFE;
import org.dkpro.tc.features.twitter.EmoticonRatio;
import org.dkpro.tc.features.twitter.NumberOfHashTags;
import org.dkpro.tc.ml.ExperimentCrossValidation;


import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import hatespeechPrediction.FEs.regression.ScoresOfMostSimilarAssertionsFE;
import io.AssertionReader;
import io.Id2OutcomeReport;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.rules.ZeroR;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.ScatterplotReport;


public class HateSpeechRegressionUsingSim implements Constants {

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
	

	public static void main(String[] args) throws Exception {
		
		for(int i=1; i<101; i+=25) {
			String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
			System.out.println("DKPRO_HOME: " + baseDir);
			HateSpeechRegressionUsingSim experiment = new HateSpeechRegressionUsingSim();

			TcFeatureSet featureSet = new TcFeatureSet(TcFeatureFactory.create(ScoresOfMostSimilarAssertionsFE.class,
					ScoresOfMostSimilarAssertionsFE.PARAM_USE_HS_SCORE, false,
					ScoresOfMostSimilarAssertionsFE.PARAM_N_MOST_SIMILAR, i,
					ScoresOfMostSimilarAssertionsFE.PARAM_USE_GOLD, true,
					ScoresOfMostSimilarAssertionsFE.PARAM_PATH2_SCORES_FILE, "src/main/resources/scores/scores.tsv",
					ScoresOfMostSimilarAssertionsFE.PARAM_ID2OUTCOME_FILE,
					"src/main/resources/similarityPredicted/snn:id2Outcome_translated_2.txt")
//					,
//					TcFeatureFactory.create(WordNGram.class, WordNGram.PARAM_NGRAM_USE_TOP_K, N_GRAM_MAXCANDIDATES,
//							WordNGram.PARAM_NGRAM_MIN_N, WORD_N_GRAM_MIN, WordNGram.PARAM_NGRAM_MAX_N, WORD_N_GRAM_MAX),
//					TcFeatureFactory.create(CharacterNGram.class, CharacterNGram.PARAM_NGRAM_USE_TOP_K,
//							N_GRAM_MAXCANDIDATES, CharacterNGram.PARAM_NGRAM_MIN_N, CHAR_N_GRAM_MIN,
//							CharacterNGram.PARAM_NGRAM_MAX_N, CHAR_N_GRAM_MAX)
					);
			
			
			// XXX CV for getting the id2outcome file for the DFE
			ParameterSpace pSpace = experiment.setupCrossValidation("src/main/resources/scores/scores.tsv", "HateSpeech",featureSet);
			experiment.runCrossValidation(pSpace, "hateSpec_AD_snn_gold"+String.valueOf(i));
		}
		

	}

	private void runCrossValidation(ParameterSpace pSpace, String title) throws Exception {
		ExperimentCrossValidation batch = new ExperimentCrossValidation(title, NUM_FOLDS);

		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		batch.addReport(BatchCrossValidationReport.class);
	    batch.addReport(Id2OutcomeReport.class);
	    batch.addReport(ScatterplotReport.class);

		// Run
		Lab.getInstance().run(batch);

	}

	protected AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)
				);
	}

	private ParameterSpace setupCrossValidation(String path, String target, TcFeatureSet featureSet) throws ResourceInitializationException {
		Map<String, Object> dimReaders = getDimReaders(path, target);
//		Dimension<List<Object>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
//                Arrays.asList(
//                        new Object[] { new LibsvmAdapter(), "-s", "4" , "-c", "100"}));
		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);

		Map<String, Object> config1 = new HashMap<>();
		config1.put(DIM_CLASSIFICATION_ARGS,new Object[] { new LibsvmAdapter(),  "-s", "3" , "-c", "100" });
		config1.put(DIM_DATA_WRITER, new LibsvmAdapter().getDataWriterClass());
		config1.put(DIM_FEATURE_USE_SPARSE, new LibsvmAdapter().useSparseFeatures());
		
		
		Dimension<Map<String, Object>> mlas = Dimension.createBundle("config1", config1);
		
		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, mlas);

		return pSpace;
	}

	private ParameterSpace bundleParameterSpace(Map<String, Object> dimReaders, Dimension<TcFeatureSet> dimFeatureSets,
			Dimension<Map<String, Object>> mlas) {
		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION), Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT),
				dimFeatureSets, mlas);
	}

	private Map<String, Object> getDimReaders(String path, String targetClass) throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read " + path);
		dimReaders.put(DIM_READER_TRAIN,
				CollectionReaderFactory.createReaderDescription(AssertionReader.class,
						AssertionReader.PARAM_SOURCE_LOCATION, path, AssertionReader.PARAM_LANGUAGE, "de",
						AssertionReader.PARAM_TARGETCLASS, targetClass));

		return dimReaders;
	}

}
