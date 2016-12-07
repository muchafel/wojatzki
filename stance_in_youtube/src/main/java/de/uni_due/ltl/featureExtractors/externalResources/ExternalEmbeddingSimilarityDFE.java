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
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingHelper;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingLexicon;
import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.ltl.util.TargetSets;
import de.uni_due.ltl.util.VectorUtil;
import preprocessing.CommentText;

public class ExternalEmbeddingSimilarityDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	public static final String ONLY_CONTENTWORDS = "useOnlyContentWordsExternalEmbeddings";
	@ConfigurationParameter(name = ONLY_CONTENTWORDS, mandatory = true)
	private boolean onlyContentWords;
	
	public static final String PARAM_EXTERNAL_SOURCES_FOLDER_PATH = "externalSourcesFolderPath";
	@ConfigurationParameter(name = PARAM_EXTERNAL_SOURCES_FOLDER_PATH, mandatory = true)
	private String externalSourcesFolder;
	
	public static final String PARAM_USE_SET1 = "useTargetSet1ForExternalEmbeddings";
	@ConfigurationParameter(name = PARAM_USE_SET1, mandatory = true)
	private boolean useSet1;
	
	public static final String PARAM_USE_SET2 = "useTargetSet2ForExternalEmbeddings";
	@ConfigurationParameter(name = PARAM_USE_SET2, mandatory = true)
	private boolean useSet2;
	
	public static final String PARAM_WORDEMBEDDINGLOCATION = "embeddingsLocation";
	@ConfigurationParameter(name = PARAM_WORDEMBEDDINGLOCATION, mandatory = true)
	private String embeddingsLocation;
	
	private WordEmbeddingLexicon wordEmbeddingLexicon;
	private WordEmbeddingHelper wordEmbeddingHelper;
	
	//TODO use n-grams?
	Map<String, List<Double>> idebateSubdebate2ExternalEmbedding=new HashMap<>();
	Map<String, List<Double>> redditSubdebate2ExternalEmbedding=new HashMap<>();
	
	private ArrayList<String> explicitTargets_SET1 = TargetSets.targets_Set1;
	private ArrayList<String> explicitTargets_SET2 = TargetSets.targets_Set2;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
		System.out.println("use embedding "+embeddingsLocation);
		try {
			wordEmbeddingLexicon = new WordEmbeddingLexicon(embeddingsLocation);
			wordEmbeddingHelper=new WordEmbeddingHelper(this.wordEmbeddingLexicon);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		
		if(useSet1){
			explicitTargets_SET1.remove("Death Penalty (Debate)");
			for (String target : explicitTargets_SET1) {
				try {
					if(onlyContentWords){
						redditSubdebate2ExternalEmbedding=addTargetSpecificEmbedding(redditSubdebate2ExternalEmbedding,target,new File(externalSourcesFolder+"redditArgumentations_onlyNVA.txt"));
					}else{
						redditSubdebate2ExternalEmbedding=addTargetSpecificEmbedding(redditSubdebate2ExternalEmbedding,target,new File(externalSourcesFolder+"redditArgumentations.txt"));
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
						idebateSubdebate2ExternalEmbedding=addTargetSpecificEmbedding(idebateSubdebate2ExternalEmbedding,target,new File(externalSourcesFolder+"idebateArgumentations_onlyNVA.txt"));
					}else{
						idebateSubdebate2ExternalEmbedding=addTargetSpecificEmbedding(idebateSubdebate2ExternalEmbedding,target,new File(externalSourcesFolder+"idebateArgumentations.txt"));
					}
				} catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
			}
		}
		return true;
	}
	
	private Map<String, List<Double>> addTargetSpecificEmbedding(
			Map<String, List<Double>> redditSubdebate2ExternalEmbedding, String target, File file) throws IOException {
		List<Double> averagedSentenceVector= wordEmbeddingHelper.initAveragedVector();
		int numberOfSentences=0;
		for(String line : FileUtils.readLines(file)){
			String[] fileContents=line.split("\t");
//			System.out.println(line);
			String targetInLine=fileContents[1];
			String contentInLine=fileContents[0];
			
			if(target.equals(targetInLine)){
				ArrayList<String> embeddingCandidates=new ArrayList<String>(Arrays.asList(contentInLine.split(" ")));
				List<Double> newVec=wordEmbeddingHelper.getAveragedSentenceVector(embeddingCandidates);
				averagedSentenceVector= wordEmbeddingHelper.addVector(averagedSentenceVector, newVec); 
				numberOfSentences++;
			}
		}
		averagedSentenceVector=wordEmbeddingHelper.average(averagedSentenceVector, numberOfSentences);
		redditSubdebate2ExternalEmbedding.put(target, averagedSentenceVector);
		return redditSubdebate2ExternalEmbedding;
	}


	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
//		System.out.println(unit.getCoveredText());
		
		List<String> embeddingCandidates= new ArrayList<String>();
		
		CommentText comment=JCasUtil.selectCovered(CommentText.class, unit).iterator().next();
		for(Token t: JCasUtil.selectCovered(jcas, Token.class,comment)){
			String lowerCase = t.getCoveredText().toLowerCase();
			embeddingCandidates.add(lowerCase);
		}
		List<Double> averagedSentenceVector= wordEmbeddingHelper.getAveragedSentenceVector(embeddingCandidates);
		
		if(useSet1){
			for (String target : explicitTargets_SET1) {
				featList.add(new Feature("ExternalEmbeddingSimilarity_"+target, VectorUtil.cosineSimilarity(averagedSentenceVector, redditSubdebate2ExternalEmbedding.get(target))));
			}
		}
		if(useSet2){
			for (String target : explicitTargets_SET2) {
				featList.add(new Feature("ExternalEmbeddingSimilarity_"+target, VectorUtil.cosineSimilarity(averagedSentenceVector, idebateSubdebate2ExternalEmbedding.get(target))));
			}
		}
		return featList;
	}
}
