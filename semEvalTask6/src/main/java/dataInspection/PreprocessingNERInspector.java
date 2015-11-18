package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class PreprocessingNERInspector extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		System.err.println("NEs:");
		for (NamedEntity ne : JCasUtil.select(jcas, NamedEntity.class)) {
			System.out.println(ne.getValue() + " " + ne.getCoveredText());
		}
		System.err.println("Nouns");
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if (t.getPos() != null) {
				if (t.getPos().getPosValue().equals("NN") || t.getPos().getPosValue().equals("NNP"))
					System.out.println(t.getCoveredText());
			}
		}
	}

}
