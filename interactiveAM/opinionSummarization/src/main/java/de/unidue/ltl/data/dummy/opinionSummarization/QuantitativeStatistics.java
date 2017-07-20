package de.unidue.ltl.data.dummy.opinionSummarization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.apporiented.algorithm.clustering.Cluster;

import de.unidue.ltl.core.data.opinionSummarization.CanabisParticipant;
import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;

public class QuantitativeStatistics {

	public void calculateAgreement(OpinionSummarizationData data) throws Exception {
		Map<String,Integer> statementToAgreement=getStatementToAgreement(data);
		statementToAgreement= sort(statementToAgreement);
		for (String key: statementToAgreement.keySet()){
			System.out.println(key+" : "+statementToAgreement.get(key)/statementToAgreement.size());
		}
	}

	private Map<String, Integer> sort(Map<String, Integer> statementToAgreement) {
		return statementToAgreement.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
	}

	private Map<String, Integer> getStatementToAgreement(OpinionSummarizationData data) throws Exception {
		Map<String,Integer> result= new HashMap<String, Integer>();
		for(String statement: data.getStatements()){
			result.put(statement, count(data.getRatingsForStatement(statement)));
		}
		
		return result;
	}

	private int count(double[] ratingsForStatement) {
		int result=0;
		for (double d : ratingsForStatement) {
			if(d==1.0){
				result++;
			}
		}
		return result;
	}


	public void calculateStats4ClusteredParticipants(Map<String, Cluster> hierarchicalClusters, int i, OpinionSummarizationData data) {
		int j=0;
		for(String key: hierarchicalClusters.keySet()){
			if(j==i){
				break;
			}else{
				j++;
				statsForCluster(hierarchicalClusters.get(key),data);
			}
		}
		
	}

	private void statsForCluster(Cluster cluster, OpinionSummarizationData data) {
		System.out.println(cluster.getName()+" "+cluster.countLeafs());
		List<CanabisParticipant> participants= getAllContainedParticipants(cluster,data);
		double percentageFemale=0;
		double percentageSmoker=0;
		double conservativePercentage=0;
		double averageAge=0;
		for(CanabisParticipant p: participants){
			if(p.getGender().equals("female")){
				percentageFemale++;
			}
			if(p.isSmoker()){
				percentageSmoker++;
			}
			if(p.isConservativeOrLiberal()){
				conservativePercentage++;
			}
			averageAge+=p.getAge();
		}
		System.out.println("percentageFemale "+percentageFemale/participants.size()+" percentageSmoker "+percentageSmoker/participants.size()+" conservativePercentage "+conservativePercentage/participants.size()+" averageAge "+averageAge/participants.size());
		
	}

	private List<CanabisParticipant> getAllContainedParticipants(Cluster c, OpinionSummarizationData data) {
		List<Cluster> leafs= getAllLeafs(c);
		return mapLeafsToParticipants(leafs,data);
	}

	private List<Cluster> getAllLeafs(Cluster c) {
		List<Cluster> leafNodes = new ArrayList<>();
		if (c.isLeaf()) {
			leafNodes.add(c);
		} else {
			for (Cluster child : c.getChildren()) {
				leafNodes.addAll(getAllLeafs(child));
			}
		}
		return leafNodes;
	}

	private List<CanabisParticipant> mapLeafsToParticipants(List<Cluster> leafs, OpinionSummarizationData data) {
		List<CanabisParticipant> result= new ArrayList<>();
		
		for(Participant p : data.getParticipants()){
			for(Cluster leaf: leafs){
				if(leaf.getName().equals(String.valueOf(p.getId()))){
					result.add((CanabisParticipant) p);
				}
			}
		}
		return result;
	}

	
	

	public void calculateAgreement4ClusteredStatetements(Map<String, Cluster> hierarchicalClusters, int i,
			OpinionSummarizationData data) throws Exception {
		int j=0;
		for(String key: hierarchicalClusters.keySet()){
			if(j==i){
				break;
			}else{
				j++;
				agreementForCluster(hierarchicalClusters.get(key),data);
			}
		}
		
	}

	private void agreementForCluster(Cluster cluster, OpinionSummarizationData data) throws Exception {
		System.out.println(cluster.getName()+" "+cluster.countLeafs());
		List<String> statements= getAllContainedStatements(cluster,data);
		int count=0;
		for(String st: statements){
			count+=count(data.getRatingsForStatement(st));
		}
		System.out.println("agreement for cluster "+cluster.getName()+" "+count/statements.size()/data.getParticipants().size());
	}

	private List<String> getAllContainedStatements(Cluster cluster, OpinionSummarizationData data) {
		List<Cluster> leafs= getAllLeafs(cluster);
		return mapLeafsToStatements(leafs, data);
	}

	private List<String> mapLeafsToStatements(List<Cluster> leafs, OpinionSummarizationData data) {
		List<String> result= new ArrayList<>();
		
		for(String st : data.getStatements()){
			for(Cluster leaf: leafs){
				if(leaf.getName().equals(String.valueOf(st))){
					result.add(st);
				}
			}
		}
		return result;
	}


}
