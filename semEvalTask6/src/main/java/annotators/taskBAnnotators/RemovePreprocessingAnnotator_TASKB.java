package annotators.taskBAnnotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import types.StanceAnnotation;
import types.TaskBStanceAnnotation;

public class RemovePreprocessingAnnotator_TASKB extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		List<TOP> toDelete = new ArrayList<TOP>();
		for(TOP anno: JCasUtil.selectAll(jcas)){
			if(anno instanceof TaskBStanceAnnotation || anno instanceof DocumentMetaData || anno instanceof StanceAnnotation){
				
			}else{
				toDelete.add(anno);
			}
		}
		for (TOP anno : toDelete) {
			// delete all POS annotations
			anno.removeFromIndexes();
		}
	}

}
