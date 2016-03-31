package featureExtractors.stacking;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.ClassifiedConceptOutcome;
import types.TransferClassificationOutcome;

public class StackedTargetTransferClassificationDFE extends FeatureExtractorResource_ImplBase
		implements DocumentFeatureExtractor {

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		for (TransferClassificationOutcome transferredClassification : JCasUtil.select(jcas, TransferClassificationOutcome.class)) {
				int outcome = 0;
				if (transferredClassification.getOutcome().equals("FAVOR"))
					outcome = 1;
				else if (transferredClassification.getOutcome().equals("AGAINST"))
					outcome = -1;
				featList.add(new Feature( transferredClassification.getModel()+"_outcome", outcome));
				// System.out.println(jcas.getDocumentText()+"
				// "+concept.getConceptName()+ " "+ outcome);
		}
		return featList;
	}
}
