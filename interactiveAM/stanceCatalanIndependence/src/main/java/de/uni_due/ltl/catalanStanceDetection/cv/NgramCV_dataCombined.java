package de.uni_due.ltl.catalanStanceDetection.cv;

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
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.TcFeature;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.util.ExperimentUtil;
import org.dkpro.tc.features.ngram.LuceneCharacterNGram;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.fstore.filter.UniformClassDistributionFilter;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentSaveModel;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;
import org.springframework.util.Log4jConfigurer;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader_BothLangs;
import de.uni_due.ltl.catalanStanceDetection.io.ConfusionMatrixOutput;
import de.uni_due.ltl.catalanStanceDetection.io.CrossValidationReport;
import de.uni_due.ltl.catalanStanceDetection.wordembeddings.WordEmbeddingDFE;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;

public class NgramCV_dataCombined implements Constants{
	/**
	 * XXX CONSTANTS
	 */
	public static final String LANGUAGE_CODE = "es";
	private static final int NUM_FOLDS = 3;

	private boolean ablation = false;

	public static TcFeatureSet featureSet = new TcFeatureSet(
			TcFeatureFactory.create(LuceneNGram.class,LuceneNGram.PARAM_NGRAM_MAX_N,3,LuceneNGram.PARAM_NGRAM_MIN_N,1, LuceneNGram.PARAM_NGRAM_USE_TOP_K,1000),
			TcFeatureFactory.create(LuceneCharacterNGram.class,LuceneCharacterNGram.PARAM_NGRAM_MAX_N,4,LuceneCharacterNGram.PARAM_NGRAM_MIN_N,2, LuceneCharacterNGram.PARAM_NGRAM_USE_TOP_K,1000)
//			,
//			TcFeatureFactory.create(WordEmbeddingDFE.class, WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION, "src/main/resources/prunedEmbeddings_wiki."+LANGUAGE_CODE+".vec")
			//			,
//			TcFeatureFactory.create(WordEmbeddingDFE.class, WordEmbeddingDFE.PARAM_WORDEMBEDDINGLOCATION, "src/main/resources/"+LANGUAGE_CODE+".polyglot.txt")

	);

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		NgramCV_dataCombined experiment = new NgramCV_dataCombined();
		ParameterSpace pSpace_explicit = experiment.setupCrossValidation(baseDir + "/IberEval/", featureSet);
		experiment.runCrossValidation(pSpace_explicit, "both");
		}


	private static String getValidName(String experimentName) {
		experimentName = experimentName.replace(" ", "");
		experimentName = experimentName.replace(".", "");
		experimentName = experimentName.replace(",", "");
		experimentName = experimentName.replace("!", "");
		experimentName = experimentName.replace("(", "");
		experimentName = experimentName.replace(")", "");
		experimentName = experimentName.replace(";", "");
		experimentName = experimentName.replace(":", "");
		experimentName = experimentName.replace("’", "");
		experimentName.replaceAll("[^a-zA-Z]+", "");
		return experimentName;
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
	public void runCrossValidation(ParameterSpace pSpace, String experimentName) throws Exception {
		ExperimentCrossValidation batch = new ExperimentCrossValidation(experimentName, WekaClassificationAdapter.class,
				NUM_FOLDS);

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
	 * @param dataLocation
	 * @param target
	 * @param targetSet
	 * @param featureSet
	 * @return
	 * @throws ResourceInitializationException
	 */
	@SuppressWarnings("unchecked")
	public ParameterSpace setupCrossValidation(String dataLocation,TcFeatureSet featureSet) throws ResourceInitializationException {
		// configure reader dimension
		Map<String, Object> dimReaders = getDimReaders(dataLocation);
		Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
				asList(new String[] { SMO.class.getName() }));
//				asList(new String[] { RandomForest.class.getName() }));
//				asList(new String[] { Logistic.class.getName() }));

		Dimension<TcFeatureSet> dimFeatureSets = null;
		if (ablation) {
			dimFeatureSets = ExperimentUtil.getAblationTestFeatures(featureSet.toArray(new TcFeature[0]));
		} else {
			dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);
		}

		// bundle parameterspace
		ParameterSpace pSpace = bundleParameterSpace(dimReaders, dimFeatureSets, dimClassificationArgs);

		return pSpace;
	}

	private Map<String, Object> getDimReaders(String dir)
			throws ResourceInitializationException {
		String inputTrainFolder = dir;
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read from " + inputTrainFolder );
		dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(CatalanStanceReader_BothLangs.class, CatalanStanceReader_BothLangs.PARAM_SOURCE_LOCATION, dir+"/both/train/*",CatalanStanceReader_BothLangs.PARAM_LABEL_FILE,dir+"/both/truth"));

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
		return new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL), Dimension.create(DIM_FEATURE_MODE, FM_UNIT),
				dimFeatureSets, dimClassificationArgs);
	}

	private AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(ArktweetTokenizer.class));
	}

}
