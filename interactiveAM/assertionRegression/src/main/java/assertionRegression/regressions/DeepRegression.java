package assertionRegression.regressions;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.DeepLearningConstants;
import org.dkpro.tc.ml.DeepLearningExperimentCrossValidation;
import org.dkpro.tc.ml.DeepLearningExperimentTrainTest;
import org.dkpro.tc.ml.keras.KerasAdapter;

import assertionRegression.io.AssertionReader;
import assertionRegression.io.CrossValidationReport;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import weka.classifiers.functions.SMOreg;

public class DeepRegression implements Constants{
	public static final String LANGUAGE_CODE = "en";

    public static void main(String[] args)
        throws Exception
    {

//      System.setProperty("DKPRO_HOME", System.getProperty("user.home") + "/Desktop");

    	String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
        DeepRegression experiment = new DeepRegression();
        ParameterSpace pSpace = getParameterSpace("src/main/resources/data/data.tsv","Agreement");

        experiment.runTrainTest(pSpace,"lstmdropoutzero5");
    }

    public static ParameterSpace getParameterSpace(String path, String targetClass)throws ResourceInitializationException{
    	Map<String, Object> dimReaders = getDimReaders(path,targetClass);

    	ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
                Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT),
                Dimension.create(DIM_LEARNING_MODE, Constants.LM_REGRESSION),
                Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION,"/usr/local/bin/python3"),
//                Dimension.create(DeepLearningConstants.DIM_USER_CODE,"src/main/resources/kerasCode/regression/regression_BiLSTM.py"),
//                Dimension.create(DeepLearningConstants.DIM_USER_CODE,"src/main/resources/kerasCode/regression/regression_DBN.py"),
//                Dimension.create(DeepLearningConstants.DIM_USER_CODE,"src/main/resources/kerasCode/regression/regression_DBN_do05.py"),
                Dimension.create(DeepLearningConstants.DIM_USER_CODE,"src/main/resources/kerasCode/regression/regression_biLSTM + deep.py"),
                Dimension.create(DeepLearningConstants.DIM_MAXIMUM_LENGTH, 30),
                Dimension.create(DeepLearningConstants.DIM_VECTORIZE_TO_INTEGER, true),
                Dimension.create(DeepLearningConstants.DIM_PRETRAINED_EMBEDDINGS,"/Users/michael/git/lang-tech-teaching/de.unidue.kbs/src/main/resources/GloVe/glove.6B.200d.txt")
                );

		return pSpace;
    }

    protected void runTrainTest(ParameterSpace pSpace, String name)
        throws Exception
    {

        DeepLearningExperimentCrossValidation batch = new DeepLearningExperimentCrossValidation(name,
                KerasAdapter.class,10);
        batch.setPreprocessing(getPreprocessing());
        batch.setParameterSpace(pSpace);
		batch.addReport(CrossValidationReport.class);
        batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);

        // Run
        Lab.getInstance().run(batch);
    }

    protected static AnalysisEngineDescription getPreprocessing()
        throws ResourceInitializationException{
        return createEngineDescription(BreakIteratorSegmenter.class);
    }
    
    

	private static Map<String, Object> getDimReaders(String path, String targetClass) throws ResourceInitializationException {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		System.out.println("read "+path);
		dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(AssertionReader.class, AssertionReader.PARAM_SOURCE_LOCATION, path, AssertionReader.PARAM_LANGUAGE,
				"en",AssertionReader.PARAM_TARGETCLASS,targetClass));
		
//		CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
//				AssertionReader.class, AssertionReader.PARAM_SOURCE_LOCATION, "src/main/resources/data/data_train.tsv", AssertionReader.PARAM_LANGUAGE,
//				"en");
//        dimReaders.put(DIM_READER_TRAIN, readerTrain);
//
//        CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(AssertionReader.class, AssertionReader.PARAM_SOURCE_LOCATION, "src/main/resources/data/data_test.tsv", AssertionReader.PARAM_LANGUAGE,
//				"en");
//        dimReaders.put(DIM_READER_TEST, readerTest);

		return dimReaders;
	}
    
}
