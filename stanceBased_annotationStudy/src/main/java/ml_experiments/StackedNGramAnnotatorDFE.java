package ml_experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import predictedTypes.ClassifiedSubTarget;
import predictedTypes.NgramClassification;

public class StackedNGramAnnotatorDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {
	private ArrayList<String> ngramVariants = new ArrayList<String>(Arrays.asList("ATHEISM_char", "ATHEISM_word"));

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
		for (NgramClassification prediction : JCasUtil.select(jcas, NgramClassification.class)) {
			for (String variant : ngramVariants) {
				if (variant.equals(prediction.getVariant())) {
					System.out.println(JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId()+ " "+ variant+ " "+prediction.getClassificationOutcome());
					features.add(new Feature(variant + "_prediction",
							resolvePolarity(prediction.getClassificationOutcome())));
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
