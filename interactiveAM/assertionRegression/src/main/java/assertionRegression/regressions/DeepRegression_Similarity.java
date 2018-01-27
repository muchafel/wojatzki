package assertionRegression.regressions;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
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
import assertionRegression.io.AssertionSimilarityPairReader;
import assertionRegression.io.AssertionSimilarityPairReader_deep;
import assertionRegression.io.CrossValidationReport;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class DeepRegression_Similarity implements Constants{
	public static final String LANGUAGE_CODE = "en";
	public static final int NUM_FOLDS = 10;

    public static void main(String[] args)
        throws Exception
    {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);

		ArrayList<String> similarityMatrixes = new ArrayList<String>(Arrays.asList(
//				"assertionSimilarity_climateChange"
//				,
//				"assertionSimilarity_aid",
//				"assertionSimilarity_blm",  
//				"assertionSimilarity_creationism",
//				"assertionSimilarity_electoralSystem", 
//				"assertionSimilarity_gender", 
				"assertionSimilarity_gunRights",
				"assertionSimilarity_immigration", 
				"assertionSimilarity_marijuana", 
				"assertionSimilarity_mediaBias",
				"assertionSimilarity_middleEast"
//				,
//				"assertionSimilarity_obamacare", 
//				"assertionSimilarity_sameSex",
//				"assertionSimilarity_terror", 
//				"assertionSimilarity_vaccination", 
//				"assertionSimilarity_veggie"
				));
		
//		String[] pythonScripts = new String[] { 
//				"src/main/resources/kerasCode/siamese/1.py"
//					 };
		
		String[] pythonScripts = new String[] { 
			
			/*** good stuff ***
			 * 	
			 */
			baseDir + "/UCI/siamese/cosinus.py",
//			baseDir + "/UCI/siamese/concat_conv.py"
			//
			
			
			//			 baseDir + "/UCI/siamese/E.py"
//			 ,
//			 baseDir + "/UCI/siamese/B.py",
//			 baseDir + "/UCI/siamese/D.py"
//			 ,
//			 baseDir + "/UCI/siamese/3.py"
//			 , 
//			 baseDir + "/UCI/siamese/3.py",
//			 baseDir + "/UCI/siamese/4.py",
//			 baseDir + "/UCI/siamese/5.py",
				 };
//		
//		String[] pythonScripts = new String[] { 
//				baseDir +"/siamese/1.py",baseDir + "/siamese/2.py", baseDir + "/siamese/3.py",baseDir + "/siamese/4.py"
//					 };

		String[] embeddings = new String[] { baseDir + "/UCI/data/wiki.en.vec",
				baseDir + "/UCI/data/glove.twitter.27B.200d.txt" };

		DeepRegression_Similarity experiment = new DeepRegression_Similarity();

		for (String simil : similarityMatrixes) {
			int i=1;
			for (String script : pythonScripts) {
				ParameterSpace pSpace = getParameterSpace(baseDir + "/UCI/similarityMatrices/" + simil + ".tsv", "Agreement",script, embeddings[0]);
				experiment.runTrainTest(pSpace, simil+"_"+String.valueOf(i));
				i++;
			}
		}
		
		
		

    }

    public static ParameterSpace getParameterSpace(String path, String targetClass,String pythonCode, String embeddingPath)throws ResourceInitializationException{
		Map<String, Object> dimReaders = getDimReaders(path, targetClass);

		ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT),
				Dimension.create(DIM_LEARNING_MODE, Constants.LM_REGRESSION),
//				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/local/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_PYTHON_INSTALLATION, "/usr/bin/python3"),
				Dimension.create(DeepLearningConstants.DIM_USER_CODE, pythonCode),
				Dimension.create(DeepLearningConstants.DIM_MAXIMUM_LENGTH, 15),
				Dimension.create(DeepLearningConstants.DIM_VECTORIZE_TO_INTEGER, false),
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
		batch.addReport(Id2OutcomeReport.class);
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
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                AssertionSimilarityPairReader_deep.class, AssertionSimilarityPairReader_deep.PARAM_SOURCE_LOCATION,path);
        dimReaders.put(DIM_READER_TRAIN, reader);
		
		return dimReaders;
	}
    
}
