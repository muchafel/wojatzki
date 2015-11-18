package annotators;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import types.FunctionalPartAnnotation;
import types.TwitterSpecificPOS;
import util.StanceConstants;

public class FunctionalPartsAnnotator extends JCasAnnotator_ImplBase implements StanceConstants {

	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		for(Sentence sentence: JCasUtil.select(jcas, Sentence.class)){
			//containsRegularPOS in contrast to twitter-specific POS
			boolean containsRegularPOS=false;
			for(TwitterSpecificPOS token: JCasUtil.selectCovered(TwitterSpecificPOS.class,sentence)){
				if(!token.getIsTokenTwitterSpecific()){
					containsRegularPOS=true;
					break;
				}
			}
			
			FunctionalPartAnnotation anno= new FunctionalPartAnnotation(jcas);
			anno.setBegin(sentence.getBegin());
			anno.setEnd(sentence.getEnd());
			
			if(containsRegularPOS){
//				System.out.println("sentence "+sentence.getCoveredText());
				anno.setFunction(SENTENCE_FUNCTION);
			}else{
//				System.out.println("tag "+sentence.getCoveredText());
				anno.setFunction(TAG_FUNCTION);
			}
				
			anno.addToIndexes();
		}
	}
}
