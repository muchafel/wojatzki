package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import types.StanceAnnotation;
import types.WordStancePolarity;

public class WordStanceAnnoInspector extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget());
		System.out.println(jcas.getDocumentText());
		for(WordStancePolarity t: JCasUtil.select(jcas, WordStancePolarity.class)){
			System.out.println(t.getCoveredText()+" : "+t.getPolarity());
		}
		
	}

}
