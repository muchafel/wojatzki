package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import types.StanceAnnotation;

public class OutcomeInspection extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String outcome = JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome();
		String target= JCasUtil.selectSingle(jcas, StanceAnnotation.class).getTarget();
//		System.out.println(target+" "+JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId()+ " "+ outcome);
	}

}
