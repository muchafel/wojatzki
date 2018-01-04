package de.unidue.ltl.io;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import com.apporiented.algorithm.clustering.Cluster;

import de.unidue.ltl.core.data.opinionSummarization.CanabisParticipant;
import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;
import de.unidue.ltl.data.dummy.opinionSummarization.Clusterer;
import de.unidue.ltl.data.dummy.opinionSummarization.QuantitativeStatistics;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Cobweb;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.SparseInstance;
import weka.core.Tag;
import weka.core.converters.ArffSaver;
import weka.gui.hierarchyvisualizer.HierarchyVisualizer;
import weka.knowledgeflow.Data;

public class ReadCrowdflowerJSON_Judgements {
	


	
	

	public static void main(String[] args) throws Exception {
		
		ArrayList<Integer> toExlude = new ArrayList<Integer>(Arrays.asList(43959216,43942797,43990834,43699918)); //new
//		ArrayList<Integer> toExlude = new ArrayList<Integer>(Arrays.asList(43853132,30408589,43967735,31349958,43498598,43884123,43891892,43959216,43976334,43990834,43860631,43699918,20244740,43869373,43916681,43780975,29571331,41127744,43851130,43935069,39531214,12366026,43679467,43806326,21088425,43961825,43641877,43851278,43994669,43939704,43855522,12115558)); //(estimated)0.05
//		ArrayList<Integer> toExlude = new ArrayList<Integer>(Arrays.asList(43853132,30408589,43967735,31349958,43498598,43884123,43891892,43976334,21088425,43860631)); //>100 ad errors
//		ArrayList<Integer> toExlude = new ArrayList<Integer>(Arrays.asList(43853132,30408589,43967735,31349958,43498598,43884123,43891892,43959216,43976334,43990834,43860631,43699918,20244740,43869373,43916681,43780975,29571331,41127744,43851130,43935069,39531214,12366026,43679467,43806326,21088425,43961825,43641877,43851278,43994669,43939704,43855522,12115558,43948769,42468998,43987213,43684066,41545007,37177227,43869435,16854635,13095205,19302668,43847840,43946798,34689613,43936834,43393412,43904063,39501628,43941355,43942386,43936844,38202325,43837109,31613324,34557302,43736722,43875818,42013631,38671298,15004831,43945424,34070076,43102561,39021669,43642386,40212251,15577915,43842980,43974080,43913997,40107272,35330747,43620110,43989034,31888345,41193378,22345789,33266010,11521789,43836952,20328071,13581319,43943676,19838540,1933319,43944033,43861467,42768322,38153196,20623511,6377879,15193250,43651516,30501721,20512953,43811627,20430407,43947238,43938234,43912578,38198526,43811734)); //>0.01

		
		File jsonFile_judgemens = new File("/Users/michael/Downloads/job_1199759 11.json");
		File jsonFile_demographics = new File("/Users/michael/Dropbox/nrc/data from crowdflower/job_1199788.json");

		CrowdflowerJsonReader_Judgements reader = new CrowdflowerJsonReader_Judgements(jsonFile_judgemens,jsonFile_demographics);
		Set<Integer> participantIds= new HashSet<>();
		Map<Integer,Integer> particpantId2Error = new HashMap<>();
		Map<String,Integer> assertion2Id = new HashMap<>();//gloablIds for assertions
//		Map<Participant,Integer> participant2Id = new HashMap<>();//gloablIds for participants
		
		for(String issue: reader.getIssue2Assertions().keySet()){
//			if(issue.equals("Gender Equality")){
				DataBuilder builder= new DataBuilder(issue);
				File issueFile=new File("src/main/resources/matricesForTSNE/"+issue+".tsv");
					//add header
					FileUtils.writeStringToFile(issueFile, getHeadline(reader.getIssue2Assertions().get(issue))+"\n", "UTF-8",true);

					//add one row per worker (two iterations because of ordering)
					for(int id: reader.getId2judgments().keySet()){
						
						if(toExlude.contains(id)) continue;
						
//						String responseString=id+" ";
						String responseString=id+"\t";

						int faultyAdCount=0;
						int faultyBWCount=0;
						//sum up faulty BWS
						for (Judgement j: reader.getId2judgments().get(id)){
							faultyBWCount+=j.getFaultyBWSTuple();
						}
						
						// agree disagree values
						List<Integer> adJudgments= new ArrayList<Integer>();
						int adPresent=0; //to filter 
						for(String assertion: reader.getIssue2Assertions().get(issue)){
							String adValue=agreeDisagree(assertion,reader.getId2judgments().get(id));
							if(adValue.equals("99")){
								faultyAdCount++;
								//set ad value to zero if there is a missmatch
								adValue="0";
								inc(id,particpantId2Error);
							}
							if(adValue.equals("99")||adValue.equals("1")||adValue.equals("-1")){
								adPresent++;
							}
							responseString+=adValue+"\t";
//							responseString+=adValue+" ";
							adJudgments.add(Integer.parseInt(adValue));
						}
						// bws values
						Map<String,Integer> best= new HashMap<String,Integer>();
						Map<String,Integer> worst= new HashMap<String,Integer>();
						for(String assertion: reader.getIssue2Assertions().get(issue)){
							String bwsValue=bwsValue(assertion,reader.getId2judgments().get(id));
//							responseString+=bwsValue+"\t";
							best.put(assertion,Integer.parseInt(bwsValue.split("\t")[0]));
							worst.put(assertion,Integer.parseInt(bwsValue.split("\t")[1]));
						}
						
						// add faulty counts
						responseString+=String.valueOf(faultyAdCount)+"\t";
						responseString+=String.valueOf(faultyBWCount)+"\t";
						if(reader.getId2demographics().containsKey(id)){
							responseString+=reader.getId2demographics().get(id).prettyPrint();
						}
						if(adPresent>=4){
							responseString+="\n";
							FileUtils.writeStringToFile(issueFile, responseString, "UTF-8",true);
							builder.getAdData().add(adJudgments);
//							builder.getBwsData().add(bwsJudgments);
							builder.addDemograhic(reader.getId2demographics().get(id),id);
							builder.addBWS(best,worst,id);
							builder.setAssertions(new ArrayList<String>(reader.getIssue2Assertions().get(issue)),assertion2Id);
						}
					}
					FileUtils.writeStringToFile(issueFile, getHeadlineID(issue,reader.getIssue2Assertions().get(issue),assertion2Id)+"\n", "UTF-8",true);

					OpinionSummarizationData data= builder.createData();
					for(Participant p:data.getParticipants()){
						participantIds.add(p.getId());
					}
					System.out.println("****");
					System.out.println(issue);
					System.out.println("****");
					QuantitativeStatistics stats= new QuantitativeStatistics();
					stats.calculateAgreement(data);
					stats.calculateSupportOppose(data);
					System.out.println("****");
					System.out.println();
//					wekaClusteringParticipants(data);
//					System.out.println("*** "+data.getIssue()+" ***");
//					writeClusters(wekaTopDownClustering(data.getParticipants(),data,0,"0",stats,new HashMap<Integer,List<String>>()),data.getIssue());
//					
//					clustering(data,false);
			}
//		}
		makeParticipantsFile(participantIds,reader.getId2demographics());
		makeQualityFile(reader.getParticipantId2Count(),particpantId2Error);
		
	}

	private static void makeQualityFile(Map<Integer, Integer> participantId2Count,
			Map<Integer, Integer> particpantId2Error) {
		for(int id: participantId2Count.keySet()) {
			if(particpantId2Error.containsKey(id)) {
				System.out.println(id+":\t"+participantId2Count.get(id)+"\t"+particpantId2Error.get(id));
			}else {
				System.out.println(id+":\t"+participantId2Count.get(id)+"\t"+"0");
			}
			
			
		}
		
	}

	private static void inc(int id, Map<Integer, Integer> particpantId2Error) {
		if(particpantId2Error.keySet().contains(id)) {
			int oldValue= particpantId2Error.get(id);
			oldValue+=1;
			particpantId2Error.put(id, oldValue);
		}else {
			particpantId2Error.put(id, 1);
		}
		
	}

	private static void writeClusters(HashMap<Integer, List<String>> wekaTopDownClustering, String issue) throws IOException {
		System.out.println(issue+" : "+wekaTopDownClustering);
		File adFile=new File("src/main/resources/clustering/"+issue+".tsv");
		FileUtils.write(adFile, "id"+"\t"+"partition 1"+"\tpartition 2"+"\tpartition 3"+"\tpartition 4"+"\n",true);
		for(int id : wekaTopDownClustering.keySet()){
			System.out.println(id+ " "+ wekaTopDownClustering.get(id));
//			FileUtils.write(adFile, id+"\t"+toZeroAndOne(wekaTopDownClustering.get(id))+"\n",true);
			FileUtils.write(adFile, id+"\t"+StringUtils.join(wekaTopDownClustering.get(id),"\t")+"\n",true);
		}
	}

	private static String toZeroAndOne(List<String> list) {
		StringBuilder sb= new StringBuilder();
		for(String t:list){
			if(t.endsWith("0")){
				sb.append("0\t");
			}else if(t.endsWith("1")){
				sb.append("1\t");
			}
		}
		return sb.toString();
	}

	private static HashMap<Integer, List<String>> wekaTopDownClustering(List<Participant> participants, OpinionSummarizationData collectedData,int level, String parentName, QuantitativeStatistics stats, HashMap<Integer, List<String>> result) throws Exception {
		if(level<4 && participants.size()>2){
			FastVector atts = new FastVector();
		    for(String assertion : collectedData.getStatements()){
			    atts.addElement(new Attribute(assertion));
		    }
		    Instances arffData = new Instances(collectedData.getIssue(), atts, 0);
		    for(Participant p: participants){
		    	double[] vals= collectedData.getRatingsOfParticipant(p);
		    	SparseInstance instance= new SparseInstance(1.0, vals);
		    	instance.setDataset(arffData);
		    	arffData.add(instance);
		    }
		    
		    
		    SimpleKMeans clusterer = new SimpleKMeans();
		    
		    ArffSaver saver = new ArffSaver();
		    saver.setInstances(arffData);
		    saver.setFile(new File("src/main/resources/"+collectedData.getIssue()+".arff"));
		    saver.writeBatch();
		    
//			clusterer.setNumClusters(10);
			clusterer.setDebug(true);
			clusterer.setPreserveInstancesOrder(true);
			
			
			clusterer.buildClusterer(arffData);
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(clusterer);
			eval.evaluateClusterer(arffData);
			
//			System.out.println(eval.clusterResultsToString());
//			Map<Integer,Integer> id2Cluster= new HashMap<>();
			String newName0=parentName+".0";
			String newName1=parentName+".1";
			List<Participant> cluster0= new ArrayList<>();
			List<Participant> cluster1= new ArrayList<>();
			for(int i=0;i<arffData.numInstances();i++) {
				double[] vals= collectedData.getRatingsOfParticipant(participants.get(i));
				int predictedCluster=clusterer.clusterInstance(new SparseInstance(1.0, vals));
//				System.out.println(participants.get(i).print()+" "+predictedCluster); 
//				id2Cluster.put(collectedData.getParticipants().get(i).getId(), predictedCluster);
				if(result.get(participants.get(i).getId()) == null){
					result.put(participants.get(i).getId(), new ArrayList<String>());
				}
				if(predictedCluster==0){
					cluster0.add(participants.get(i));
					result.get(participants.get(i).getId()).add(newName0);
				}else{
					cluster1.add(participants.get(i));
					result.get(participants.get(i).getId()).add(newName1);
				}
			}
			
//			System.out.println(newName0+":"+cluster0.size()+" <-> "+newName1+":"+cluster1.size());
			level++;
//			System.out.println("overall");
//			stats.calculateStats4ClusteredParticipants(collectedData.getParticipants(), collectedData, "");
//
//			System.out.println("stats for "+newName0+":"+cluster0.size() );
			stats.calculateStats4ClusteredParticipants(cluster0,collectedData,newName0);
//			
//			System.out.println("stats for "+newName1+":"+cluster1.size() );
//			stats.calculateStats4ClusteredParticipants(cluster1,collectedData,newName1);
			
			result.putAll(wekaTopDownClustering(cluster0, collectedData, level,newName0,stats,result));
			result.putAll(wekaTopDownClustering(cluster1, collectedData, level,newName1,stats,result));
		}
		return result;
	}

	private static String getHeadlineID(String issue, Set<String> assertions, Map<String, Integer> assertion2Id) {
		String headline="ID"+"\t";
		//header for agree/disagree
		for(String assertion: assertions){
			headline+=assertion2Id.get(assertion+"\t"+issue)+"\t";
		}
		//header for judgments
		for(String assertion: assertions){
			headline+="best "+assertion2Id.get(assertion+"\t"+issue)+"\tworst "+assertion2Id.get(assertion+"\t"+issue)+"\t";
		}
//		headline+="faultyAD"+"\t";
//		headline+="faultyBW"+"\t";
		headline+=demographicHeader();
		return headline;
	}


	private static void clustering(OpinionSummarizationData data,boolean clusterStatements) throws Exception {
		File clusteringFile=null;
		if(clusterStatements){
			clusteringFile = new File("src/main/resources/clustering/"+data.getIssue()+"_assertions.tsv");
		}else{
			clusteringFile = new File("src/main/resources/clustering/"+data.getIssue()+"_participants.tsv");
		}
		Clusterer clusterer= new Clusterer();
		Cluster cluster= clusterer.cluster(data,clusterStatements);
		Map<String,Cluster> hierarchicalClusters1=clusterer.getClusterMapping(cluster, new LinkedHashMap<String,Cluster>() ,"1",0);
		
		if(clusterStatements){
			//TODO
		}else{
			Map<String,List<String>> clusterToParticipantId= new TreeMap<String, List<String>>();
			for(String clusterName:hierarchicalClusters1.keySet()){
				List<String>participantIdsInCluster= getParticipantsInCluster(hierarchicalClusters1.get(clusterName), data);
//				System.out.println(clusterName+" "+ participantIdsInCluster);
				clusterToParticipantId.put(clusterName, participantIdsInCluster);
			}
//			for(String clusterId: clusterToParticipantId.keySet()){
//				System.out.println(clusterId+ " - "+clusterToParticipantId.get(clusterId).size());
//			}
			List<String> orderedKeys=sortByLength(clusterToParticipantId);
			String header= "ID"+"\t";
			for(String clusterId: orderedKeys){
				header+=clusterId+"\t";
			}
			FileUtils.writeStringToFile(clusteringFile, header+"\n", "UTF-8",true);

			for(String participant: data.getParticipantStrings()){
				String toPrint = "";
				
				//iterate by length
				for(String clusterId: orderedKeys){
					if(clusterToParticipantId.get(clusterId).contains(participant)){
//						System.out.print(clusterId+" ");
						toPrint+=clusterId+"\t";
					}else{
//						System.out.print("---");
						toPrint+="\t";
					}
				}
				System.out.println();
				FileUtils.writeStringToFile(clusteringFile, participant+"\t"+toPrint+"\n", "UTF-8",true);

			}
		}
	}

	
	private static List<String> sortByLength(Map<String, List<String>> clusterToParticipantId) {
		List<String> s = new ArrayList<>(clusterToParticipantId.keySet());
		Collections.sort(s, new Comparator<String>(){
		            @Override
		            public int compare(String s1, String s2){
		                 return Integer.compare(s1.length(), s2.length());
		            }
		});
		
		return s;
	}

	private static List<String> getParticipantsInCluster(Cluster c, OpinionSummarizationData data) {
		List<Cluster> leafs= getAllLeafs(c);
//		System.out.println(c.getName()+ " "+leafs.size());
		return mapLeafsToParticipants(leafs,data);
	}

	private static List<Cluster> getAllLeafs(Cluster c) {
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

	private static List<String> mapLeafsToParticipants(List<Cluster> leafs, OpinionSummarizationData data) {
		List<String> result= new ArrayList<>();
		
		for(Cluster leaf: leafs){
				result.add(leaf.getName());
		}
		return result;
	}
	

	private static void makeParticipantsFile(Set<Integer> participantIds, Map<Integer, Demographics> id2demographics) throws IOException {
		File participantFile= new File("src/main/resources/participants.tsv");
		FileUtils.writeStringToFile(participantFile, demographicHeader()+"\n", "UTF-8",true);
		for(int id: participantIds){
			Demographics demographic=id2demographics.get(id);
			if(demographic != null){
				FileUtils.writeStringToFile(participantFile, String.valueOf(id)+"\t"+demographic.prettyPrint()+"\n", "UTF-8",true);
			}else{
				FileUtils.writeStringToFile(participantFile, String.valueOf(id)+"\n", "UTF-8",true);
			}
		}
		
	}

	private static void wekaClusteringParticipants(OpinionSummarizationData collectedData) throws Exception {
	    FastVector atts = new FastVector();
	    for(String assertion : collectedData.getStatements()){
		    atts.addElement(new Attribute(assertion));
	    }
//	    atts.addElement(new Attribute("IDs", (FastVector) null));
//	    atts.add(new Attribute("IDs"));
	    Instances arffData = new Instances(collectedData.getIssue(), atts, 0);
	    for(Participant p: collectedData.getParticipants()){
	    	double[] vals= collectedData.getRatingsOfParticipant(p);
	    	SparseInstance instance= new SparseInstance(1.0, vals);
	    	instance.setDataset(arffData);
//	    	instance.setValue(vals.length, String.valueOf(p.getId()));
	    	arffData.add(instance);
	    }
	
	    
	    /**
	     * clusterer linktype mapping
	     */
	    int SINGLE = 0;
	    int COMPLETE = 1;
	    int AVERAGE = 2;
	    int MEAN = 3;
	    int CENTROID = 4;
	    int WARD = 5;
	    int ADJCOMPLETE = 6;
	    int NEIGHBOR_JOINING = 7;
	    Tag[] TAGS_LINK_TYPE = { new Tag(SINGLE, "SINGLE"),
	    	    new Tag(COMPLETE, "COMPLETE"), new Tag(AVERAGE, "AVERAGE"),
	    	    new Tag(MEAN, "MEAN"), new Tag(CENTROID, "CENTROID"),
	    	    new Tag(WARD, "WARD"), new Tag(ADJCOMPLETE, "ADJCOMPLETE"),
	    	    new Tag(NEIGHBOR_JOINING, "NEIGHBOR_JOINING") };
	    
	    SimpleKMeans clusterer = new SimpleKMeans();
	    
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(arffData);
	    saver.setFile(new File("src/main/resources/"+collectedData.getIssue()+".arff"));
	    saver.writeBatch();
	    
		/**
		 * set up clusterer
		 */
//		clusterer.setNumClusters(4);
		clusterer.setDebug(true);
//		clusterer.setLinkType(new SelectedTag(AVERAGE, TAGS_LINK_TYPE));
		
		
		//run clusterer
		clusterer.buildClusterer(arffData);
//		System.out.println(clusterer.graph());
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer);
		eval.evaluateClusterer(arffData);
		
		System.out.println(eval.clusterResultsToString());
		
		//visualize in JFRAME
//		JFrame mainFrame = new JFrame("Weka Test");
//		mainFrame.setSize(600, 400);
//		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		Container content = mainFrame.getContentPane();
//		content.setLayout(new GridLayout(1, 1));
//		
//		HierarchyVisualizer visualizer = new HierarchyVisualizer(clusterer.graph());
//		content.add(visualizer);
//		
//		mainFrame.setVisible(true);
		
	}


	private static String bwsValue(String assertion, List<Judgement> list) {
		int best = 0;
		int worst = 0;
		for(Judgement j: list){
			if(j.getBest().equals(assertion)){
				best++;
			}
			if(j.getWorst().equals(assertion)){
				worst++;
			}
		}
		return String.valueOf(best)+"\t"+String.valueOf(worst);
	}

	private static String getHeadline(Set<String> assertions) {
		String headline="participant id"+"\t";
		//header for agree/disagree
		for(String assertion: assertions){
//			headline+=assertion+"\t";
			headline+=assertion+"\t";
		}
		//header for support opposejudgments
//		for(String assertion: assertions){
//			headline+="best "+assertion+"\tworst "+assertion+"\t";
//		}
		headline+="faultyAD"+"\t";
		headline+="faultyBW"+"\t";
		headline+=demographicHeader();
		return headline;
	}

	private static String demographicHeader() {
		StringBuilder sb= new StringBuilder();
		sb.append("participant id\t");
		sb.append("age\t");
		sb.append("gender\t");
		sb.append("affiliation\t");
		sb.append("education\t");
		sb.append("family status\t");
		sb.append("profession\t");
		sb.append("race\t");
		sb.append("religion\t");
		sb.append("ties2overseas\t");
		sb.append("US citizen");
		return sb.toString();
	}

	private static String agreeDisagree(String assertion, List<Judgement> list) {
		boolean agree = false;
		boolean disagree = false;
		for(Judgement j: list){
			if(j.getAgree().contains(assertion)){
				agree=true;
			}
			if(j.getDisagree().contains(assertion)){
				disagree=true;
			}
		}
		if(!agree && !disagree){
			return "0";
		}else if(agree && !disagree){
			return "1";
		}else if(!agree && disagree){
			return "-1";
		}else{
			return "99";
		}
	}

}
