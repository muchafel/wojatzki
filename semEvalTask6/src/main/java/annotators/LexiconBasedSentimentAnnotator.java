package annotators;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import lexicons.BingLiuSentimentLexicon;
import lexicons.MPQASentimentLexicon;
import lexicons.NRCSentimentLexicon;
import lexicons.SentimentLexicon;
import types.Sentiment;

public class LexiconBasedSentimentAnnotator extends JCasAnnotator_ImplBase {

	
	
	public static final String PARAM_NRC_LEXICON_FILE_PATH = "nrcSentimentLexiconFilePath";
	public static final String PARAM_MPQA_LEXICON_FILE_PATH = "mpqaSentimentLexiconFilePath";
	public static final String PARAM_BING_LIU_LEXICON_FILE_PATH = "bingLiuSentimentLexiconFilePath";
	
	@ConfigurationParameter(name = PARAM_NRC_LEXICON_FILE_PATH, mandatory = true)
	private String nrcPath = "";
	
	@ConfigurationParameter(name = PARAM_MPQA_LEXICON_FILE_PATH, mandatory = true)
	private String mpqaPath = "";
	
	@ConfigurationParameter(name = PARAM_BING_LIU_LEXICON_FILE_PATH, mandatory = true)
	private String bingLiuPath = "";
	
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
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Token t: JCasUtil.select(jcas, Token.class)){
			String tokenText=t.getCoveredText();
			String tokenLemma= t.getLemma().getValue();
			
			Sentiment anno= new Sentiment(jcas);
			anno.setBegin(t.getBegin());
			anno.setEnd(t.getEnd());
			float nrcSentimen= nrcSentimentLexicon.getSentiment(tokenLemma);
			float mpqaSentimen = mpqaSentimentLexicon.getSentiment(tokenLemma);
			float bingLiuSentiment = bingLiuSentimentLexicon.getSentiment(tokenLemma);
			
			if(nrcSentimen !=0){
				anno.setNrcSentiment(nrcSentimen);
			}else{
				anno.setNrcSentiment(nrcSentimentLexicon.getSentiment(tokenText));
			}
			
			if(mpqaSentimen!=0){
				anno.setMpqaSentiment(mpqaSentimen);
			}else{
				anno.setMpqaSentiment(mpqaSentimentLexicon.getSentiment(tokenText));
			}
			
			if(bingLiuSentiment!=0){
				anno.setBingLiuSentiment(bingLiuSentiment);
			}else{
				anno.setBingLiuSentiment(bingLiuSentimentLexicon.getSentiment(tokenText));
			}
			anno.addToIndexes();
		}
	}

}
