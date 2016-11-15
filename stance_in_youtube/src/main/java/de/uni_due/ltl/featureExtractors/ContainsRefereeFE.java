package de.uni_due.ltl.featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import preprocessing.Users;

public class ContainsRefereeFE extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		Users users=JCasUtil.selectCovered(jcas, Users.class,unit).iterator().next();
		String referee = users.getReferee();
		if(referee.equals("None")){
			featList.add(new Feature("CONTAINS_REFEREE",1));
		}else{
			featList.add(new Feature("CONTAINS_REFEREE",0));
		}
		return featList;
	}

}
