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
import org.dkpro.tc.features.pair.core.ngram.LuceneNGramPFE;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.ScatterplotReport;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramContainmentResource;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramJaccardResource;
import dkpro.similarity.algorithms.lexical.uima.string.GreedyStringTilingMeasureResource;
import io.AssertionSimilarityPairReader;
import io.Id2OutcomeReport;

public class AssertionSimilarityPrediction implements Constants {
    public static final String languageCode = "de";


	private static final int NUM_FOLDS = 10;
    
    
    public static void main(String[] args)throws Exception {
    	String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
    	
		
		
		TcFeatureSet featureSet = new TcFeatureSet(
				TcFeatureFactory.create(LuceneNGramPFE.class,
				LuceneNGramPFE.PARAM_NGRAM_MIN_N, 4, LuceneNGramPFE.PARAM_NGRAM_MAX_N, 4,
				LuceneNGramPFE.PARAM_USE_VIEW1_NGRAMS_AS_FEATURES, true,
				LuceneNGramPFE.PARAM_USE_VIEW2_NGRAMS_AS_FEATURES, true,
				LuceneNGramPFE.PARAM_USE_VIEWBLIND_NGRAMS_AS_FEATURES, false)
				);
        
		
			ParameterSpace pSpace = getParameterSpace(featureSet, baseDir+"/hateSpeechGender/similarity_gold/matrix.tsv");
			AssertionSimilarityPrediction experiment = new AssertionSimilarityPrediction();
			experiment.runCrossValidation(pSpace, "Similarity");
		
    }

    @SuppressWarnings("unchecked")
    public static ParameterSpace getParameterSpace(TcFeatureSet featureSet, String path)
        throws ResourceInitializationException
    {
        Map<String, Object> dimReaders = new HashMap<String, Object>();

        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                AssertionSimilarityPairReader.class, AssertionSimilarityPairReader.PARAM_SOURCE_LOCATION,path);
        dimReaders.put(DIM_READER_TRAIN, reader);
//		Dimension<List<Object>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
//                Arrays.asList(
//                        new Object[] { new LibsvmAdapter(), "-s", "4" , "-c", "100"}));
		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);

		Map<String, Object> config1 = new HashMap<>();
		config1.put(DIM_CLASSIFICATION_ARGS,new Object[] { new LibsvmAdapter(),  "-s", "4" , "-c", "100" });
		config1.put(DIM_DATA_WRITER, new LibsvmAdapter().getDataWriterClass());
		config1.put(DIM_FEATURE_USE_SPARSE, new LibsvmAdapter().useSparseFeatures());
		
		Dimension<Map<String, Object>> mlas = Dimension.createBundle("config1", config1);
		

        ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
                Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION),
                Dimension.create(DIM_FEATURE_MODE, FM_PAIR), dimFeatureSets, mlas);

        return pSpace;
    }

	protected void runCrossValidation(ParameterSpace pSpace, String title) throws Exception {

		ExperimentCrossValidation batch = new ExperimentCrossValidation("sim_"+title,  NUM_FOLDS);
		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
//		batch.addReport(LibsvmOutcomeIdReport.class);
		
		batch.addReport(BatchCrossValidationReport.class);
//		batch.addReport(BatchCrossValidationReport.class);

		batch.addReport(Id2OutcomeReport.class);
		batch.addReport(ScatterplotReport.class);

		// Run
		Lab.getInstance().run(batch);
	}
    
    protected AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)
		);
	}
}
