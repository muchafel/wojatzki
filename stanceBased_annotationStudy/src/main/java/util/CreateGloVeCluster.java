package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class CreateGloVeCluster {

	public static void main(String[] args) throws Exception {
		WordVectors wordVectors = null;
		try {
			 wordVectors = WordVectorSerializer.loadTxtVectors(new File("src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.25d.txt"));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String sentence1="i like the cats";
		String sentence2="driving a car is fun";
		String sentence3="driving a bike is fun";
		String sentence4="i like the pussies";
		
		INDArray vec1= wordVectors.getWordVectorsMean(Arrays.asList(sentence1.split(" ")));
		INDArray vec2= wordVectors.getWordVectorsMean(Arrays.asList(sentence2.split(" ")));
		INDArray vec3= wordVectors.getWordVectorsMean(Arrays.asList(sentence3.split(" ")));
		INDArray vec4= wordVectors.getWordVectorsMean(Arrays.asList(sentence4.split(" ")));
		System.out.println(vec1);
		System.out.println(vec2);
		System.out.println(vec3);
		System.out.println(vec4);
		
		/**
		 * transform GloVe files into arffs
		 */
//		File arff=writeARFF(new File("src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.25d.txt"), 25);
		
		Instances data = DataSource.read("src/main/resources/wordEmbeddings/glove.twitter.27B/25.arff"); 
//		Instances data = getData(new File("src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.25d.txt"), 25);
//
//
//	    // create the model 
		SimpleKMeans kMeans = new SimpleKMeans();
	    kMeans.setNumClusters(40);
	    kMeans.buildClusterer(data); 
//
//	    // print out the cluster centroids
	    Instances centroids = kMeans.getClusterCentroids(); 
	    System.out.println(kMeans.getNumClusters());
	    Map<Integer,INDArray> centroidVectors= new HashMap<Integer,INDArray>();
	    for (int i = 0; i < centroids.numInstances(); i++) { 
//	      System.out.println( "Centroid " + i+1 + ": " + centroids.instance(i)); 
	      INDArray centroidVec= getVecOfCentroid(centroids.instance(i),data);
	      centroidVectors.put(i,centroidVec);
	      System.out.println(centroidVec);
	    } 
	    
	    System.out.println("1 cluster "+findClosestCentroid(vec1,centroidVectors));
	    System.out.println("2 cluster "+findClosestCentroid(vec2,centroidVectors));
	    System.out.println("3 cluster "+findClosestCentroid(vec3,centroidVectors));
	    System.out.println("4 cluster "+findClosestCentroid(vec4,centroidVectors));
	    
//
//	    // get cluster membership for each instance 
//	    for (int i = 0; i < data.numInstances(); i++) { 
//	      System.out.println( data.instance(i) + " is in cluster " + kMeans.clusterInstance(data.instance(i)) + 1); 
//
//	    } 
		
//		System.out.println(wordVectors.getWordVector("hate"));
//		System.out.println(wordVectors.similarity("hillary", "bill"));
//		
//		System.out.println(wordVectors.similarity("cat", "dog"));
//		System.out.println(wordVectors.similarity("cat", "lion"));

	}

	/**
	 * find the closest centroid from a list according to euclidean distance between vectors
	 * @param vec1
	 * @param centroidVectors
	 * @return
	 */
	private static int findClosestCentroid(INDArray vec1, Map<Integer, INDArray> centroidVectors) {
		double smallestDiff=1000;
		int currentClossestMatch=99;
		for(int centroidId: centroidVectors.keySet()){
			INDArray centroidVector= centroidVectors.get(centroidId);
			System.out.println("diff to "+centroidId+": "+vec1.distance2(centroidVector));
			if(vec1.distance2(centroidVector)<smallestDiff){
				smallestDiff=vec1.distance2(centroidVector);
				currentClossestMatch=centroidId;
				System.out.println("current selection " +currentClossestMatch);
			}
		}
		return currentClossestMatch;
	}

	private static INDArray getVecOfCentroid(Instance instance, Instances data) {
		double dims[] = new double[data.numAttributes()];
		for (int j = 0; j < data.numAttributes(); j++) {
			dims[j] = instance.value(j);
		} 
		return Nd4j.create(dims);
	}

	private static File writeARFF(File file, int dimensions) throws IOException {
		File arff= new File("src/main/resources/wordEmbeddings/glove.twitter.27B/"+String.valueOf(dimensions)+".arff");
		FileUtils.write(arff, "@relation"+" embeddings"+"\n\n", true);
		for (int i =0;i< dimensions;i++){
			FileUtils.write(arff, "@attribute"+" "+String.valueOf(i)+" "+ "numeric"+"\n", true);
		}
		FileUtils.write(arff,"\n", true);
		FileUtils.write(arff, "@data"+"\n\n", true);
		for(String line:FileUtils.readLines(file)){
			String[] dims= line.split(" ");
			int i=0;
			for(String value: dims){
				if(i>0){
				FileUtils.write(arff, value+",", true);
				}
				i++;
			}
			FileUtils.write(arff, "\n", true);
//			for(int i=0; i<=dims.length; i++){
//				System.out.println(i+" "+String.valueOf(line.split(" ")[i]));
//				if(i>0){
//					if(i<dims.length){
//						FileUtils.write(arff, String.valueOf(i)+" "+String.valueOf(line.split(" ")[i])+",", true);
//					}
//					else{
//						FileUtils.write(arff, String.valueOf(line.split(" ")[i])+"\n", true);
//					}
//				}
//			}
		}
		return arff;
	}

	private static Instances getData(File file, int dimensions) throws IOException {
//		Instances instance= new 
		 @SuppressWarnings("rawtypes")
		FastVector atts = null;
		 atts = new FastVector();
		for (int i =0;i< dimensions;i++){
			atts.addElement(new Attribute("att."+String.valueOf(i)));
		}
		Instances data = new Instances("MyArff", atts, 0);
		
		for(String line:FileUtils.readLines(file)){
			System.out.println(line);
			Instance instance = new DenseInstance(dimensions);
			int i=0;
			String[] dims= line.split(" ");
			for(String dim: line.split(" ")){
				if(i>0){
					System.out.println(String.valueOf(i)+ " "+ String.valueOf(line.split(" ")[i]));
//					instance.setValue("att."+String.valueOf(i), String.valueOf(line.split(" ")[i]));
				}
				data.add(instance);
				i++;
			}
		}
		return data;
	}
}
