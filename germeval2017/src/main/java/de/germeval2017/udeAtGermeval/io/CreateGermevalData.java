package de.germeval2017.udeAtGermeval.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class CreateGermevalData {

	public static void main(String[] args) throws JAXBException, FileNotFoundException {
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
