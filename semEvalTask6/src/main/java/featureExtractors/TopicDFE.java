package featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.StanceAnnotation;

public class TopicDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {

	String[] targets={"Atheism", "Climate Change is a Real Concern", "Feminist Movement", "Hillary Clinton", "Legalization of Abortion"};	
	
	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		String topic = "";
		topic = JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget();

		return getFeatList(topic);
	}

	private Set<Feature> getFeatList(String topic) {
		Set<Feature> features= new HashSet<Feature>();

		for (String target : targets){
			if(topic.equals(target)){
				features.add(new Feature("TOPIC_"+target, 1));
			}else{
				features.add(new Feature("TOPIC_"+target, 0));
			}
		}
		return features;
	}

}
