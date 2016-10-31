package ml_experiments.embeddingExperiments;

import java.io.File;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.engine.TaskContext;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.lab.uima.task.impl.UimaTaskBase;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextWriter;

public class PreprocessingTask extends UimaTaskBase{
	 public static final String OUTPUT_FOLDER = "output";

		public CollectionReaderDescription getCollectionReaderDescription(TaskContext context) 
				throws ResourceInitializationException, IOException
		{		
			return CollectionReaderFactory.createReaderDescription(
					TextReader.class,
					TextReader.PARAM_SOURCE_LOCATION, "src/main/resources/texts/",
					TextReader.PARAM_PATTERNS, "*.txt",
					TextReader.PARAM_LANGUAGE, "en"
			);
		}
		
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext context) 
				throws ResourceInitializationException,	IOException 
		{
			
	        File outputFolder = context.getFolder(OUTPUT_FOLDER, AccessMode.READWRITE);

			return AnalysisEngineFactory.createEngineDescription(
					TextWriter.class,
					TextWriter.PARAM_TARGET_LOCATION, outputFolder
			);
		}
}
