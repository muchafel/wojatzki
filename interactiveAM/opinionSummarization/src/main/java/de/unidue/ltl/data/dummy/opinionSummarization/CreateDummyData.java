package de.unidue.ltl.data.dummy.opinionSummarization;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import com.apporiented.algorithm.clustering.Cluster;

import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;

public class CreateDummyData {

	public static void main(String[] args) throws Exception {
		RandomDataGenerator generator=new RandomDataGenerator(new Random(),100,50);
		OpinionSummarizationData data =generator.generateOpinionSummarizationData();
//		System.out.println("DATA");
//		for (double[] row : data.getValueMatrix()){
//		    System.out.println(Arrays.toString(row));
//		}
		
		CanabisDataWriter writer= new CanabisDataWriter();
		writer.write(new File("src/main/resources/dummyData_canabis_3.tsv"),data);
//		writer.writeArff(new File("src/main/resources/dummyData_canabis_2.arff"), data);
//		
		QuantitativeStatistics stats= new QuantitativeStatistics();
		stats.calculateAgreement(data);
		
		Clusterer clusterer= new Clusterer();
		Map<String,Cluster> hierarchicalClusters1= clusterer.cluster(data,true);
		stats.calculateAgreement4ClusteredStatetements(hierarchicalClusters1,7,data);
		
		Map<String,Cluster> hierarchicalClusters2= clusterer.cluster(data,false);
		stats.calculateStats4ClusteredParticipants(hierarchicalClusters2,7,data);
		
	}

}
