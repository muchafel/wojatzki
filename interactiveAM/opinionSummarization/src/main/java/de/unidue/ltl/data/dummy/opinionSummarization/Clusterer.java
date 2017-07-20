package de.unidue.ltl.data.dummy.opinionSummarization;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;
import de.unidue.ltl.util.SimilarityHelper;

public class Clusterer {

	/**
	 * 
	 * @param data
	 * @param clusterStatements
	 * @return
	 * @throws Exception
	 */
	public Map<String, Cluster> cluster(OpinionSummarizationData data, boolean clusterStatements) throws Exception {
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		String[] names=null;
		if(clusterStatements){
			names = data.getStatements().toArray(new String[data.getStatements().size()]);
		}else{
			names = getNames(data.getParticipants());
		}
		System.out.println("instances to cluster "+Arrays.toString(names));
		
		double[][] distances = generateDistanceMatrix(data,clusterStatements,true);
//		System.out.println("DISTANCE MATRIX");
//		for (double[] row : distances){
//		    System.out.println(Arrays.toString(row));
//		}
		Cluster cluster = alg.performClustering(distances, names,new AverageLinkageStrategy());
		cluster.toConsole(0);
		DendrogramPanel dp = new DendrogramPanel();
		dp.setShowScale(false);
		dp.setModel(cluster);
		JFrame frame = new JFrame("Hierarchical Clustering "+clusterStatements);
		frame.add(dp);
		frame.getContentPane().setPreferredSize(new Dimension(500, 400));
		frame.pack();
		frame.setVisible(true);
		
		Map<String,Cluster>levelMapping=getClusterMapping(cluster, new LinkedHashMap<String,Cluster>() );
		return levelMapping;
		
	}

	private Map<String, Cluster> getClusterMapping(Cluster cluster, Map<String, Cluster> hashMap) {
		
		for (Cluster c : cluster.getChildren()) {
//			System.out.println(c.getName()+" "+c.countLeafs()+" "+c.isLeaf());
			hashMap.put(c.getName(), c);
			hashMap.putAll(getClusterMapping(c, hashMap));
		}
		
		return hashMap;
	}

	private String[] getNames(List<Participant> participants) {
		String[] names = new String[participants.size()];
		int i=0;
		for (Participant participant : participants) {
			names[i]=String.valueOf(participant.getId());
			i++;
		}
		
		return names;
	}
/**
 * generates a distance matrix for statements or participants
 * useDistance specifiies if the matrix represents distance or simialriyt
 * @param data
 * @param clusterStatements
 * @param useDistance
 * @return
 * @throws Exception
 */
	private double[][] generateDistanceMatrix(OpinionSummarizationData data, boolean clusterStatements, boolean useDistance) throws Exception {
		if (clusterStatements) {
			List<String>statements=data.getStatements();
			double[][] doublearray = new double[statements.size()][statements.size()];
			int i=0;
			for(String statement_inner: statements){
				int j=0;
				for(String statement_outer: statements){
					double[] vector1=data.getRatingsForStatement(statement_outer);
					double[] vector2=data.getRatingsForStatement(statement_inner);
					doublearray[i][j] = SimilarityHelper.getCosineSimilarity(vector1,vector2,useDistance);
					j++;
				}
				i++;
			}
			return doublearray;
		}
		else{
			List<Participant>participants=data.getParticipants();
			double[][] doublearray = new double[participants.size()][participants.size()];
			int i=0;
			for(Participant participantsInner: participants){
				int j=0;
				for(Participant participantsOuter: participants){
					double[] vector1=data.getRatingsOfParticipant(participantsOuter);
					double[] vector2=data.getRatingsOfParticipant(participantsInner);
					doublearray[i][j] = SimilarityHelper.getCosineSimilarity(vector1, vector2,true);
					j++;
				}
				i++;
			}
			return doublearray;
		}
	}


}
