package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import types.Sentiment;

public class SentimentInspector extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		for(Sentiment s: JCasUtil.select(jcas, Sentiment.class)){
			System.out.println(s.getCoveredText());
			if(s.getBingLiuSentiment()!=0)System.out.println("  bingLiu: "+s.getBingLiuSentiment());
			if(s.getNrcSentiment()!=0)System.out.println("  nrc: "+s.getNrcSentiment());
			if(s.getMpqaSentiment()!=0)System.out.println("  mpqa: "+s.getMpqaSentiment());
		}
		
	}

}
