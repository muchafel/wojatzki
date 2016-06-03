package util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import io.SubStanceReader;

public class BinToXMI {
//	private static final String FilteringPostfix = "_wo_irony_understandability";
	private static final String FilteringPostfix = "";
	
	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();		
		BinToXMI pipeline= new BinToXMI();
		pipeline.run(baseDir);
	}

	private void run(String baseDir) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReader(
						SubStanceReader.class,
						SubStanceReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all"+FilteringPostfix, SubStanceReader.PARAM_LANGUAGE,
						"en", SubStanceReader.PARAM_PATTERNS, "*.bin", SubStanceReader.PARAM_TARGET_LABEL,"ATHEISM"),
				AnalysisEngineFactory.createEngineDescription(createEngineDescription(XmiWriter.class,XmiWriter.PARAM_TARGET_LOCATION, baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all_xmi"))
		);	
	}
}
