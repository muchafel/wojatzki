package de.unidue.ltl.data.dummy.opinionSummarization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.unidue.ltl.core.data.opinionSummarization.CanabisParticipant;
import de.unidue.ltl.core.data.opinionSummarization.OpinionSummarizationData;
import de.unidue.ltl.core.data.opinionSummarization.Participant;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.AddID;


public class CanabisDataWriter {

	public void write(File file, OpinionSummarizationData data) throws Exception {
		StringBuilder headerBuilder= new StringBuilder("id\tgender\tage\tprofession\tsportiv\tsmoker\tinterestd in having a smoke shop\tconservative or liberal");
		int i=0;
		for(String s: data.getStatements()){
			System.out.println(s+"\t"+i++);
			headerBuilder.append("\t"+s);
		}
		System.out.println(headerBuilder.toString().split("\t").length);
		headerBuilder.append("\n");
		FileUtils.write(file, headerBuilder.toString(), true);
		for(Participant p: data.getParticipants()){
			String row=((CanabisParticipant)p).print()+"\t"+data.getStringValuesForParticipant(p)+"\n";
			FileUtils.write(file, row, true);
		}
		
	}

	public void writeArff(File file, OpinionSummarizationData data) throws Exception {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		
		for(String statement: data.getStatements()){
		     atts.add(new Attribute(statement));
		}
		
		
		List<Instance> instanceList= getInstanceParticipants(data);
		Instances instances = new Instances("Dataset", atts, instanceList.size());
		
		// add the instances to the data set
		for(Instance inst : instanceList){
			instances.add(inst);
		}
		
		AddID addId= new AddID();
		addId.setInputFormat(instances); 
		Instances data_with_id = Filter.useFilter(instances, addId);

		
		HierarchicalClusterer clusterer = new HierarchicalClusterer(); 
		System.out.println(clusterer.getDistanceFunction().listOptions());
		clusterer.buildClusterer(instances);  
		System.out.println(clusterer.getNumClusters());
		System.out.println(clusterer.graph());
		System.out.println(clusterer.Newick);
		System.out.println(clusterer.getPrintNewick());

		HierarchicalClusterer clusterer2 = new HierarchicalClusterer(); 
		clusterer2.buildClusterer(data_with_id);  
		System.out.println(clusterer2.graph());
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		saver.setFile(file);
		saver.setDestination(file);   
		saver.writeBatch();
	}

	private List<Instance> getInstanceParticipants(OpinionSummarizationData data) throws Exception {
		List<Instance> instanceList = new ArrayList<Instance>();
		//create instances and 
		for(Participant p : data.getParticipants()){
			DenseInstance instance= new DenseInstance(data.getStatements().size());
			int i=0;
			for(double j: data.getRatingsOfParticipant(p)){
				instance.setValue(i, j);
				i++;
			}
            instanceList.add(instance);
        } 
		return instanceList;
	}

}
