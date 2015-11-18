package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import types.HashTagStancePolarity;
import types.WordStancePolarity;

public class FunctionalPartsAnnotationInspector extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		jcas.getDocumentText();
		
		for(WordStancePolarity token: JCasUtil.select(jcas, WordStancePolarity.class)){
			System.out.println("token: "+token.getCoveredText()+" : "+token.getPolarity());
		}
		for(HashTagStancePolarity ht: JCasUtil.select(jcas, HashTagStancePolarity.class)){
			System.out.println("#: "+ht.getCoveredText()+" : "+ht.getPolarity());
		}
	}

}
