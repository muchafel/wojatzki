package de.germeval2017.Evaluation.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import de.germeval2017.Evaluation.io.GermevalReader;
import de.germeval2017.Evaluation.objectBindings.SentimentAspect;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

public class CreateSentiment {
	public static void main(String[] args) throws Exception {
		GermevalReader reader= new GermevalReader();
		SentimentDocumentSet set =reader.read(new File("/Users/michael/Downloads/test_diachron (1).xml"));
		
		for(SentimentDocument doc:set.getDocs()){
			String sentiment = calcSentiment(doc);
			doc.setSentiment(sentiment);
		}
		
		/**
		 * important: does not remove relevance rating
		 */
		write(set,false);
		
	}

	private static String calcSentiment(SentimentDocument doc) {
		int negative = 0;
		int positive= 0;
		int neutral= 0;
		if(doc.getAspects() != null && doc.getAspects().getAspects() != null){
			for(SentimentAspect aspect: doc.getAspects().getAspects()){
				if(aspect.getSentiment().equals("negative"))negative++;
				else if(aspect.getSentiment().equals("positive"))positive++;
				else neutral++;
			}
		}
		
		if(negative > 0 && positive == 0){
			return "negative";
		}else if (positive > 0 && negative == 0){
			return "positive";
		}else{
			return "neutral";
		}
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
	        m.marshal(set, new File("/Users/michael/Desktop/germeval_data_final_v1.3/test_diachron_gold.xml"));
		}else{
			for(SentimentDocument doc: set.getDocs()){
				String tsvRep= getTSVRepresentation(doc);
				FileUtils.write( new File("/Users/michael/Desktop/germeval_data_final_v1.3/test_diachron_gold.tsv"), tsvRep+"\n", "UTF-8", true);
			}
			
		}
	}
	
	private static String getTSVRepresentation(SentimentDocument doc) {
		StringBuilder sb= new StringBuilder();
		sb.append(doc.getId()+"\t");
		sb.append(doc.getText()+"\t");
		sb.append(doc.isRelevance()+"\t");
		sb.append(doc.getSentiment()+"\t");
		sb.append(getApectString(doc));
		return sb.toString();
	}

	private static String getApectString(SentimentDocument doc) {
		String result="";
		if(doc.getAspects()!= null && doc.getAspects().getAspects() != null){
			for(SentimentAspect aspect: doc.getAspects().getAspects()){
				result+=aspect.getAspect()+":"+aspect.getSentiment()+" ";
			}
		}
		
		return result;
	}
	
	
}
