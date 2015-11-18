package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import types.ModalVerb;
import types.StanceAnnotation;

public class PreprocessingTokenizationInspector extends JCasAnnotator_ImplBase  {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println("Document Text: "+ jcas.getDocumentText());
		System.out.println("Target: "+ JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget());
		System.out.println("Tokens: ");
		for (Sentence sentence: JCasUtil.select(jcas, Sentence.class)){
			System.out.println("sentence: "+ sentence.getCoveredText());
		}
		for (Token t: JCasUtil.select(jcas, Token.class)){
			System.out.println(t.getCoveredText());
			if(t.getPos()!=null)System.out.println(" POS: "+ t.getPos().getPosValue());
			if(t.getLemma()!=null)System.out.println(" Lemma: "+ t.getLemma().getValue());
			if(!JCasUtil.selectCovered( ModalVerb.class, t).isEmpty()) System.out.println(t.getCoveredText()+ " is modal verb");
		}
	}
}
