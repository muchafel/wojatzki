package de.uni_due.ltl.featureExtractors.explcitVocab;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import preprocessing.CommentText;

public class ExplicitVocabNGram extends BinCasMetaDependent {

	public static final String PARAM_VOCAB_TARGET = "SubdebateVocab_Target";
	@ConfigurationParameter(name = PARAM_VOCAB_TARGET, mandatory = true)
	private String explicitTarget;
	
	public static final String PARAM_Set_NUMBER = "SubdebateVocab_TargetSet";
	@ConfigurationParameter(name = PARAM_Set_NUMBER, mandatory = true)
	private String set;
	
	
	Set<String> highlyAssociatedUniGrams;
	Set<String> highlyAssociatedBiGrams;
	Set<String> highlyAssociatedTriGrams;
//	Set<String> highlyAssociated4Grams;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		try {
			if(set.equals("1")){
				highlyAssociatedUniGrams = getNgramsSet1( 1);
				highlyAssociatedBiGrams = getNgramsSet1(2);
				highlyAssociatedTriGrams = getNgramsSet1(3);
//				highlyAssociated4Grams = getNgramsSet1(4);
			}else{
				highlyAssociatedUniGrams = getNgramsSet2( 1);
				highlyAssociatedBiGrams = getNgramsSet2(2);
				highlyAssociatedTriGrams = getNgramsSet2(3);
//				highlyAssociated4Grams = getNgramsSet2(4);
			}
			
		} catch (ResourceInitializationException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}
	
	
	private Set<String> getNgramsSet1(int i) throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		FrequencyDistribution<String> fd = new FrequencyDistribution<>();

		for (JCas jcas : new JCasIterable(reader)) {
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				for(curated.Explicit_Stance_Set1 explicitTarget1: JCasUtil.selectCovered(curated.Explicit_Stance_Set1.class, sentence)){
					if(explicitTarget1.getTarget().equals(explicitTarget) && !explicitTarget1.getPolarity().equals("NONE")){
						for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, explicitTarget1)), i,
								i)) {
							ngram = lower(ngram);
							String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
							fd.inc(ngramString);
						}
					}
				}
			}
		}
		
		return fd.getKeys();
	}
	
	private Set<String> getNgramsSet2(int i) throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		FrequencyDistribution<String> fd = new FrequencyDistribution<>();

		for (JCas jcas : new JCasIterable(reader)) {
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				for(curated.Explicit_Stance_Set2 explicitTarget2: JCasUtil.selectCovered(curated.Explicit_Stance_Set2.class, sentence)){
					if(explicitTarget2.getTarget().equals(explicitTarget) && !explicitTarget2.getPolarity().equals("NONE")){
						for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, explicitTarget2)), i,
								i)) {
							ngram = lower(ngram);
							String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
							fd.inc(ngramString);
						}
					}
				}
			}
		}
//		Set<String> result= new HashSet();
//		for(String ngram: fd.getKeys()){
//			if(fd.getCount(ngram)>1){
//				result.add(ngram);
//			}
//		}
		return fd.getKeys();
//		return result;
	}

	private FrequencyDistribution<String> getNgrams(List<Token> text, int n_gramSize) {
		FrequencyDistribution<String> annoNgrams = new FrequencyDistribution<String>();

		for (List<String> ngram : new NGramStringListIterable(toText(text), n_gramSize,
				n_gramSize)) {
			ngram = lower(ngram);
			String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
			annoNgrams.inc(ngramString);
		}
		return annoNgrams;
	}
	
	public static List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}
	

	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		List<Token> text=JCasUtil.selectCovered(Token.class,unit);

		FrequencyDistribution<String> fd_uni=getNgrams(text, 1);
		FrequencyDistribution<String> fd_bi=getNgrams(text, 2);
		FrequencyDistribution<String> fd_tri=getNgrams(text, 3);
		FrequencyDistribution<String> fd_4=getNgrams(text, 4);
		for (String word : highlyAssociatedUniGrams) {
			if (fd_uni.contains(word)) {
				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 1));
			} else {
				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 0));
			}
		}
		for (String word : highlyAssociatedBiGrams) {
			if (fd_bi.contains(word)) {
				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 1));
			} else {
				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 0));
			}
		}
		for (String word : highlyAssociatedTriGrams) {
			if (fd_tri.contains(word)) {
				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 1));
			} else {
				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 0));
			}
		}
//		for (String word : highlyAssociated4Grams) {
//			if (fd_4.contains(word)) {
//				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 1));
//			} else {
//				featList.add(new Feature(explicitTarget + "_ExplicitNgram_" + word, 0));
//			}
//		}
		return featList;
	}

}
