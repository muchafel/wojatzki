package util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import io.PlainTaskATweetReader;
import io.StanceResultWriter;
import io.TaskATweetReader;
import util.PreprocessingPipeline;

public class TweetsToXMI {

	
	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {
		ArrayList<String> targets = new ArrayList<String>(
			    Arrays.asList("Atheism","FeministMovement", "ClimateChangeisaRealConcern","HillaryClinton", "LegalizationofAbortion"));
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		for(String target: targets){
			TweetsToXMI pipelineTaskA= new TweetsToXMI();
			pipelineTaskA.run(baseDir,target);
		}
	}

	private void run(String baseDir, String target) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				//training data
//				CollectionReaderFactory.createReader(
//						PlainTaskATweetReader.class,
//						PlainTaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/"+target+"/", PlainTaskATweetReader.PARAM_PATTERNS,
//						"*.xml", PlainTaskATweetReader.PARAM_LANGUAGE, "en",PlainTaskATweetReader.PARAM_MEMORIZE_RESOURCE,true
//				),
				//test data
				CollectionReaderFactory.createReader(
						PlainTaskATweetReader.class,
						PlainTaskATweetReader.PARAM_SOURCE_LOCATION, "/Users/michael/ArgumentMiningCoprora/semEval2016/SemEval2016-Task6-testdata/xmls/tweets/taskA_targetWise/"+target, PlainTaskATweetReader.PARAM_PATTERNS,
						"*.xml", PlainTaskATweetReader.PARAM_LANGUAGE, "en",PlainTaskATweetReader.PARAM_MEMORIZE_RESOURCE,true
				),
				AnalysisEngineFactory.createEngineDescription(createEngineDescription(ArktweetTokenizer.class)),
				AnalysisEngineFactory.createEngineDescription(createEngineDescription(XmiWriter.class,XmiWriter.PARAM_TARGET_LOCATION, baseDir + "/semevalTask6/TweetsTaskA_xmi_test/"+target+"/"))
		);	
	}

}
