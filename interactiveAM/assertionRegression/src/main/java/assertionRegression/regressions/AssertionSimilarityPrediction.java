package assertionRegression.regressions;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.pair.core.length.DiffNrOfTokensPairFeatureExtractor;
import org.dkpro.tc.features.pair.core.ngram.LuceneNGramCPFE;
import org.dkpro.tc.features.pair.core.ngram.LuceneNGramPFE;
import org.dkpro.tc.features.pair.similarity.GreedyStringTilingFeatureExtractor;
import org.dkpro.tc.features.pair.similarity.SimilarityPairFeatureExtractor;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentTrainTest;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.ScatterplotReport;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;

import assertionRegression.io.AssertionSimilarityPairReader;
import assertionRegression.io.CrossValidationReport;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramContainmentResource;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramJaccardResource;
import dkpro.similarity.algorithms.lexical.uima.string.GreedyStringTilingMeasureResource;

public class AssertionSimilarityPrediction  implements Constants
{

    public static final String languageCode = "en";

    
    static ExternalResourceDescription gstResource = ExternalResourceFactory
            .createExternalResourceDescription(GreedyStringTilingMeasureResource.class,
                    GreedyStringTilingMeasureResource.PARAM_MIN_MATCH_LENGTH, "3");
    
    static ExternalResourceDescription jaccardResource = ExternalResourceFactory
            .createExternalResourceDescription(WordNGramJaccardResource.class,
            		WordNGramJaccardResource.PARAM_N, "1");
    
    static ExternalResourceDescription ngramResource = ExternalResourceFactory
            .createExternalResourceDescription(WordNGramContainmentResource.class,
            		WordNGramContainmentResource.PARAM_N, "2");
    
    
    public static void main(String[] args)
        throws Exception
    {

    	
    
    	
    	
		TcFeatureSet featureSet = new TcFeatureSet(
				// TcFeatureFactory.create(GreedyStringTilingFeatureExtractor.class)
//				TcFeatureFactory.create(SimilarityPairFeatureExtractor.class,
//						SimilarityPairFeatureExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, "ngram_2",
//						SimilarityPairFeatureExtractor.PARAM_SEGMENT_FEATURE_PATH, Token.class.getName(),
//						SimilarityPairFeatureExtractor.PARAM_TEXT_SIMILARITY_RESOURCE, ngramResource)
//				,TcFeatureFactory.create(SimilarityPairFeatureExtractor.class,
//						SimilarityPairFeatureExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, "jaccard_1",
//						SimilarityPairFeatureExtractor.PARAM_SEGMENT_FEATURE_PATH, Token.class.getName(),
//						SimilarityPairFeatureExtractor.PARAM_TEXT_SIMILARITY_RESOURCE, jaccardResource)
//				,TcFeatureFactory.create(SimilarityPairFeatureExtractor.class,
//						SimilarityPairFeatureExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, "gst",
//						SimilarityPairFeatureExtractor.PARAM_SEGMENT_FEATURE_PATH, Token.class.getName(),
//						SimilarityPairFeatureExtractor.PARAM_TEXT_SIMILARITY_RESOURCE, gstResource)
				TcFeatureFactory.create(LuceneNGramPFE.class,
				LuceneNGramPFE.PARAM_NGRAM_MIN_N, 4, LuceneNGramPFE.PARAM_NGRAM_MAX_N, 4,
				LuceneNGramPFE.PARAM_USE_VIEW1_NGRAMS_AS_FEATURES, true,
				LuceneNGramPFE.PARAM_USE_VIEW2_NGRAMS_AS_FEATURES, true,
				LuceneNGramPFE.PARAM_USE_VIEWBLIND_NGRAMS_AS_FEATURES, false)
				);
        
        ParameterSpace pSpace = getParameterSpace(featureSet);
        AssertionSimilarityPrediction experiment = new AssertionSimilarityPrediction();
        experiment.runCrossValidation(pSpace);
    }

    @SuppressWarnings("unchecked")
    public static ParameterSpace getParameterSpace(TcFeatureSet featureSet)
        throws ResourceInitializationException
    {
        Map<String, Object> dimReaders = new HashMap<String, Object>();

        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                AssertionSimilarityPairReader.class, AssertionSimilarityPairReader.PARAM_SOURCE_LOCATION,"src/main/resources/similarityMatrices/assertionSimilarity_gunRights.tsv");
        dimReaders.put(DIM_READER_TRAIN, reader);


        Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
                Arrays.asList(new String[] { "-s", LibsvmAdapter.PARAM_SVM_TYPE_NU_SVR_REGRESSION , "-c", "100"}));

        Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, featureSet);

        ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
                Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION),
                Dimension.create(DIM_FEATURE_MODE, FM_PAIR), dimFeatureSets,
                dimClassificationArgs);

        return pSpace;
    }

	protected void runCrossValidation(ParameterSpace pSpace) throws Exception {

		ExperimentCrossValidation batch = new ExperimentCrossValidation("sim_test", LibsvmAdapter.class, 10);
		batch.setPreprocessing(getPreprocessing());
		batch.setParameterSpace(pSpace);
		batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
//		batch.addReport(CrossValidationReport.class);
		batch.addReport(BatchCrossValidationReport.class);
		batch.addReport(ScatterplotReport.class);

		// Run
		Lab.getInstance().run(batch);
	}
    
    protected AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException {
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)
		);
	}

}

