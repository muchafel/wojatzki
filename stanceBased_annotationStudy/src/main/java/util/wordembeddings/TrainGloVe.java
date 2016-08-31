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
import org.deeplearning4j.plot.BarnesHutTsne;
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

		int dimensions=50;
		double alpha= 0.75;
		double learningRate=0.1;
		int epochs=25;
		int xMax=100;
		int batchSize=1000;
		
		 File inputFile = new File("/Users/michael/Desktop/atheism2013-12.txt");

	        // creating SentenceIterator wrapping our training corpus
	        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());

	        // Split on white spaces in the line to get words
	        TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());

	        Glove glove = new Glove.Builder()
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .alpha(alpha)
	                .learningRate(learningRate)

	                //set dimensions
	                .layerSize(dimensions)
	                
	                // number of epochs for training
	                .epochs(epochs)

	                // cutoff for weighting function
	                .xMax(xMax)

	                // training is done in batches taken from training corpus
	                .batchSize(batchSize)

	                // if set to true, batches will be shuffled before training
	                .shuffle(true)

	                // if set to true word pairs will be built in both directions, LTR and RTL
	                .symmetric(true)
	                .build();

	        glove.fit();
	        System.out.println("Training Done");
	        
	    WordVectorSerializer.writeWordVectors(glove, "atheismGloVeEmbeddings_"+String.valueOf(dimensions)+".txt");

//        BarnesHutTsne tsne = new BarnesHutTsne.Builder()
//                .setMaxIter(1000)
//                .stopLyingIteration(250)
//                .learningRate(500)
//                .useAdaGrad(false)
//                .theta(0.5)
//                .setMomentum(0.5)
//                .normalize(true)
//                .usePca(false)
//                .build();
//        glove.lookupTable().plotVocab(tsne,500, new File("test_TSNE"));
        System.out.println("Plotting Done");
	    
//		WordVectors wordVectors = null;
//		try {
//			wordVectors = WordVectorSerializer.loadTxtVectors(new File("atheismEmbeddings.txt"));
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
	}
}
