package annotators;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import io.BingLiuSentimentLexicon;
import io.NRCSentimentLexicon;
import lexicons.MPQASentimentLexicon;
import lexicons.SentimentLexicon;

public class LexiconBasedSentimentAnnotator extends JCasAnnotator_ImplBase {

	private String nrcPath = null;
	private String mpqaPath = null;
	private String bingLiuPath = null;
	
	private SentimentLexicon nrcSentimentLexicon;
	private SentimentLexicon mpqaSentimentLexicon;
	private SentimentLexicon bingLiuSentimentLexicon;
	
	@Override
	public void initialize(org.apache.uima.UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		this.nrcSentimentLexicon = new NRCSentimentLexicon(nrcPath);
		this.mpqaSentimentLexicon = new MPQASentimentLexicon(mpqaPath);
		this.bingLiuSentimentLexicon = new BingLiuSentimentLexicon(bingLiuPath);
	};
	
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		

	}

}
