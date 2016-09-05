package util.wordembeddings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class TrainWord2Vec {
	public static void main(String[] args) throws IOException {
		File inputFile = new File("/Users/michael/Desktop/TwitterSearcher/atheism2015.txt");

		 int dimensions=75;
	     // creating SentenceIterator wrapping our training corpus
	     SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());

	     // Split on white spaces in the line to get words
	     TokenizerFactory t = new DefaultTokenizerFactory();
	     t.setTokenPreProcessor(new CommonPreprocessor());
		
	    Word2Vec vec = new Word2Vec.Builder()
	            .minWordFrequency(1)
	            .iterations(5)
	            .layerSize(dimensions)
	            .seed(42)
	            .windowSize(5)
	            .iterate(iter)
	            .tokenizerFactory(t)
	            .build();

	    vec.fit();
	    
	  
		WordVectorSerializer.writeWordVectors(vec, "src/main/resources/wordEmbeddings/atheismWord2VecEmbeddings_"+String.valueOf(dimensions)+".txt");
	}
}
