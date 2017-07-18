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
			System.out.println(key+" : "+statementToAgreement.get(key));
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
		List<Cluster> leafs= getAllLeafs(c, new ArrayList<Cluster>());
		return mapLeafsToParticipants(leafs,data);
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

	private List<Cluster> getAllLeafs(Cluster c, ArrayList<Cluster> arrayList) {
		if(c.isLeaf()){
			arrayList.add(c);
		}
		
		for(Cluster child: c.getChildren()){
			if(child.isLeaf()){
				arrayList.add(child);
			}else{
				arrayList.addAll(getAllLeafs(child,arrayList));
			}
		}
		return arrayList;
	}


}
