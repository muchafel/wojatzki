package de.uni_due.ltl.featureExtractors.userModel;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import preprocessing.Users;

public class RecurrentAuthor extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		Users users=JCasUtil.selectCovered(jcas, Users.class,unit).iterator().next();
		String author = users.getAuthor();
//		System.out.println("examine "+unit.getCoveredText());
		if(userOcurredBefore(author,jcas,unit.getAddress())){
			featList.add(new Feature("RECURRENT_USER",1));
		}else{
			featList.add(new Feature("RECURRENT_USER",0));
		}
		return featList;
	}

	/**
	 * check whether the author already commented before
	 * TODO use counts (#of previous occurrences) instead of boolean?
	 * @param author
	 * @param jcas
	 * @param unitAdress
	 * @return
	 */
	private boolean userOcurredBefore(String author, JCas jcas, int unitAdress) {
		for(TextClassificationTarget unit: JCasUtil.select(jcas, TextClassificationTarget.class)){
//			System.out.println("\t"+unit.getCoveredText());
			//break if we reach the same unit (indicated by address) //TODO check if we need something else here (id)/ but seems to work
			if(unit.getAddress()==unitAdress){
				break;
			}
			if(JCasUtil.selectCovered(Users.class,unit).iterator().next().getAuthor().equals(author)){
//				System.out.println("found recurrent user in "+unit.getAddress()+" "+unit.getCoveredText());
				return true;
			}
		}
		return false;
	}
}
