package de.germeval2017.Evaluation.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import de.germeval2017.Evaluation.io.GermevalReader;
import de.germeval2017.Evaluation.objectBindings.SentimentAspect;
import de.germeval2017.Evaluation.objectBindings.SentimentAspectSet;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

public class CreateTaskCBaseline {

	public static void main(String[] args) throws Exception {
		GermevalReader reader= new GermevalReader();
//		SentimentDocumentSet set =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.3/test.xml"));
		SentimentDocumentSet set =reader.read(new File("/Users/michael/Desktop/germeval results/test_TIMESTAMP2.xml"));
		
		set=makeBaselinePredictions(set);
		/**
		 * important: does not remove relevance rating
		 */
		write(set,true);
		
	}

	/**
	 * important: does not remove relevance rating
	 * @param writeXml 
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	private static void write(SentimentDocumentSet set, boolean writeXml) throws JAXBException, IOException {
		
		if(writeXml){
			JAXBContext context = JAXBContext.newInstance(SentimentDocumentSet.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	        // Write to File
	        m.marshal(set, new File("/Users/michael/Desktop/germeval results/timestamp2/organizers.C.2.mcb.xml"));
		}else{
			for(SentimentDocument doc: set.getDocs()){
				String tsvRep= getTSVRepresentation(doc);
				FileUtils.write( new File("/Users/michael/Desktop/germeval_data_final_v1.3/test_diachron_clear.tsv"), tsvRep+"\n", "UTF-8", true);
			}
			
		}
	}

	private static String getTSVRepresentation(SentimentDocument doc) {
		StringBuilder sb= new StringBuilder();
		sb.append(doc.getId()+"\t");
		sb.append(doc.getText()+"\t");
		sb.append("unknown\t");
		sb.append("unknown\t");
		return sb.toString();
	}

	private static SentimentDocumentSet makeBaselinePredictions(SentimentDocumentSet set) {
		for(SentimentDocument doc:set.getDocs()){
			List<SentimentAspect> aspects= new ArrayList<>();
			SentimentAspect aspect = new SentimentAspect(0, 0, "Allgemein#Haupt", "neutral");
			aspects.add(aspect);
			SentimentAspectSet sentimentAspectSet= new SentimentAspectSet();
			sentimentAspectSet.setAspects(aspects);
			doc.setAspects(sentimentAspectSet);
			doc.setSentiment("neutral");
			doc.setRelevance(true);
		}
		return set;
	}

}
