package featureExtractors.wordEmbeddings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import featureExtractors.stanceLexicon.SummedStance_base;
import featureExtractors.stanceLexicon.BinCasMetaDependent.RelevantTokens;
import lexicons.WordEmbeddingLexicon;
import types.StanceAnnotation;
import util.SimilarityHelper;
import util.wordEmbeddingUtil.WordEmbeddingHelper;

public class WordEmbeddingDFE_topConcepts_cosinus extends SummedStance_base{

	private WordEmbeddingLexicon lexicon;
	private List<String> stopwords;
	private Map<String,List<Float>> keyConeptsVectors;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		lexicon = new WordEmbeddingLexicon(
				"src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.100d.txt");
		System.out.println("word embedding with "+lexicon.getDimensionality()+ " dimensions");
		
		try {
			stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			keyConeptsVectors= readKeyConceptsVector(binCasDir,lexicon,stopwords);
			System.out.println("key concepts "+keyConeptsVectors);
		} catch (UIMAException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}


	/**
	 * read in a file and return a list of strings
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected List<String> init(String path) throws IOException {
		List<String> stopwords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
		}
		return stopwords;
	}
	
	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		
		WordEmbeddingHelper helper=new WordEmbeddingHelper(this.lexicon);
		Set<String> embeddingCandidates= new HashSet<String>();
		Set<Feature> features = new HashSet<Feature>();
		
		for(Token t: JCasUtil.select(jcas, Token.class)){
			//filter stopwords and punctuations
			String lowerCase = t.getCoveredText().toLowerCase();
			if (!stopwords.contains(lowerCase) || !t.getPos().getPosValue().equals(",")
					|| !t.getPos().getPosValue().equals(".") ||! t.getPos().getPosValue().equals("$")
					|| !t.getPos().getPosValue().equals("'") ||! t.getPos().getPosValue().equals(":")){
				embeddingCandidates.add(lowerCase);
			}
		}
//		System.out.println(embeddingCandidates);
		List<Float> averagedSentenceVector= helper.getAveragedSentenceVector(embeddingCandidates);
		for(String concept: keyConeptsVectors.keySet()){
			features.add(new Feature(concept, SimilarityHelper.getCosineSimilarity(averagedSentenceVector,keyConeptsVectors.get(concept))));
		}
		return features;
	}

}
