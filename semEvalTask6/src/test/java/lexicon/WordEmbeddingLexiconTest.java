package lexicon;

import org.junit.Test;

import lexicons.WordEmbeddingLexicon;
import util.SimilarityHelper;

public class WordEmbeddingLexiconTest {
	
	
	@Test
    public void lexcionReadTest() throws Exception {
		WordEmbeddingLexicon lex = new WordEmbeddingLexicon("src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.100d.txt");
		System.out.println("hillary "+lex.getEmbedding("hillary"));
		System.out.println("trump "+lex.getEmbedding("trump"));
		System.out.println("cat "+lex.getEmbedding("cat"));
		System.out.println("cosine hillary & trump:" + SimilarityHelper.getCosineSimilarity(lex.getEmbedding("hillary"), lex.getEmbedding("trump")));
		System.out.println("cosine hillary & obama:" + SimilarityHelper.getCosineSimilarity(lex.getEmbedding("hillary"), lex.getEmbedding("obama")));
		System.out.println("cosine hillary & cat:" + SimilarityHelper.getCosineSimilarity(lex.getEmbedding("hillary"), lex.getEmbedding("cat")));
		System.out.println("cosine hitler & stalin:" + SimilarityHelper.getCosineSimilarity(lex.getEmbedding("hitler"), lex.getEmbedding("stalin")));
		System.out.println("cosine hitler & flower:" + SimilarityHelper.getCosineSimilarity(lex.getEmbedding("hitler"), lex.getEmbedding("flower")));
	}
}
