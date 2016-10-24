package featureExtractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class WordEmbeddingHelper {
	private WordEmbeddingLexicon lexicon;

	public WordEmbeddingHelper(WordEmbeddingLexicon lexicon) {
		this.lexicon = lexicon;
	}

	public List<Float> getAveragedSentenceVector(Set<String> embeddingCandidates) {
		List<Float> averagedSentenceVector = initAveragedVector();
		int numberOfEmbeddings = 0;
		for (String lowerCase : embeddingCandidates) {
			// add wordembeddings
			averagedSentenceVector=addVector(averagedSentenceVector,lexicon.getEmbedding(lowerCase));
			numberOfEmbeddings++;
		}
		return average(averagedSentenceVector, numberOfEmbeddings);
	}

	public List<Float> average(List<Float> averagedVector, int numberOfEmbeddings) {
		List<Float> newVec = new ArrayList<Float>();
		for (int i = 0; i < averagedVector.size(); i++) {
			newVec.add(averagedVector.get(i) / numberOfEmbeddings);
		}
		return newVec;
	}

	private List<Float> addVector(List<Float> averagedVector, List<Float> embedding) {
		List<Float> newVec = new ArrayList<Float>();
		for (int i = 0; i < embedding.size(); i++) {
			newVec.add(averagedVector.get(i) + embedding.get(i));
		}
		return newVec;
	}

	private List<Float> initAveragedVector() {
		List<Float> averagedVector = new ArrayList<Float>();
		for (int i = 0; i < lexicon.getDimensionality(); i++) {
			averagedVector.add(0.0f);
		}
		return averagedVector;
	}
}
