package de.germeval2017.Evaluation.main;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.germeval2017.Evaluation.io.GermevalReader;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class RedundancyInspection {

	public static void main(String[] args) throws Exception {
		GermevalReader reader= new GermevalReader();
		
//		System.out.println();
//		System.out.println("*** TRAIN ***");
//		
//		SentimentDocumentSet setTrain =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.2/train.xml"));
//		setTrain=checkReducndancy(setTrain);
		
		System.out.println();
		System.out.println("*** TEST ***");

		SentimentDocumentSet setTestxml =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.3/test.xml"));
		setTestxml=checkReducndancy(setTestxml);
		
		SentimentDocumentSet setTesttsv =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.3/test.tsv"));
		setTesttsv=checkReducndancy(setTesttsv);
		
		
		SentimentDocumentSet setTestxml2 =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.3/test_clear.xml"));
		setTestxml2=checkReducndancy(setTestxml2);
		
		SentimentDocumentSet setTesttsv2 =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.3/test_clear.tsv"));
		setTesttsv2=checkReducndancy(setTesttsv2);
		
//		System.out.println();
//		System.out.println("*** DEV ***");
//		SentimentDocumentSet setDev =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.2/dev.xml"));
//		setDev=checkReducndancy(setDev);
//		
//		System.out.println();
//		System.out.println("*** Train OLD ***");
//		SentimentDocumentSet setTrainOld =reader.read(new File("/Users/michael/Downloads/train (5).xml"));
//		setTrainOld=checkReducndancy(setTrainOld);
		
	}

	private static SentimentDocumentSet checkReducndancy(SentimentDocumentSet set) {
		FrequencyDistribution<String> fdId= new FrequencyDistribution<>();
		for(SentimentDocument doc:set.getDocs()){
			fdId.inc(doc.getId());
		}
		
		for(String inced: fdId.getMostFrequentSamples(40)){
			if(fdId.getCount(inced)>1){
				System.out.println(inced+ "\tcount: "+fdId.getCount(inced));
			}
		}
		
		return set;
	}


	
}
