package de.uni_due.ltl.simpleClassifications;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import preprocessing.CommentType;
import preprocessing.Users;

public class CommentTypeFE extends FeatureExtractorResource_ImplBase
implements FeatureExtractor {

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		
		CommentType users=JCasUtil.selectCovered(jcas, CommentType.class,target).iterator().next();
		if(users.getCommentNotReply()){
			featList.add(new Feature("COMMENT_TYPE_COMMENT",1));
			featList.add(new Feature("COMMENT_TYPE_REPLY",0));
		}else{
			featList.add(new Feature("COMMENT_TYPE_COMMENT",0));
			featList.add(new Feature("COMMENT_TYPE_REPLY",1));
		}
		return featList;
	}

}
