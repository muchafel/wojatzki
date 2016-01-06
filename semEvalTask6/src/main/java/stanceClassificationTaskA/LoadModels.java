package stanceClassificationTaskA;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import io.TaskATweetReader;
import util.PreprocessingPipeline;

public class LoadModels {

	public static String modelFolder="src/main/resources/trainedModels/Atheism";
	
	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		loadModel(modelFolder,baseDir);
	}

	private static void loadModel(String modelFolder, String baseDir) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReader(
						TaskATweetReader.class,
						TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/Atheism/",
						TaskATweetReader.PARAM_PATTERNS, "*.xml",
						TaskATweetReader.PARAM_LANGUAGE, "en"
				),
				AnalysisEngineFactory.createEngineDescription(PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno()),
				AnalysisEngineFactory.createEngineDescription(
						TcAnnotatorDocument.class,
						TcAnnotatorDocument.PARAM_TC_MODEL_LOCATION, modelFolder
				)
		);	
	}

}
