package dataInspection;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.fit.component.initialize.ExternalResourceInitializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import types.TwitterSpecificPOS;

public class PreprocessingTwitterSpecificAnnotatorInspector extends JCasAnnotator_ImplBase {

	
	public static final String PARAM_WRITE_LEXCICON = "writeHashTagLexicon";
	@ConfigurationParameter(name = PARAM_WRITE_LEXCICON, mandatory = false, defaultValue = "false")
	private boolean writeLexicon;

	
	public static final String PARAM_LEXCICON_PATH = "hashTagLexiconPath";
	@ConfigurationParameter(name = PARAM_LEXCICON_PATH, mandatory = false)
	private String hashTagLexiconPath;
	
	private List<String> hashTagLexicon;

	 @Override
	  public void initialize(final UimaContext context) throws ResourceInitializationException {
	    super.initialize(context);
	    this.hashTagLexicon= new ArrayList<String>();
	  }
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (TwitterSpecificPOS anno : JCasUtil.select(jcas, TwitterSpecificPOS.class)) {
//			System.out.println(anno.getCoveredText() + " " + anno.getIsTokenTwitterSpecific());
			if (anno.getIsTokenTwitterSpecific()) {
				if (writeLexicon) {
					String annoText= anno.getCoveredText().toLowerCase();
					if(hashTagLexiconPath!=null && anno.getTag().equals("#") && !hashTagLexicon.contains(annoText)){
						hashTagLexicon.add(annoText);
						writeToHashTagLexicon(annoText, hashTagLexiconPath);
					}
				}
			}
		}

	}


	private void writeToHashTagLexicon(String annoText, String path) {
		System.out.println("write to "+path );
		try (PrintWriter out = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true))))) {
			out.println(annoText);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
