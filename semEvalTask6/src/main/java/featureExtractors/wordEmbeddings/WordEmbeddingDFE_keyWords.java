package featureExtractors.wordEmbeddings;

import java.io.IOException;
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
import featureExtractors.SummedStance_base;
import featureExtractors.BinCasMetaDependent.RelevantTokens;
import lexicons.WordEmbeddingLexicon;
import util.wordEmbeddingUtil.WordEmbeddingHelper;

public class WordEmbeddingDFE_keyWords extends SummedStance_base {
	private WordEmbeddingLexicon lexicon;
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
			lexicon = new WordEmbeddingLexicon(
					"src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.50d.txt");
			System.out.println("word embedding with "+lexicon.getDimensionality()+ " dimensions");
			
			if (useStances) {
				wordStanceLexicon = readLexicon(binCasDir,RelevantTokens.SENTENCE);
			}

		} catch (IOException | UIMAException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
//		System.out.println(jcas.getDocumentText());
		WordEmbeddingHelper helper=new WordEmbeddingHelper(this.lexicon);
		Set<String> embeddingCandidates= new HashSet<String>();
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if (useStances) {
				String lowerCase= t.getCoveredText().toLowerCase();
				float stance= wordStanceLexicon.getStance(t.getCoveredText());
				if(stance>0 && !stopwords.contains(lowerCase) && (t.getPos().getPosValue().equals("NN")
						|| t.getPos().getPosValue().equals("NNP")|| t.getPos().getPosValue().equals("NNS") || t.getPos().getPosValue().equals("NNPS"))){
					embeddingCandidates.add(lowerCase);
				}
			}
		}
//		System.out.println(embeddingCandidates);
		List<Float> averagedSentenceVector= helper.getAveragedSentenceVector(embeddingCandidates);
		Set<Feature> featList = createFeatures(averagedSentenceVector);
		return featList;
	}
	private Set<Feature> createFeatures(List<Float> averagedVector) {
		Set<Feature> featList = new HashSet<Feature>();
		for(int i=0; i< averagedVector.size(); i++){
			featList.add(new Feature("embeddingDimension_"+i, averagedVector.get(i)));
		}
		return featList;
	}
}
