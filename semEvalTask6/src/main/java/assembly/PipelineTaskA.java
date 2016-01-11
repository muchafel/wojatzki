package assembly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import dataInspection.OutcomeInspection;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import io.StanceResultWriter;
import util.PreprocessingPipeline;

public class PipelineTaskA {
	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {
		ArrayList<String> targets = new ArrayList<String>(
			    Arrays.asList("Atheism","FeministMovement", "ClimateChangeisaRealConcern","HillaryClinton", "LegalizationofAbortion"));
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		for(String target: targets){
			if(target.equals("Atheism"))continue;
			String modelFolderStanceVsNone="src/main/resources/trainedModels/noneVsStance/"+target;
			String modelFolderFavorAgainst="src/main/resources/trainedModels/favorVsAgainst/"+target;
			PipelineTaskA pipelineTaskA= new PipelineTaskA();
			System.out.println(modelFolderStanceVsNone+" "+modelFolderFavorAgainst+" "+target);
			pipelineTaskA.run(baseDir,modelFolderStanceVsNone,modelFolderFavorAgainst,target);
		}
	}

	private void run(String baseDir, String modelFolder, String modelFolderFavorAgainst,String target) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				//training data
//				CollectionReaderFactory.createReader(
//						UnclassifiedTweetReader.class,
//						UnclassifiedTweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/"+target+"/",
//						UnclassifiedTweetReader.PARAM_PATTERNS, "*.xml",
//						UnclassifiedTweetReader.PARAM_LANGUAGE, "en"
//				)
				//test data
				CollectionReaderFactory.createReader(
						UnclassifiedTweetReader.class,
						UnclassifiedTweetReader.PARAM_SOURCE_LOCATION, "/Users/michael/ArgumentMiningCoprora/semEval2016/SemEval2016-Task6-testdata/xmls/tweets/taskA_targetWise/"+target+"/",
						UnclassifiedTweetReader.PARAM_PATTERNS, "*.xml",
						UnclassifiedTweetReader.PARAM_LANGUAGE, "en"
				)
				,
//				AnalysisEngineFactory.createEngineDescription(PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno()),
				AnalysisEngineFactory.createEngineDescription(PreprocessingPipeline.getFullPreProcessing(target, false)),
				//annotate none vs stance
				AnalysisEngineFactory.createEngineDescription(
						TcAnnotatorDocument.class,
						TcAnnotatorDocument.PARAM_TC_MODEL_LOCATION, modelFolder
				),
				//annotate fav vs against
				AnalysisEngineFactory.createEngineDescription(
						FavorAgainstOutcomeAnnotator.class,
						FavorAgainstOutcomeAnnotator.PARAM_TC_MODEL_LOCATION, modelFolderFavorAgainst
				),
				AnalysisEngineFactory.createEngineDescription(
						OutcomeInspection.class
				),
				AnalysisEngineFactory.createEngineDescription(
						StanceResultWriter.class, StanceResultWriter.PARAM_RESULT_OUTPUT_TARGET,target
				)
		);	
	}

}
