package de.uni_due.ltl.featureExtractors.externalResources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.ltl.util.TargetSets;

public class ExternalVocabularyDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	
	public static final String ONLY_CONTENTWORDS = "useOnlyContentWordsExternalVocabulary";
	@ConfigurationParameter(name = ONLY_CONTENTWORDS, mandatory = true)
	private boolean onlyContentWords;
	
	public static final String PARAM_EXTERNAL_SOURCES_FOLDER_PATH = "externalSourcesFolderPath";
	@ConfigurationParameter(name = PARAM_EXTERNAL_SOURCES_FOLDER_PATH, mandatory = true)
	private String externalSourcesFolder;
	
	public static final String PARAM_USE_SET1 = "useTargetSet1ForExternalVocab";
	@ConfigurationParameter(name = PARAM_USE_SET1, mandatory = true)
	private boolean useSet1;
	
	public static final String PARAM_USE_SET2 = "useTargetSet2ForExternalVocab";
	@ConfigurationParameter(name = PARAM_USE_SET2, mandatory = true)
	private boolean useSet2;
	
	public static final String PARAM_MAX_VOCAB = "maxExternalVocab";
	@ConfigurationParameter(name = PARAM_MAX_VOCAB, mandatory = true)
	private int maxVocab;
	
	//TODO use n-grams?
	Map<String, FrequencyDistribution<String>> idebateSubdebate2ExternalVocab=new HashMap<>();
	Map<String, FrequencyDistribution<String>> redditSubdebate2ExternalVocab=new HashMap<>();
	
	private ArrayList<String> explicitTargets_SET1 = TargetSets.targets_Set1;
	private ArrayList<String> explicitTargets_SET2 = TargetSets.targets_Set2;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
		if(useSet1){
			explicitTargets_SET1.remove("Death Penalty (Debate)");
			for (String target : explicitTargets_SET1) {
				try {
					if(onlyContentWords){
						redditSubdebate2ExternalVocab=addTargetSpecificVocab(redditSubdebate2ExternalVocab,target,new File(externalSourcesFolder+"redditArgumentations_onlyNVA.txt"));
					}else{
						redditSubdebate2ExternalVocab=addTargetSpecificVocab(redditSubdebate2ExternalVocab,target,new File(externalSourcesFolder+"redditArgumentations.txt"));
					}
				} catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
			}
		}
		
		if(useSet2){
			for (String target : explicitTargets_SET2) {
				try {
					if(onlyContentWords){
						idebateSubdebate2ExternalVocab=addTargetSpecificVocab(idebateSubdebate2ExternalVocab,target,new File(externalSourcesFolder+"idebateArgumentations_onlyNVA.txt"));
					}else{
						idebateSubdebate2ExternalVocab=addTargetSpecificVocab(idebateSubdebate2ExternalVocab,target,new File(externalSourcesFolder+"idebateArgumentations.txt"));
					}
				} catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
			}
		}
		return true;
	}
	
	private Map<String, FrequencyDistribution<String>> addTargetSpecificVocab(Map<String, FrequencyDistribution<String>> subdebate2ExternalVocab, String target, File file) throws IOException {
		for(String line : FileUtils.readLines(file)){
			String[] fileContents=line.split("\t");
//			System.out.println(line);
			String targetInLine=fileContents[1];
			String contentInLine=fileContents[0];
			if(target.equals(targetInLine)){
				if(subdebate2ExternalVocab.containsKey(target)){
					subdebate2ExternalVocab.get(target).incAll(Arrays.asList(contentInLine.split(" ")));
				}else{
					FrequencyDistribution<String> vocab4Target= new FrequencyDistribution<>();
					vocab4Target.incAll(Arrays.asList(contentInLine.split(" ")));
					subdebate2ExternalVocab.put(target, vocab4Target);
				}
			}
		}
		return subdebate2ExternalVocab;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		List<Token> tokens=JCasUtil.selectCovered(Token.class, unit);
//		System.out.println(unit.getCoveredText());
		if(useSet1){
			for (String target : explicitTargets_SET1) {
				FrequencyDistribution<String> targetFd= redditSubdebate2ExternalVocab.get(target);
				for(String word: targetFd.getMostFrequentSamples(maxVocab)){
					double normalized_VocabCount=getNormalizedCountOfVocab(word,tokens);
					featList.add(new Feature("ExternalVocab_"+target+"_"+word, normalized_VocabCount));
				}
			}
		}
		if(useSet2){
			for (String target : explicitTargets_SET2) {
				FrequencyDistribution<String> targetFd= idebateSubdebate2ExternalVocab.get(target);
				for(String word: targetFd.getMostFrequentSamples(maxVocab)){
					double normalized_VocabCount=getNormalizedCountOfVocab(word,tokens);
					featList.add(new Feature("ExternalVocab_"+target+"_"+word, normalized_VocabCount));
				}
			}
		}
		return featList;
	}

	private double getNormalizedCountOfVocab(String word, List<Token> tokens) {
		double i=0;
		for(Token t: tokens){
			if(t.getCoveredText().toLowerCase().equals(word.toLowerCase())){
				i++;
			}
		}
		double normalized= i/(double)tokens.size();
		return normalized;
	}
}
