package featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import curatedTypes.CuratedMainTarget;

/**
 * informed upperbound prediction. Relies on human labled annotations (CuratedMainTarget) in the data.
 * @author niklas
 *
 */
public class OracleMainTargetDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	private String mainTarget = "Original_Stance";
	
	@Override
	public Set<Feature> extract(JCas jcas,TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features= new HashSet<Feature>();
		for(CuratedMainTarget maintarget: JCasUtil.select(jcas, CuratedMainTarget.class)){
				if(mainTarget.equals(maintarget.getTarget())){
					features.add(new Feature("Oracle_MainTarget_"+mainTarget,resolvePolarity(maintarget.getPolarity())));
				}
			
		}
		return features;
	}

	private int resolvePolarity(String polarity) {
		if(polarity.equals("FAVOR")){
			return 1;
		}else if(polarity.equals("AGAINST")){
			return -1;
		}else
			return 0;
	}

}
