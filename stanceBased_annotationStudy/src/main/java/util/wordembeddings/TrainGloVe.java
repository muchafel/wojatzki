package util.wordembeddings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TrainGloVe {

	public static void main(String[] args) throws Exception {

		int dimensions=75;
		
		 File inputFile = new File("/Users/michael/Desktop/atheism2013-12.txt");

	        // creating SentenceIterator wrapping our training corpus
	        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());

	        // Split on white spaces in the line to get words
	        TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());

	        Glove glove = new Glove.Builder()
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .alpha(0.75)
	                .learningRate(0.1)

	                //set dimensions
	                .layerSize(dimensions)
	                
	                // number of epochs for training
	                .epochs(25)

	                // cutoff for weighting function
	                .xMax(100)

	                // training is done in batches taken from training corpus
	                .batchSize(1000)

	                // if set to true, batches will be shuffled before training
	                .shuffle(true)

	                // if set to true word pairs will be built in both directions, LTR and RTL
	                .symmetric(true)
	                .build();

	        glove.fit();
	        
	    WordVectorSerializer.writeWordVectors(glove, "atheismEmbeddings_"+String.valueOf(dimensions)+".txt");

//		WordVectors wordVectors = null;
//		try {
//			wordVectors = WordVectorSerializer.loadTxtVectors(new File("atheismEmbeddings.txt"));
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
	}
}
