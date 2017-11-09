package assertionRegression.wordembeddings;

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

	public List<Double> getAveragedSentenceVector(List<String> embeddingCandidates) {
		List<Double> averagedSentenceVector = initAveragedVector();
		int numberOfEmbeddings = 0;
		for (String lowerCase : embeddingCandidates) {
			// add wordembeddings
			averagedSentenceVector=addVector(averagedSentenceVector,lexicon.getEmbedding(lowerCase));
			numberOfEmbeddings++;
		}
		return average(averagedSentenceVector, numberOfEmbeddings);
	}

	public List<Double> average(List<Double> averagedVector, int numberOfEmbeddings) {
		List<Double> newVec = new ArrayList<Double>();
		for (int i = 0; i < averagedVector.size(); i++) {
			newVec.add(averagedVector.get(i) / numberOfEmbeddings);
		}
		return newVec;
	}

	public List<Double> addVector(List<Double> averagedVector, List<Double> embedding) {
		List<Double> newVec = new ArrayList<Double>();
		for (int i = 0; i < embedding.size(); i++) {
			newVec.add(averagedVector.get(i) + embedding.get(i));
		}
		return newVec;
	}

	public List<Double> initAveragedVector() {
		List<Double> averagedVector = new ArrayList<Double>();
		for (int i = 0; i < lexicon.getDimensionality(); i++) {
			averagedVector.add(0.0);
		}
		return averagedVector;
	}
}
