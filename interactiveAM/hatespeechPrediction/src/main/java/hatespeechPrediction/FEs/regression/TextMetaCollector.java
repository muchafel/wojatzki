package hatespeechPrediction.FEs.regression;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.features.meta.MetaCollector;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class TextMetaCollector extends MetaCollector {

	protected File f;
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		String baseDir = null;
		try {
			baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		f= new File(baseDir+"/hateSpeechGender/mapping.txt");
		FileUtils.deleteQuietly(f);
		f= new File(baseDir+"/hateSpeechGender/mapping.txt");
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			FileUtils.write(f, jcas.getDocumentText()+"\n", "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
