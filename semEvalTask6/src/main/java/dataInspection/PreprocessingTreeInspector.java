package dataInspection;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.PennTree;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;

public class PreprocessingTreeInspector extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		for(PennTree tree :JCasUtil.select(jcas, PennTree.class)){
//			System.out.println(tree.getPennTree());
			Collection<Constituent> constituents = JCasUtil.selectCovered(Constituent.class, tree);
			for(Constituent constituent: constituents){
				System.out.println(constituent.getConstituentType()+" : "+constituent.getCoveredText());
			}
		}
	}

}
