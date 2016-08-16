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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import util.wordembeddings.GloVeClusteringConfig;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class CreateGloVeCluster {

	public static void main(String[] args) throws Exception {

		int numberOfClusters = Integer.valueOf(args[0]);
		String embeddingsLocation = args[1];
		boolean writeARFF = Boolean.valueOf(args[2]);
		int embeddingsDimensions = Integer.valueOf(args[3]);
		String arffLocation = args[4];
		int maxIterations = Integer.valueOf(args[5]);
		String centroidLocation = args[6];
		String clusterIdToWordLocation = args[7];
		String wordToCluserLocation = args[8];

		System.out.println("Number of Clusters " + numberOfClusters);
		System.out.println("Max Iterations " + maxIterations);
		System.out.println("Embeddings " + embeddingsLocation);
		if (writeARFF) {
			System.out.println("WriteArff to ");
		}
		GloVeClusteringConfig config = new GloVeClusteringConfig();
		config.setNumberOfClusters(numberOfClusters);
		config.setEmbeddingsLocation(embeddingsLocation);
		config.setEmbeddingsDimensions(embeddingsDimensions);
		config.setArffLocation(arffLocation);
		config.setWriteARFF(writeARFF);
		config.setMaxIterations(maxIterations);
		config.setCentroidLocation(centroidLocation);
		config.setClusterIdToWordLocation(clusterIdToWordLocation);
		config.setWordToCluserLocation(wordToCluserLocation);

		try {

			File file = new File("src/main/resources/config.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(GloVeClusteringConfig.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(config, file);
			jaxbMarshaller.marshal(config, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	// WordVectors wordVectors = null;
	// try {
	// wordVectors = WordVectorSerializer.loadTxtVectors(new
	// File(embeddingsLocation));
	// } catch (FileNotFoundException | UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	//// String sentence1="i like the cats";
	//// String sentence2="driving a car is fun";
	//// String sentence3="driving a bike is fun";
	//// String sentence4="i like the pussies";
	////
	//// INDArray vec1=
	// wordVectors.getWordVectorsMean(Arrays.asList(sentence1.split(" ")));
	//// INDArray vec2=
	// wordVectors.getWordVectorsMean(Arrays.asList(sentence2.split(" ")));
	//// INDArray vec3=
	// wordVectors.getWordVectorsMean(Arrays.asList(sentence3.split(" ")));
	//// INDArray vec4=
	// wordVectors.getWordVectorsMean(Arrays.asList(sentence4.split(" ")));
	//// System.out.println(vec1);
	//// System.out.println(vec2);
	//// System.out.println(vec3);
	//// System.out.println(vec4);
	////
	//
	// File arff=null;
	// /**
	// * transform GloVe files into arffs
	// */
	// if(writeARFF){
	// arff=writeARFF(new File(embeddingsLocation),
	// embeddingsDimensions,arffLocation);
	// System.out.println("done writing arff");
	// }else{
	// arff= new File(arffLocation);
	// }
	// System.out.println("read arff file from "+arff.getAbsolutePath());
	// Instances data = DataSource.read(arff.getAbsolutePath());
	//
	// // create the model
	// System.out.println("start clustering");
	// SimpleKMeans kMeans = new SimpleKMeans();
	// kMeans.setPreserveInstancesOrder(true);
	//
	// kMeans.setNumClusters(numberOfClusters);
	// kMeans.setMaxIterations(maxIterations);
	// kMeans.buildClusterer(data);
	////
	//// // print out the cluster centroids
	// Instances centroids = kMeans.getClusterCentroids();
	// System.out.println(kMeans.getNumClusters());
	// Map<Integer,INDArray> centroidVectors= new HashMap<Integer,INDArray>();
	// for (int i = 0; i < centroids.numInstances(); i++) {
	//// System.out.println( "Centroid " + i+1 + ": " + centroids.instance(i));
	// INDArray centroidVec= getVecOfCentroid(centroids.instance(i),data);
	// centroidVectors.put(i,centroidVec);
	// System.out.println(centroidVec);
	// }
	//
	// System.out.println("print centroids to"+ centroidLocation);
	// printCentroids(centroidVectors,centroidLocation);
	//
	// Map<Integer,List<String>> clusterIdToWords=new
	// HashMap<Integer,List<String>>();
	// Map<String,Integer> wordToCluster=new HashMap<String,Integer>();
	//
	// int counter=0;
	// for(int assignment: kMeans.getAssignments()){
	//// System.out.println(counter+"
	// ("+wordVectors.lookupTable().getVocabCache().elementAtIndex(counter).getLabel()+")
	// : "+assignment);
	// clusterIdToWords=
	// addAssignement(assignment,wordVectors.lookupTable().getVocabCache().elementAtIndex(counter).getLabel(),clusterIdToWords);
	// wordToCluster.put(wordVectors.lookupTable().getVocabCache().elementAtIndex(counter).getLabel(),
	// assignment);
	// counter++;
	// }
	// System.out.println("print assignments to "+clusterIdToWordLocation+ " and
	// "+wordToCluserLocation);
	// printClustersToWords(clusterIdToWordLocation,clusterIdToWords);
	// printWordsToClusters(wordToCluserLocation,wordToCluster);
	// }
	//
	// private static void printWordsToClusters(String clusterIdToWordLocation,
	// Map<String, Integer> wordToCluster) throws IOException {
	// for(String word: wordToCluster.keySet()){
	// FileUtils.write(new File(clusterIdToWordLocation), word+ " >
	// "+wordToCluster.get(word)+"\n", "UTF-8", true);
	// }
	// }
	//
	// private static void printClustersToWords(String clusterIdToWordLocation,
	// Map<Integer, List<String>> clusterIdToWords) throws IOException {
	// for(int id: clusterIdToWords.keySet()){
	// FileUtils.write(new File(clusterIdToWordLocation), id+ " >
	// "+clusterIdToWords.get(id)+"\n", "UTF-8", true);
	// }
	// }
	//
	// private static void printCentroids(Map<Integer, INDArray>
	// centroidVectors, String centroidLocation) throws IOException {
	// for(int id: centroidVectors.keySet()){
	// FileUtils.write(new File(centroidLocation), id+ " >
	// "+centroidVectors.get(id)+"\n", "UTF-8", true);
	// }
	// }
	//
	// private static Map<Integer, List<String>> addAssignement(int assignment,
	// String label,
	// Map<Integer, List<String>> clusterIdToWords) {
	// if(clusterIdToWords.containsKey(assignment)){
	// clusterIdToWords.get(assignment).add(label);
	// }else{
	// List<String> clusteredWords= new ArrayList<>();
	// clusteredWords.add(label);
	// clusterIdToWords.put(assignment, clusteredWords);
	// }
	// return clusterIdToWords;
	// }
	//
	// /**
	// * find the closest centroid from a list according to euclidean distance
	// between vectors
	// * @param vec1
	// * @param centroidVectors
	// * @return
	// */
	// private static int findClosestCentroid(INDArray vec1, Map<Integer,
	// INDArray> centroidVectors) {
	// double smallestDiff=1000;
	// int currentClossestMatch=99;
	// for(int centroidId: centroidVectors.keySet()){
	// INDArray centroidVector= centroidVectors.get(centroidId);
	// System.out.println("diff to "+centroidId+":
	// "+vec1.distance2(centroidVector));
	// if(vec1.distance2(centroidVector)<smallestDiff){
	// smallestDiff=vec1.distance2(centroidVector);
	// currentClossestMatch=centroidId;
	// System.out.println("current selection " +currentClossestMatch);
	// }
	// }
	// return currentClossestMatch;
	// }
	//
	// private static INDArray getVecOfCentroid(Instance instance, Instances
	// data) {
	// double dims[] = new double[data.numAttributes()];
	// for (int j = 0; j < data.numAttributes(); j++) {
	// dims[j] = instance.value(j);
	// }
	// return Nd4j.create(dims);
	// }
	//
	// private static File writeARFF(File embeddingsFile, int dimensions, String
	// arffLocation) throws IOException {
	// File arff= new File(arffLocation);
	// FileUtils.write(arff, "@relation"+" embeddings"+"\n\n", true);
	// for (int i =0;i< dimensions;i++){
	// FileUtils.write(arff, "@attribute"+" "+String.valueOf(i)+" "+
	// "numeric"+"\n", true);
	// }
	// FileUtils.write(arff,"\n", true);
	// FileUtils.write(arff, "@data"+"\n\n", true);
	// for(String line:FileUtils.readLines(embeddingsFile)){
	// String[] dims= line.split(" ");
	// int i=0;
	// for(String value: dims){
	// if(i>0){
	// FileUtils.write(arff, value+",", true);
	// }
	// i++;
	// }
	// FileUtils.write(arff, "\n", true);
	// }
	// return arff;
	// }
}
