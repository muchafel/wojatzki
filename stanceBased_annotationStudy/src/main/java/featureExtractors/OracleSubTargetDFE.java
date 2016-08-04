package featureExtractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import curatedTypes.CuratedSubTarget;

/**
 * informed upperbound prediction. Relies on human labled annotations (CuratedSubTarget) in the data.
 * @author michael
 *
 */
public class OracleSubTargetDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList(
//			"secularism",
//			"Same-sex marriage",
//			"religious_freedom", 
//			"Conservative_Movement",
//			"Freethinking",
//			"Islam"
//			,"No_evidence_for_religion"
//			,"USA"
			"Supernatural_Power_Being"
//			, "Life_after_death"
//			, "Christianity"
			));
	
	@Override
	public Set<Feature> extract(JCas jcas,TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features= new HashSet<Feature>();
		for(CuratedSubTarget subtarget: JCasUtil.select(jcas, CuratedSubTarget.class)){
			for(String subTarget: subTargets){
				if(subTarget.equals(subtarget.getTarget())){
					features.add(new Feature("Oracle_SubTarget_"+subTarget,resolvePolarity(subtarget.getPolarity())));
				}
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
