package util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.generic.AASTORE;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import dataInspection.SentimentInspector;
import dataInspection.WordEmbediddingInspector;
import io.TaskATweetReader;
import lexicons.WordEmbeddingLexicon;
import util.wordEmbeddingUtil.WordEmbeddingHelper;
/**
 * example from Kusner, M. J., Sun, E. Y., Kolkin, E. N. I., & EDU, W.(2015) From Word Embeddings To Document Distances.
 * @author michael
 *
 */
public class WordEmbeddingHelperTest {
	
	@Test
    public void helperTest() throws Exception {
		WordEmbeddingLexicon lexicon = null;
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		List<Float> testVec= new ArrayList<Float>(Arrays.asList(2.0f,2.0f,2.0f,2.0f));
		System.out.println(testVec);
		System.out.println(helper.average(testVec, 2));
		Assert.assertEquals(helper.average(testVec, 2), new ArrayList<Float>(Arrays.asList(1.0f,1.0f,1.0f,1.0f)));
		
	}
	
	@Test
    public void helperTest2() throws Exception {
		Map<String, List<Float>> map = new HashMap<String, List<Float>>();
		map.put("a", new ArrayList<Float>(Arrays.asList(1.0f,1.0f,1.0f,1.0f)));
		map.put("b", new ArrayList<Float>(Arrays.asList(1.0f,1.0f,1.0f,1.0f)));
		map.put("c", new ArrayList<Float>(Arrays.asList(1.0f,1.0f,1.0f,1.0f)));
		WordEmbeddingLexicon lexicon = new WordEmbeddingLexicon(map);
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		Set<String> w1 = new HashSet<String>(Arrays.asList("a", "b", "c"));
		System.out.println(w1+" avg: "+helper.getAveragedSentenceVector(w1));
		Assert.assertEquals(helper.getAveragedSentenceVector(w1), new ArrayList<Float>(Arrays.asList(1.0f,1.0f,1.0f,1.0f)));
	}
	@Test
    public void wordEmbeddingTest() throws Exception {
		WordEmbeddingLexicon lexicon = new WordEmbeddingLexicon("src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.100d.txt");
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		Set<String> w1 = new HashSet<String>(Arrays.asList("obama", "speaks", "media", "illinois"));
		Set<String> w2 = new HashSet<String>(Arrays.asList("president", "greets", "press","chicago"));
		Set<String> w3 = new HashSet<String>(Arrays.asList("band", "gave", "concert","japan"));
		
		List<Float> v1= helper.getAveragedSentenceVector(w1);
		System.out.println(v1);
		List<Float> v2= helper.getAveragedSentenceVector(w2);
		System.out.println(v2);
		List<Float> v3= helper.getAveragedSentenceVector(w3);
		System.out.println(w1+" "+ w2+ " "+SimilarityHelper.getCosineSimilarity(v1,v2));
		System.out.println(w1+" "+ w3+ " "+SimilarityHelper.getCosineSimilarity(v1,v3));
		System.out.println(w2+" "+ w3+ " "+SimilarityHelper.getCosineSimilarity(v2,v3));
	}
}
