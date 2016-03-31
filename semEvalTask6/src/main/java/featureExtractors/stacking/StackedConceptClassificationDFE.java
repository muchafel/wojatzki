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

public class StackedConceptClassificationDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor{

	public static final String PARAM_MODEL_ONLY_BI_POLAR_CONCEPTS = "useStrictlyPolarConcepts";
	@ConfigurationParameter(name = PARAM_MODEL_ONLY_BI_POLAR_CONCEPTS, mandatory = true, defaultValue = "true")
	protected boolean modelBiPolarConcepts;

	
	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		for(ClassifiedConceptOutcome concept: JCasUtil.select(jcas, ClassifiedConceptOutcome.class)){
			if(modelBiPolarConcepts){
				if(concept.getBiPolar()){
					int outcome=0;
					if(concept.getClassificationOutcome().equals("FAVOR")) outcome=1;
					else if(concept.getClassificationOutcome().equals("AGAINST")) outcome=-1;
					featList.add(new Feature("CONCEPT_"+concept.getConceptName(), outcome));
//					System.out.println(jcas.getDocumentText()+" "+concept.getConceptName()+ " "+ outcome);
				}
			}else{
				int outcome=0;
				if(concept.getClassificationOutcome().equals("FAVOR")) outcome=1;
				else if(concept.getClassificationOutcome().equals("AGAINST")) outcome=-1;
				featList.add(new Feature("CONCEPT_"+concept.getConceptName(), outcome));
			}
		}
		return featList;
	}

}
