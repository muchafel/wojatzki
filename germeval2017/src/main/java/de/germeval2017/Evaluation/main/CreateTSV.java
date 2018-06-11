package de.germeval2017.Evaluation.main;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import de.germeval2017.Evaluation.io.GermevalReader;
import de.germeval2017.Evaluation.objectBindings.SentimentAspect;
import de.germeval2017.Evaluation.objectBindings.SentimentDocument;
import de.germeval2017.Evaluation.objectBindings.SentimentDocumentSet;

public class CreateTSV {

	public static void main(String[] args) throws Exception {
		GermevalReader reader= new GermevalReader();
//		SentimentDocumentSet set =reader.read(new File("/Users/michael/Desktop/germeval_data_final_v1.3/test.xml"));
//		SentimentDocumentSet set =reader.read(new File("/Users/michael/Downloads/dev_v1.4.xml"));
		SentimentDocumentSet set =reader.read(new File("/Users/michael/Downloads/train_v1.4 (1).xml"));
//		SentimentDocumentSet set =reader.read(new File("/Users/michael/Desktop/germeval results/test_TIMESTAMP1.xml"));		
		write(set,true);
		
	}

	/**
	 * important: does not remove relevance rating
	 * @param writeXml 
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	private static void write(SentimentDocumentSet set, boolean writeXml) throws JAXBException, IOException {

		for (SentimentDocument doc : set.getDocs()) {
			String tsvRep = getTSVRepresentation(doc);
			FileUtils.write(new File("/Users/michael/Desktop/germeval results/train.tsv"),
					tsvRep + "\n", "UTF-8", true);
		}

	}

	private static String getTSVRepresentation(SentimentDocument doc) {
		StringBuilder sb= new StringBuilder();
		sb.append(doc.getId()+"\t");
		sb.append(doc.getText()+"\t");
		sb.append(doc.isRelevance()+"\t");
		sb.append(doc.getSentiment()+"\t");
		sb.append(getApectString(doc)+"\t");
		return sb.toString();
	}

	private static String getApectString(SentimentDocument doc) {
		String result="";
		if(doc.getAspects()!= null && doc.getAspects().getAspects() != null){
			for(SentimentAspect aspect: doc.getAspects().getAspects()){
				result+=getCategory(aspect.getAspect())+":"+aspect.getSentiment()+" ";
			}
		}
		
		return result;
	}

	private static String getCategory(String aspect) {
		if(aspect.contains("#")){
			return aspect.split("#")[0];
		}
		return aspect;
	}

}
