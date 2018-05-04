package hatespeechPrediction.regression;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.DeepLearningConstants;
import org.dkpro.tc.features.pair.core.ngram.LuceneNGramPFE;
import org.dkpro.tc.ml.DeepLearningExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.keras.KerasAdapter;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.ScatterplotReport;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramContainmentResource;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramJaccardResource;
import dkpro.similarity.algorithms.lexical.uima.string.GreedyStringTilingMeasureResource;
import io.AssertionSimilarityPairReader;
import io.AssertionSimilarityPairReader_deep;
import io.Id2OutcomeReport;

public class AssertionSimilarityPrediction_deep implements Constants {
	public static final String languageCode = "de";

	private static final int NUM_FOLDS = 10;

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);

		ParameterSpace pSpace = getParameterSpace(baseDir + "/hateSpeechGender/similarity_gold/matrix.tsv",
				baseDir + "/hateSpeechGender/embeddings/de.polyglot.txt", baseDir + "/hateSpeechGender/kerasCode/cosinus.py");
		AssertionSimilarityPrediction_deep experiment = new AssertionSimilarityPrediction_deep();
		experiment.runCrossValidation(pSpace, "Similarity");

	}

	@SuppressWarnings("unchecked")
	public static ParameterSpace getParameterSpace(String path, String embeddingPath, String pythonCode)
			throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				AssertionSimilarityPairReader_deep.class, AssertionSimilarityPairReader_deep.PARAM_SOURCE_LOCATION,
				path);
		dimReaders.put(DIM_READER_TRAIN, reader);

		ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT),
				Dimension.create(DIM_LEARNING_MODE, Constants.LM_REGRESSION),
//				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/local/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION,"/usr/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_USER_CODE, pythonCode),
				Dimension.create(DeepLearningConstants.DIM_VECTORIZE_TO_INTEGER, false),
				Dimension.create(DeepLearningConstants.DIM_PRETRAINED_EMBEDDINGS, embeddingPath));

		return pSpace;
	}

	protected void runCrossValidation(ParameterSpace pSpace, String title) throws Exception {

		DeepLearningExperimentCrossValidation batch = new DeepLearningExperimentCrossValidation("sim_" + title,
				KerasAdapter.class, NUM_FOLDS);
		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		// batch.addReport(LibsvmOutcomeIdReport.class);

		batch.addReport(BatchCrossValidationReport.class);
		// batch.addReport(BatchCrossValidationReport.class);

		batch.addReport(Id2OutcomeReport.class);
		batch.addReport(ScatterplotReport.class);

		// Run
		Lab.getInstance().run(batch);

	}

	protected AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class));
	}
}
