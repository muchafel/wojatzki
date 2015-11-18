package featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.ModalVerb;

//TODO: past forms? 
public class ModalVerbFeaturesDFE extends FeatureExtractorResource_ImplBase
implements DocumentFeatureExtractor{

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
	
		Set<Feature> featureList = new HashSet<Feature>();
		for (ModalVerb verbs: JCasUtil.select(jcas, ModalVerb.class)) {
			 featureList.add(new Feature("MODAL_VERBS", true));
		}
		 
		return featureList;
	}

}
