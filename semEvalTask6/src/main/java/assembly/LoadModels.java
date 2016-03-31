package assembly;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import dataInspection.OutcomeInspection;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

import org.dkpro.tc.ml.uima.TcAnnotator;
import io.TaskATweetReader;
import util.PreprocessingPipeline;

public class LoadModels {

	public static String modelFolder="src/main/resources/trainedModels/favorVsAgainst/Atheism";
	
	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		loadModel(modelFolder,baseDir);
	}

	private static void loadModel(String modelFolder, String baseDir) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReader(
						UnclassifiedTweetReader.class,
						UnclassifiedTweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/Atheism/",
						UnclassifiedTweetReader.PARAM_PATTERNS, "*.xml",
						UnclassifiedTweetReader.PARAM_LANGUAGE, "en"
				),
				AnalysisEngineFactory.createEngineDescription(PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno()),
				//annotate stance
				AnalysisEngineFactory.createEngineDescription(
						TcAnnotator.class,
						TcAnnotator.PARAM_TC_MODEL_LOCATION, modelFolder
				),
				AnalysisEngineFactory.createEngineDescription(
						OutcomeInspection.class
				)
		);	
	}
}
