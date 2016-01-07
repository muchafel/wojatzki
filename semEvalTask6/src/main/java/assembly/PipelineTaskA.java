package assembly;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import dataInspection.OutcomeInspection;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import util.PreprocessingPipeline;

public class PipelineTaskA {
	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		String modelFolderStanceVsNone="src/main/resources/trainedModels/noneVsStance/Atheism";
		String modelFolderFavorAgainst="src/main/resources/trainedModels/favorVsAgainst/Atheism";
		String target="Atheism";
		PipelineTaskA pipelineTaskA= new PipelineTaskA();
		pipelineTaskA.run(baseDir,modelFolderStanceVsNone,modelFolderFavorAgainst,target);
	}

	private void run(String baseDir, String modelFolder, String modelFolderFavorAgainst,String target) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReader(
						UnclassifiedTweetReader.class,
						UnclassifiedTweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/Atheism/",
						UnclassifiedTweetReader.PARAM_PATTERNS, "*.xml",
						UnclassifiedTweetReader.PARAM_LANGUAGE, "en"
				),
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
				)
		);	
	}
}
