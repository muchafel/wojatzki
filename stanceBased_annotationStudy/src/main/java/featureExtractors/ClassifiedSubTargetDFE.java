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
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import predictedTypes.ClassifiedSubTarget;

/**
 * feature extractor works on a annotation that are made based on a previously trained model. The predictions by the model are stored in ClassifiedSubTarget Annotations
 * @author michael
 *
 */
public class ClassifiedSubTargetDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList("secularism", "Same-sex marriage",
			"religious_freedom", "Conservative_Movement", "Freethinking", "Islam", "No_evidence_for_religion", "USA",
			"Supernatural_Power_Being", "Life_after_death", "Christianity"));

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
		for (ClassifiedSubTarget subtarget : JCasUtil.select(jcas, ClassifiedSubTarget.class)) {
			for (String subTarget : subTargets) {
				if (subTarget.equals(subtarget.getSubTarget())) {
//					System.out.println(JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId()+ " "+target+" "+subtarget.getClassificationOutcome());
					features.add(new Feature("Predicted_SubTarget_" + subTarget,resolvePolarity((subtarget.getClassificationOutcome()))));
				}
			}
		}
		return features;
	}

	private int resolvePolarity(String polarity) {
		if (polarity.equals("FAVOR")) {
			return 1;
		} else if (polarity.equals("AGAINST")) {
			return -1;
		} else
			return 0;
	}

}
