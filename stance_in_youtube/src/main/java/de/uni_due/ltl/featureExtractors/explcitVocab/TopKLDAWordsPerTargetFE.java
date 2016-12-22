package de.uni_due.ltl.featureExtractors.explcitVocab;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TopKLDAWordsPerTargetFE extends LDA_FeatureExtractor_base {

	public static final String PARAM_VOCAB_TARGET = "SubdebateVocab_Target";
	@ConfigurationParameter(name = PARAM_VOCAB_TARGET, mandatory = true)
	protected String target;

	public static final String PARAM_TOP_K_WORDS = "lda_vocab_topK";
	@ConfigurationParameter(name = PARAM_TOP_K_WORDS, mandatory = true)
	protected int topk;
	
	protected List<String> targetVocab;
	
    
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		this.targetVocab=topK(target2vocab.get(target),topk);
		return true;
	}
	
	
	private List<String> topK(List<String> list, int topk) {
	List<String> topK= new ArrayList<>();
	int i=0;
	for(String word: list){
		if(i==topk) return topK;
		topK.add(word);
		i++;
	}
	return topK;
}


	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		List<Token> tokens=JCasUtil.selectCovered(Token.class, unit);
		int countOfContainedVocab=0;
		for(String word: targetVocab){
			if(contained(tokens,word)){
//				featList.add(new Feature(target+"_Vocab_"+word, 1));
				countOfContainedVocab++;
			}else{
//				featList.add(new Feature(target+"_Vocab_"+word, 0));
			}
		}
		double ratio=(double)countOfContainedVocab/(double)tokens.size();
		featList.add(new Feature("vocabCount_"+target, ratio));

		return featList;
	}
	protected boolean contained(List<Token> tokens, String word) {
		for(Token t: tokens){
			if(t.getCoveredText().toLowerCase().equals(word))return true;
		}
		return false;
	}

}
