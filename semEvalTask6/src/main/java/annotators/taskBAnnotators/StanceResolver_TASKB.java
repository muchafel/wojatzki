package annotators.taskBAnnotators;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import types.TaskBStanceAnnotation;

public class StanceResolver_TASKB extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String resolvedStance="";
		int favor=0;
		int against=0;
		for(TaskBStanceAnnotation anno: JCasUtil.select(jcas, TaskBStanceAnnotation.class)){
			if(anno.getTarget().equals("DonaldTrump")){
				if(anno.getStance().equals("NONE")){
					resolvedStance=anno.getStance();
					break;
				}
			}else{
				if(anno.getStance().equals("AGAINST"))against++;
				if(anno.getStance().equals("FAVOR"))favor++;
			}
		}
		if(!resolvedStance.equals("NONE")){
			resolvedStance=resolve(against,favor);
		}
		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
		outcome.setOutcome(resolvedStance);
		outcome.addToIndexes();
	}

	private String resolve(int against, int favor) {
		if((against==0 && favor==0 )||against==favor )return "NONE";
		if(against>favor)return "AGAINST";
		else return "FAVOR";
	}

}
