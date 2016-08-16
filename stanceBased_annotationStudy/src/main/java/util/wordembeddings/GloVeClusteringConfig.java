package util.wordembeddings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GloVeClusteringConfig {

	int numberOfClusters;
	String embeddingsLocation;
	boolean writeARFF;
	int embeddingsDimensions;
	String arffLocation;
	int maxIterations;
	String centroidLocation;
    String clusterIdToWordLocation;
	String wordToCluserLocation;
	
	public int getNumberOfClusters() {
		return numberOfClusters;
	}
	@XmlElement
	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}
	public String getEmbeddingsLocation() {
		return embeddingsLocation;
	}
	@XmlElement
	public void setEmbeddingsLocation(String embeddingsLocation) {
		this.embeddingsLocation = embeddingsLocation;
	}
	public boolean isWriteARFF() {
		return writeARFF;
	}
	@XmlElement
	public void setWriteARFF(boolean writeARFF) {
		this.writeARFF = writeARFF;
	}
	public int getEmbeddingsDimensions() {
		return embeddingsDimensions;
	}
	@XmlElement
	public void setEmbeddingsDimensions(int embeddingsDimensions) {
		this.embeddingsDimensions = embeddingsDimensions;
	}
	public String getArffLocation() {
		return arffLocation;
	}
	@XmlElement
	public void setArffLocation(String arffLocation) {
		this.arffLocation = arffLocation;
	}
	public int getMaxIterations() {
		return maxIterations;
	}
	@XmlElement
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
	public String getCentroidLocation() {
		return centroidLocation;
	}
	@XmlElement
	public void setCentroidLocation(String centroidLocation) {
		this.centroidLocation = centroidLocation;
	}
	public String getClusterIdToWordLocation() {
		return clusterIdToWordLocation;
	}
	@XmlElement
	public void setClusterIdToWordLocation(String clusterIdToWordLocation) {
		this.clusterIdToWordLocation = clusterIdToWordLocation;
	}
	public String getWordToCluserLocation() {
		return wordToCluserLocation;
	}
	@XmlElement
	public void setWordToCluserLocation(String wordToCluserLocation) {
		this.wordToCluserLocation = wordToCluserLocation;
	}
	
}
