package de.uni_due.ltl.featureExtractors.subdebates;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.uni_due.ltl.util.TargetSets;
import preprocessing.SentenceStance;

public class ClassifiedSubdebateSentencesDFE extends ClassifiedSubdebateDFE{
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		
			if(useSet1){
				for(String target1: TargetSets.targets_Set1 ){
					int i=0;
					for(Sentence sentence: JCasUtil.selectCovered(Sentence.class, unit)){
						for(SentenceStance st: JCasUtil.selectCovered(SentenceStance.class, sentence)){
							if(st.getTarget().equals(target1)){
								i+=st.getPolarity();
							}
						}
					}
					featList.add(new Feature(target1+"_SENTENCE", i));
				}
			}
			if(useSet2){
				for(String target2: TargetSets.targets_Set2 ){
					int i=0;
					for(Sentence sentence: JCasUtil.selectCovered(Sentence.class, unit)){
						for(SentenceStance st: JCasUtil.selectCovered(SentenceStance.class, sentence)){
							if(st.getTarget().equals(target2)){
								i+=st.getPolarity();
							}
						}
					}
					featList.add(new Feature(target2+"_SENTENCE", i));
				}
			}
		return featList;
	}
}
