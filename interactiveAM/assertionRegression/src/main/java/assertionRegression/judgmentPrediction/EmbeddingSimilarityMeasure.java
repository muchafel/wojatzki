package assertionRegression.judgmentPrediction;

import java.util.Collection;
import java.util.List;

import assertionRegression.wordembeddings.WordEmbeddingHelper;
import assertionRegression.wordembeddings.WordEmbeddingLexicon;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

public class EmbeddingSimilarityMeasure extends TextSimilarityMeasureBase{

 private WordEmbeddingHelper embeddingHelper;	
 private SimilarityHelper similarityHelper;
 private WordEmbeddingLexicon lexicon;
	
	public EmbeddingSimilarityMeasure(String path) {
		try {
			this.lexicon= new WordEmbeddingLexicon(path);
			this.embeddingHelper= new WordEmbeddingHelper(lexicon);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.similarityHelper= new SimilarityHelper();
	}

	@Override
	public double getSimilarity(Collection<String> strings1, Collection<String> strings2) throws SimilarityException {
		List<Double> averagedVector1 =embeddingHelper.getAveragedSentenceVector(strings1);
		List<Double> averagedVector2 =embeddingHelper.getAveragedSentenceVector(strings2);
		
		return similarityHelper.getCosineSimilarity(averagedVector1, averagedVector2);
	}
	
	@Override
	public double getSimilarity(String term1, String term2)
		throws SimilarityException
	{
		List<Double> vector1= lexicon.getEmbedding(term1);
		List<Double> vector2= lexicon.getEmbedding(term2);
		return similarityHelper.getCosineSimilarity(vector1, vector2);
	}
	
}
