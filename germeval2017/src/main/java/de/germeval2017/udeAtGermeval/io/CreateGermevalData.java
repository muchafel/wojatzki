package de.germeval2017.udeAtGermeval.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class CreateGermevalData {

	public static void main(String[] args) throws JAXBException, IOException {
		File input=  new File("data/train.xml");
		SentimentDocumentSet docs= SentimentDataReader.read(input);
		System.out.println(docs.getDocs().size());
		int aspects=0;
		for(SentimentDocument doc: docs.getDocs()){
			doc=setDocSentiment(doc);
			doc=setRelevance(doc);
//			System.out.println(doc.getId());
//			System.out.println(doc.getText());
			if(doc.getAspects().getAspects() != null){
				aspects+=doc.getAspects().getAspects().size();
//				for(SentimentAspect aspect: doc.getAspects().getAspects()){
//					System.out.println(aspect.getAspect()+ " : "+aspect.getSentiment());
//				}
			}
		}
		FrequencyDistribution<String> fd_Sentiment= new FrequencyDistribution<String>();
		FrequencyDistribution<String> fd_Relevance= new FrequencyDistribution<String>();

		for(SentimentDocument doc: docs.getDocs()){
			fd_Sentiment.inc(doc.getSentiment());
			fd_Relevance.inc(String.valueOf(doc.isRelevance()));
		}
		System.out.println(fd_Sentiment.getCount("neutral")+" "+fd_Sentiment.getCount("negative")+" "+fd_Sentiment.getCount("positive"));
		System.out.println(fd_Relevance.getCount("true")+" "+ fd_Relevance.getCount("false"));
		System.out.println(aspects);
		
		writeXML(docs, new File(input.getName()+"_processed.xml"));
		writeTSV(docs, new File(input.getName()+"_processed.tsv"));
	}

	private static void writeTSV(SentimentDocumentSet docs, File file) throws IOException {
		int i =0;
		for(SentimentDocument doc: docs.getDocs()){
			if(i<100){
				i++;
				FileUtils.write(new File(file.getName()+"_trial"), getDocTSVString(doc)+System.lineSeparator(), "UTF-8",true);
			}else{
				FileUtils.write(file, getDocTSVString(doc)+System.lineSeparator(), "UTF-8",true);
			}
		}
	}

	private static CharSequence getDocTSVString(SentimentDocument doc) {
		List<String> data= new ArrayList<String>();
		data.add(doc.getId());
		data.add(doc.getText());
		data.add(String.valueOf(doc.isRelevance()));
		data.add(doc.getSentiment());
		if(doc.getAspects() != null && doc.getAspects().getAspects()!=null){
			data.add(getAspectString(doc.getAspects().getAspects()));
		}
		
		return StringUtils.join(data, "\t");
	}

	private static String getAspectString(List<SentimentAspect> aspects) {
		List<String> data= new ArrayList<String>();
		for(SentimentAspect aspect: aspects){
			data.add(aspect.getAspect()+":"+aspect.getSentiment());
		}
		
		return StringUtils.join(data, " ");
	}

	private static void writeXML(SentimentDocumentSet docs, File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(SentimentDocumentSet.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(docs, file);
	}

	private static SentimentDocument setRelevance(SentimentDocument doc) {
		if(doc.getSentiment().equals("neutral")){
			doc.setRelevance(false);
		}else{
			doc.setRelevance(true);
		}
		return doc;
	}

	private static SentimentDocument setDocSentiment(SentimentDocument doc) {
		
		int positive=0;
		int neutral=0;
		int negative=0;
		
		if(doc.getAspects().getAspects() != null){
			for(SentimentAspect aspect: doc.getAspects().getAspects()){
				if(aspect.getSentiment().equals("positive")){
					positive++;
				}else if(aspect.getSentiment().equals("negative")){
					negative++;
				}else{
					neutral++;
				}
			}
		}
		if(negative==0 && positive>0){
			doc.setSentiment("positive");
		}
		else if(negative>0 && positive==0){
			doc.setSentiment("negative");
		}else{
			doc.setSentiment("neutral");
		}
		
		return doc;
	}

}
