package de.uni_due.ltl.featureExtractors.explcitVocab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingHelper;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingLexicon;
import de.uni_due.ltl.util.VectorUtil;
import preprocessing.CommentText;

public class TopKLDAEmbeddingDistancePerTargetFE extends TopKLDAWordsPerTargetFE{

	public static final String PARAM_WORD_EMBEDDINGLOCATION = "embeddingsLocation4LDA_DFE";
	@ConfigurationParameter(name = PARAM_WORD_EMBEDDINGLOCATION, mandatory = true)
	private String embeddingsLocation;
	
	private WordEmbeddingLexicon lexicon;
	private WordEmbeddingHelper helper;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		System.out.println("use embedding "+embeddingsLocation);
		try {
			lexicon = new WordEmbeddingLexicon(embeddingsLocation);
			helper=new WordEmbeddingHelper(this.lexicon);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		
		return true;
	}
	
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		List<String> words=getWords(jcas,unit);
		List<String> embeddingCandidates= new ArrayList();
		for(String word:words){
			if(targetVocab.contains(word)){
				embeddingCandidates.add(word);
			}
		}
		List<Double> averagedSentenceVector= helper.getAveragedSentenceVector(embeddingCandidates);
//		Set<Feature> featList = createFeatures(averagedSentenceVector);
		Set<Feature> featList= new HashSet<Feature>();
		
		//TODO: check whether we should only take the words which are in the LDA topics
		List<Double> averagedSentenceVectorWhole= helper.getAveragedSentenceVector(words);
		for(String topLDA: targetVocab){
			List<Double> embedding=lexicon.getEmbedding(topLDA.toLowerCase());
			featList.add(new Feature(target+"_EmbeddingDistance_"+topLDA, VectorUtil.cosineSimilarity(embedding, averagedSentenceVector)));
		}
		return featList;
	}
	
	private Set<Feature> createFeatures(List<Double> averagedVector) {
		Set<Feature> featList = new HashSet<Feature>();
		for(int i=0; i< averagedVector.size(); i++){
			featList.add(new Feature("embeddingDimension_"+i, averagedVector.get(i)));
		}
		return featList;
	}
//	@Override
//	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
//		Set<Feature> featList = new HashSet<Feature>();
//		WordEmbeddingHelper helper=new WordEmbeddingHelper(this.lexicon);
//		List<String> embeddingCandidates=getWords(jcas,unit);
//		
//		List<Double> averagedSentenceVector= helper.getAveragedSentenceVector(embeddingCandidates);
//		for(String topLDA: targetVocab){
//			List<Double> embedding=lexicon.getEmbedding(topLDA.toLowerCase());
//			featList.add(new Feature(target+"_EmbeddingDistance_"+topLDA, VectorUtil.cosineSimilarity(embedding, averagedSentenceVector)));
//		}
//		return featList;
//	}

	/**
	 * TODO: filter for ADJ?
	 * @param jcas
	 * @param unit
	 * @return
	 */
	private List<String> getWords(JCas jcas, TextClassificationTarget unit) {
		List<String> embeddingCandidates= new ArrayList<String>();
		CommentText comment=JCasUtil.selectCovered(CommentText.class, unit).iterator().next();
		//only select the tokens in the current comment
		for(Token t: JCasUtil.selectCovered(jcas, Token.class,comment)){
			String lowerCase = t.getCoveredText().toLowerCase();
			embeddingCandidates.add(lowerCase);
		}
		return embeddingCandidates;
	}
	
}
