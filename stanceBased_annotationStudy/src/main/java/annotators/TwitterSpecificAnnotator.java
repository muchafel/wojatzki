package annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import types.TwitterSpecificPOS;
/**
 * 1.performs ark-tools POS-tagging (does not overwrite existing POS-tagging) 
 * 2.writes twitter-specific POS tags (#,@,U,E,~) to own annotation (TwitterSpecificPOS)
 * @author michael
 *
 */
public class TwitterSpecificAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		// add POS to delete
		List<POS> toDelete = new ArrayList<POS>();

		for (Token tokenAnno : JCasUtil.select(jcas, Token.class)) {

			POS posValue = tokenAnno.getPos();
			String cPosValue = tokenAnno.getPos().getPosValue();
			TwitterSpecificPOS anno = new TwitterSpecificPOS(jcas);
			if(isTwitterSpecific(cPosValue)){
				anno.setIsTokenTwitterSpecific(true);
				anno.setTag(cPosValue);
			}else anno.setIsTokenTwitterSpecific(false);

			anno.setBegin(posValue.getBegin());
			anno.setEnd(posValue.getEnd());
			anno.addToIndexes();
			toDelete.add(posValue);

		}

		for (POS pos : toDelete) {
			// delete all POS annotations
			pos.removeFromIndexes();
		}
	}

	private boolean isTwitterSpecific(String cPosValue) {
		if(cPosValue.equals("#"))return true;
		if(cPosValue.equals("@"))return true;
		if(cPosValue.equals("U"))return true;
		if(cPosValue.equals("E"))return true;
		if(cPosValue.equals("~"))return true;
		return false;
	}

}
