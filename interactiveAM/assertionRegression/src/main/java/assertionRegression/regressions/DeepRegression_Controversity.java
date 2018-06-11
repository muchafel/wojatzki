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
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.ScatterplotReport;

import assertionRegression.io.AssertionReader;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import weka.classifiers.functions.SMOreg;

public class DeepRegression_Controversity implements Constants{
	public static final String LANGUAGE_CODE = "en";
	public static final int NUM_FOLDS = 10;

    public static void main(String[] args)
        throws Exception
    {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);

//		String[] pythonScripts = new String[] { 
//				baseDir+"/regression_exp/1.py",
//				baseDir+"/regression_exp/2.py", baseDir+"/regression_exp/3.py",
//				baseDir+"/regression_exp/4.py", baseDir+"/regression_exp/5.py",
//				baseDir+"/regression_exp/6.py", baseDir+"/regression_exp/7.py",
//				baseDir+"/regression_exp/8.py", baseDir+"/regression_exp/9.py",
//				baseDir+"/regression_exp/10.py",
//				baseDir+"/regression_exp/11.py" };
		String[] pythonScripts = new String[] { 
				baseDir+"/regression_conv/A.py",
				baseDir+"/regression_conv/B.py", baseDir+"/regression_conv/C.py",baseDir+"/regression_conv/D.py"
				 };

		String[] embeddings = new String[] { baseDir + "/UCI/data/wiki.en.vec",
				baseDir + "/UCI/data/glove.twitter.27B.200d.txt" };

		DeepRegression_Controversity experiment = new DeepRegression_Controversity();

		// System.setProperty("DKPRO_HOME", System.getProperty("user.home") +
		// "/Desktop");
		for (String script : pythonScripts) {
			int i=0;
			for (String embeddingPath : embeddings) {
				String name = script.split("/")[5];
				ParameterSpace pSpace = getParameterSpace(baseDir + "/UCI/data/data.tsv", "Controversity", script,embeddingPath);
				
				experiment.runTrainTest(pSpace, "controversity_script"+name.split("\\.")[0]+"_embeddings"+String.valueOf(i));
				
				i++;
			}
		}
//		ParameterSpace pSpace = getParameterSpace(baseDir + "/UCI/data/data.tsv", "Agreement", baseDir+"/UCI/regression_exp/2.py",embeddings[1]);
//		
//		experiment.runTrainTest(pSpace, "test");

    }

    public static ParameterSpace getParameterSpace(String path, String targetClass,String pythonCode, String embeddingPath)throws ResourceInitializationException{
		Map<String, Object> dimReaders = getDimReaders(path, targetClass);

		ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT),
				Dimension.create(DIM_LEARNING_MODE, Constants.LM_REGRESSION),
//				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/local/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_USER_CODE, pythonCode),
				Dimension.create(DeepLearningConstants.DIM_MAXIMUM_LENGTH, 50),
				Dimension.create(DeepLearningConstants.DIM_VECTORIZE_TO_INTEGER, true),
				Dimension.create(DeepLearningConstants.DIM_PRETRAINED_EMBEDDINGS, embeddingPath)

		);

		return pSpace;
    }

    protected void runTrainTest(ParameterSpace pSpace, String name)
        throws Exception{

        DeepLearningExperimentCrossValidation batch = new DeepLearningExperimentCrossValidation(name,KerasAdapter.class,NUM_FOLDS);
        batch.setPreprocessing(getPreprocessing());
        batch.setParameterSpace(pSpace);
        batch.addReport(BatchCrossValidationReport.class);        
        batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
        batch.addReport(ScatterplotReport.class);

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
		dimReaders.put(DIM_READER_TRAIN, CollectionReaderFactory.createReaderDescription(AssertionReader.class, AssertionReader.PARAM_SOURCE_LOCATION, path, AssertionReader.PARAM_LANGUAGE,"en",AssertionReader.PARAM_TARGETCLASS,targetClass));
		
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
