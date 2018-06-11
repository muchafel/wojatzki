package de.unidue.ltl.data.dummy.opinionSummarization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.apporiented.algorithm.clustering.Cluster;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.unidue.ltl.core.data.opinionSummarization.CanabisParticipant;
import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;

public class QuantitativeStatistics {
	
	
	public File adFile;
	public File soFile;
	
	public QuantitativeStatistics() throws IOException{
		adFile= new File("src/main/resources/agreement/agreement.tsv");
//		FileUtils.writeStringToFile(adFile, "ID\tassertion\tagreement score\tissue\n", "UTF-8", true);
		soFile= new File("src/main/resources/bws/supportOppose.tsv");
//		FileUtils.writeStringToFile(soFile, "ID\tassertion\tsupport-oppose score\tmost-least support score\tmost least oppose score\tissue\n", "UTF-8", true);
	}

	public void calculateAgreement(OpinionSummarizationData data) throws Exception {
		File agreementFile= new File("src/main/resources/agreement/"+data.getIssue());
		FileUtils.writeStringToFile(agreementFile, "assertion"+"\t"+"agreement score"+"\n", "UTF-8",true);

		Map<String,Double> statementToAgreement=getStatementToAgreement(data);
		statementToAgreement= sort(statementToAgreement);
		double polarizationScore=0.0;
		for (String key: statementToAgreement.keySet()){
			System.out.println(key+"\t"+statementToAgreement.get(key));
			polarizationScore+=statementToAgreement.get(key);
			FileUtils.writeStringToFile(agreementFile, data.getAssertion2Id().get(key)+"\t"+key+"\t"+statementToAgreement.get(key)+"\n", "UTF-8",true);
			FileUtils.writeStringToFile(adFile, data.getAssertion2Id().get(key+"\t"+data.getIssue())+"\t"+key+"\t"+statementToAgreement.get(key)+"\t"+data.getIssue()+"\n", "UTF-8",true);
		}
		System.out.println("---");
		System.out.println("("+data.getIssue()+","+(1-polarizationScore/(double)statementToAgreement.size())+")");
//		System.out.println("polarization score "+(1-polarizationScore/(double)statementToAgreement.size()));

	}

	private Map<String, Double> sort(Map<String, Double> statementToAgreement) {
		return statementToAgreement.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
	}

	private Map<String, Double> getStatementToAgreement(OpinionSummarizationData data) throws Exception {
		Map<String,Double> result= new HashMap<String, Double>();
		for(String statement: data.getStatements()){
			result.put(statement, score(data.getRatingsForAssertion(statement)));
		}
		
		return result;
	}

	private double score(double[] ratingsForStatement) {
		double valuePos = 0;
		double valueNeg = 0;
		int ratings=0;
		for (double d : ratingsForStatement) {
			if(d==1.0 ){
				valuePos++;
				ratings++;
			}
			if(d==-1.0){
				valueNeg++;
				ratings++;
			}
		}
		return valuePos/(double)ratings-valueNeg/(double)ratings;
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
		double count=0;
		for(String st: statements){
			count+=score(data.getRatingsForAssertion(st));
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

	public void calculateSupportOppose(OpinionSummarizationData data) throws Exception {
		
		File supportFile= new File("src/main/resources/bws/"+data.getIssue());
//		FileUtils.writeStringToFile(supportFile, "assertion"+"\t"+"support-oppose"+"\t"+"most suppport-least support"+"\t"+"most suppport-least oppose"+"\n", "UTF-8", true);

		
		//count scores per assertion 
		Map<String, Double> resultBest= new HashMap<>();
		Map<String, Double> result_worst= new HashMap<>();
		
		//filtered according to agree disagree
		Map<String, Double> result_leastSupport= new HashMap<>();
		Map<String, Double> result_mostOppose= new HashMap<>();
		Map<String, Double> result_mostSupport= new HashMap<>();
		Map<String, Double> result_leastOppose= new HashMap<>();
		Map<String, Double> assertionToAgree= new HashMap<>();
		Map<String, Double> assertionToDisAgree= new HashMap<>();
		
		//iterate over worst scores
		for(int id: data.getId2AssertionToWorst().keySet()){
			for(String assertion: data.getId2AssertionToWorst().get(id).keySet()){
				if(result_worst.containsKey(assertion)){
					double newV= result_worst.get(assertion)+data.getId2AssertionToWorst().get(id).get(assertion);
					result_worst.put(assertion, newV);
				}else{
					result_worst.put(assertion, (double)data.getId2AssertionToWorst().get(id).get(assertion));
				}
				//if assertion is agreed worst becomes least support else most oppose
				if(agree(id,assertion,data)){
					assertionToAgree=increment(assertionToAgree,assertion);
					if(result_leastSupport.containsKey(assertion)){
						result_leastSupport.put(assertion, result_leastSupport.get(assertion)+data.getId2AssertionToWorst().get(id).get(assertion));
					}else{
						result_leastSupport.put(assertion,(double) data.getId2AssertionToWorst().get(id).get(assertion));
					}
				}else if(disagree(id,assertion,data)){
					assertionToDisAgree=increment(assertionToDisAgree,assertion);
					if(result_mostOppose.containsKey(assertion)){
						result_mostOppose.put(assertion, result_mostOppose.get(assertion)+data.getId2AssertionToWorst().get(id).get(assertion));
					}else{
						result_mostOppose.put(assertion, (double)data.getId2AssertionToWorst().get(id).get(assertion));
					}
				}
			}
		}
		
		//iterate over best scores
		for(int id: data.getId2AssertionToBest().keySet()){
			for(String assertion: data.getId2AssertionToBest().get(id).keySet()){
				if(resultBest.containsKey(assertion)){
					double newV= resultBest.get(assertion)+data.getId2AssertionToBest().get(id).get(assertion);
					resultBest.put(assertion, newV);
				}else {
					resultBest.put(assertion, (double)data.getId2AssertionToBest().get(id).get(assertion));
				}
				//if assertion is agreed best becomes most support else least oppose
				if(agree(id,assertion,data)){
					if(result_mostSupport.containsKey(assertion)){
						result_mostSupport.put(assertion, result_mostSupport.get(assertion)+data.getId2AssertionToBest().get(id).get(assertion));
					}else{
						result_mostSupport.put(assertion,(double) data.getId2AssertionToBest().get(id).get(assertion));
					}
				}else if(disagree(id,assertion,data)){
					if(result_leastOppose.containsKey(assertion)){
						result_leastOppose.put(assertion, result_leastOppose.get(assertion)+data.getId2AssertionToBest().get(id).get(assertion));
					}else{
						result_leastOppose.put(assertion, (double)data.getId2AssertionToBest().get(id).get(assertion));
					}
				}
			}
		}

		Map<String,Double> statementToBWSScore= new HashMap<>();
		Map<String,Double> statementToMostLeastSupportScore= new HashMap<>();
		Map<String,Double> statementToMostLeastOpposeScore= new HashMap<>();
		
		//iterate another time to combine the scores
		for(String assertion: data.getStatements()){
//			System.out.println(assertion+" "+resultBest.get(assertion)+ " "+result_worst.get(assertion));
//			System.out.println(assertion+" "+result_mostSupport.get(assertion)+ " "+result_leastSupport.get(assertion));
//			System.out.println(assertion+" "+result_mostOppose.get(assertion)+ " "+result_leastOppose.get(assertion));

			double supportOpposeScore=percentageDiff(resultBest.get(assertion),result_worst.get(assertion));
			
			double mostLeastSupportScore=percentageDiff(result_mostSupport.get(assertion),result_leastSupport.get(assertion));
			double mostLeastOpposeScore=percentageDiff(result_mostOppose.get(assertion),result_leastOppose.get(assertion));
			
//			System.out.println("before "+mostLeastSupportScore);
			mostLeastSupportScore=normalize(mostLeastSupportScore);
//			System.out.println("after "+mostLeastSupportScore);
			mostLeastOpposeScore=normalize(mostLeastOpposeScore);
			
//			System.out.println(assertion+" "+resultBest.get(assertion)+ " "+result_worst.get(assertion)+" "+supportOpposeScore);
			statementToBWSScore.put(assertion, supportOpposeScore);
			statementToMostLeastSupportScore.put(assertion, mostLeastSupportScore);
			statementToMostLeastOpposeScore.put(assertion,mostLeastOpposeScore);
		}
		
		statementToBWSScore= sort(statementToBWSScore);
		
		//iterate to write them
		for (String key: statementToBWSScore.keySet()){
			System.out.println(key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key));
//			FileUtils.writeStringToFile(supportFile, data.getAssertion2Id().get(key)+"\t"+key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key)+"\n", "UTF-8", true);
			FileUtils.writeStringToFile(soFile, data.getAssertion2Id().get(key+"\t"+data.getIssue())+"\t"+key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key)+"\t"+computeControversyScore(statementToMostLeastSupportScore.get(key),statementToMostLeastOpposeScore.get(key),assertionToAgree.get(key),assertionToDisAgree.get(key))+"\t"+data.getIssue()+"\n", "UTF-8",true);
//			FileUtils.writeStringToFile(soFile, data.getAssertion2Id().get(key+"\t"+data.getIssue())+"\t"+key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key)+"\t"+data.getIssue()+"\n", "UTF-8", true);
		}
		
	}

	private Double computeControversyScore(Double supportScore, Double opposeScore, Double cAgree, Double cDisagree) {
		if(cAgree==null){
			cAgree=0.0;
		}
		if(cDisagree==null){
			cDisagree=0.0;
		}
		double sum= cAgree+ cDisagree;
		supportScore=(cAgree/sum)*supportScore;
		opposeScore=(cDisagree/sum)*opposeScore;
		return supportScore+opposeScore;
	}

	private Map<String, Double> increment(Map<String, Double> assertionToAgree,String assertion) {
		if(assertionToAgree.containsKey(assertion)){
			double value= assertionToAgree.get(assertion)+1;
			assertionToAgree.put(assertion, value);
		}else{
			assertionToAgree.put(assertion, 1.0);
		}
		return assertionToAgree;
	}

	/**
	 * normalize(z)= z-min(z)/max(z)-min(z) in our case z-(-1)/1-(-1) aka z/2 + 1/2
	 * @param supportOpposeScore
	 * @return
	 */
private double normalize(double score) {
		double normalizedScore=score/2;
		normalizedScore=normalizedScore+0.5;
		return normalizedScore;
	}

private boolean disagree(int id, String assertion, OpinionSummarizationData data) throws Exception {
	if(data.getValue(id,assertion)==-1){
		return true;
	}else{
		return false;
	}
	}

/**
 * best/sum -worst/sum
 * @param best
 * @param worst
 * @return
 */
	private Double percentageDiff(Double best, Double worst) {
//		System.out.println(best+ " "+worst);
		
		//handle != null but real zero
		if(best!=null && worst!=null && best==0.0 && worst==0.0){
			return 0.0;
		}
		
		double sum= 0;
		if(worst == null){
			worst=0.0;
		}
		if(best == null ){
			best=0.0;
		}
		double diff=best-worst;
		sum=best+worst;
		if(sum==0.0){ 
//			System.out.println("NaN");
			return Double.NaN;
		}else{
//		System.out.println(diff/sum);
		 return diff/sum;
		}
//	    System.out.println(best+ " "+worst+ " "+(best/sum-worst/sum));
//		return best/sum-worst/sum;
		
	}

	private boolean agree(int id, String assertion, OpinionSummarizationData data) throws Exception {
		if(data.getValue(id,assertion)==1){
			return true;
		}else{
			return false;
		}
	}

	public void calculateStats4ClusteredParticipants(List<Participant> cluster0, OpinionSummarizationData collectedData,String clusterName) throws Exception {
		FrequencyDistribution<String> gender= new FrequencyDistribution<>();
		FrequencyDistribution<String> religion= new FrequencyDistribution<>();
		FrequencyDistribution<String> race= new FrequencyDistribution<>();
		FrequencyDistribution<String> affiliation= new FrequencyDistribution<>();
		FrequencyDistribution<String> education= new FrequencyDistribution<>();

		List<Integer> ids=new ArrayList<>();
		double avgAge=0.0;
		int ageCounter=0;
		for(Participant p : cluster0){
			ids.add(p.getId());
			if(p.getGender() != null){
				ageCounter++;
				avgAge+=(double)p.getAge();
				gender.inc(p.getGender());
				religion.inc(p.getReligion());
				race.inc(p.getRace());
				affiliation.inc(p.getAffiliation());
				education.inc(p.getEducation());
			}
		}
		System.out.println("female "+(double)gender.getCount("female")/(double)gender.getN());
		System.out.println("male "+(double)gender.getCount("male")/(double)gender.getN());
		System.out.println("Age "+avgAge/(double)ageCounter);
		
		System.out.println("democrat "+(double)affiliation.getCount("democrat")/(double)affiliation.getN());
		System.out.println("republican "+(double)affiliation.getCount("republican")/(double)affiliation.getN());
		System.out.println("other "+(double)affiliation.getCount("other")/(double)affiliation.getN());
		System.out.println("independent "+(double)affiliation.getCount("independent")/(double)affiliation.getN());

		
		System.out.println("bachelor "+(double)education.getCount("bachelor")/(double)education.getN());
		System.out.println("vocational_secondary_certification "+(double)education.getCount("vocational_secondary_certification")/(double)education.getN());
		System.out.println("master "+(double)education.getCount("master")/(double)education.getN());
		System.out.println("high_school "+(double)education.getCount("high_school")/(double)education.getN());
		System.out.println("some_college "+(double)education.getCount("some_college")/(double)education.getN());
		System.out.println("associate_degree "+(double)education.getCount("associate_degree")/(double)education.getN());

		
		File adFile=new File("src/main/resources/clustering/"+collectedData.getIssue()+"/"+clusterName+"_ad.tsv");
		double polarizationScore=0.0;
		int adCounter= 0;
		for(String assertion :collectedData.getStatements()){
			double agreementScore=getAgreementScore(assertion,cluster0,collectedData);
			
//agreementScore!=0.0 &&
			if(!Double.isNaN(agreementScore)){
				polarizationScore+=agreementScore;
				adCounter++;
//				System.out.println(assertion+" "+agreementScore);
//				FileUtils.writeStringToFile(adFile, collectedData.getAssertion2Id().get(assertion+"\t"+collectedData.getIssue())+"\t"+assertion+"\t"+agreementScore+"\t"+collectedData.getIssue()+"\n", "UTF-8", true);
			}
		}
//		calculateSupportOppose(collectedData,ids, new File("src/main/resources/clustering/"+collectedData.getIssue()+"/"+clusterName+"_bws.tsv"));
		System.out.println(polarizationScore/(double)adCounter);
		
	}

	private void calculateSupportOppose(OpinionSummarizationData data, List<Integer> idsInCluster,File clusterSOFile) throws Exception {
		//count scores per assertion 
		Map<String, Double> resultBest= new HashMap<>();
		Map<String, Double> result_worst= new HashMap<>();
		
		//filtered according to agree disagree
		Map<String, Double> result_leastSupport= new HashMap<>();
		Map<String, Double> result_mostOppose= new HashMap<>();
		Map<String, Double> result_mostSupport= new HashMap<>();
		Map<String, Double> result_leastOppose= new HashMap<>();
		Map<String, Double> assertionToAgree= new HashMap<>();
		Map<String, Double> assertionToDisAgree= new HashMap<>();
		
		//iterate over worst scores
		for(int id: data.getId2AssertionToWorst().keySet()){
			if(idsInCluster.contains(id)){
				for(String assertion: data.getId2AssertionToWorst().get(id).keySet()){
					if(result_worst.containsKey(assertion)){
						double newV= result_worst.get(assertion)+data.getId2AssertionToWorst().get(id).get(assertion);
						result_worst.put(assertion, newV);
					}else{
						result_worst.put(assertion, (double)data.getId2AssertionToWorst().get(id).get(assertion));
					}
					//if assertion is agreed worst becomes least support else most oppose
					if(agree(id,assertion,data)){
						assertionToAgree=increment(assertionToAgree,assertion);
						if(result_leastSupport.containsKey(assertion)){
							result_leastSupport.put(assertion, result_leastSupport.get(assertion)+data.getId2AssertionToWorst().get(id).get(assertion));
						}else{
							result_leastSupport.put(assertion,(double) data.getId2AssertionToWorst().get(id).get(assertion));
						}
					}else if(disagree(id,assertion,data)){
						assertionToDisAgree=increment(assertionToDisAgree,assertion);
						if(result_mostOppose.containsKey(assertion)){
							result_mostOppose.put(assertion, result_mostOppose.get(assertion)+data.getId2AssertionToWorst().get(id).get(assertion));
						}else{
							result_mostOppose.put(assertion, (double)data.getId2AssertionToWorst().get(id).get(assertion));
						}
					}
				}
			}
		}
		
		//iterate over best scores
		for(int id: data.getId2AssertionToBest().keySet()){
			if(idsInCluster.contains(id)){
				for(String assertion: data.getId2AssertionToBest().get(id).keySet()){
					if(resultBest.containsKey(assertion)){
						double newV= resultBest.get(assertion)+data.getId2AssertionToBest().get(id).get(assertion);
						resultBest.put(assertion, newV);
					}else {
						resultBest.put(assertion, (double)data.getId2AssertionToBest().get(id).get(assertion));
					}
					//if assertion is agreed best becomes most support else least oppose
					if(agree(id,assertion,data)){
						if(result_mostSupport.containsKey(assertion)){
							result_mostSupport.put(assertion, result_mostSupport.get(assertion)+data.getId2AssertionToBest().get(id).get(assertion));
						}else{
							result_mostSupport.put(assertion,(double) data.getId2AssertionToBest().get(id).get(assertion));
						}
					}else if(disagree(id,assertion,data)){
						if(result_leastOppose.containsKey(assertion)){
							result_leastOppose.put(assertion, result_leastOppose.get(assertion)+data.getId2AssertionToBest().get(id).get(assertion));
						}else{
							result_leastOppose.put(assertion, (double)data.getId2AssertionToBest().get(id).get(assertion));
						}
					}
				}
			}
			}
			
		Map<String,Double> statementToBWSScore= new HashMap<>();
		Map<String,Double> statementToMostLeastSupportScore= new HashMap<>();
		Map<String,Double> statementToMostLeastOpposeScore= new HashMap<>();
		
		//iterate another time to combine the scores
		for(String assertion: data.getStatements()){
//			System.out.println(assertion+" "+resultBest.get(assertion)+ " "+result_worst.get(assertion));
//			System.out.println(assertion+" "+result_mostSupport.get(assertion)+ " "+result_leastSupport.get(assertion));
//			System.out.println(assertion+" "+result_mostOppose.get(assertion)+ " "+result_leastOppose.get(assertion));

			double supportOpposeScore=percentageDiff(resultBest.get(assertion),result_worst.get(assertion));
			
			double mostLeastSupportScore=percentageDiff(result_mostSupport.get(assertion),result_leastSupport.get(assertion));
			double mostLeastOpposeScore=percentageDiff(result_mostOppose.get(assertion),result_leastOppose.get(assertion));
			
//			System.out.println("before "+mostLeastSupportScore);
			mostLeastSupportScore=normalize(mostLeastSupportScore);
//			System.out.println("after "+mostLeastSupportScore);
			mostLeastOpposeScore=normalize(mostLeastOpposeScore);
			
//			System.out.println(assertion+" "+resultBest.get(assertion)+ " "+result_worst.get(assertion)+" "+supportOpposeScore);
			statementToBWSScore.put(assertion, supportOpposeScore);
			statementToMostLeastSupportScore.put(assertion, mostLeastSupportScore);
			statementToMostLeastOpposeScore.put(assertion,mostLeastOpposeScore);
		}
		
		statementToBWSScore= sort(statementToBWSScore);
		
		//iterate to write them
		for (String key: statementToBWSScore.keySet()){
//			System.out.println(key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key));
//			FileUtils.writeStringToFile(supportFile, data.getAssertion2Id().get(key)+"\t"+key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key)+"\n", "UTF-8", true);
			FileUtils.writeStringToFile(clusterSOFile, data.getAssertion2Id().get(key+"\t"+data.getIssue())+"\t"+key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key)+"\t"+computeControversyScore(statementToMostLeastSupportScore.get(key),statementToMostLeastOpposeScore.get(key),assertionToAgree.get(key),assertionToDisAgree.get(key))+"\t"+data.getIssue()+"\n", "UTF-8");
//			FileUtils.writeStringToFile(soFile, data.getAssertion2Id().get(key+"\t"+data.getIssue())+"\t"+key+"\t"+statementToBWSScore.get(key)+"\t"+statementToMostLeastSupportScore.get(key)+"\t"+statementToMostLeastOpposeScore.get(key)+"\t"+data.getIssue()+"\n", "UTF-8", true);
		}
		
		
	}

	private double getAgreementScore(String assertion, List<Participant> cluster0, OpinionSummarizationData data) throws Exception {
		double[] vals=new double[cluster0.size()];
		int i=0;
		for(Participant p: cluster0){
			vals[i]=data.getValue(p.getId(), assertion);
			i++;
		}
		return score(vals);
	}



}
