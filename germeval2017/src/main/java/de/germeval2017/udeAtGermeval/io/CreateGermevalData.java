package de.germeval2017.udeAtGermeval.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class CreateGermevalData {

	public static void main(String[] args) throws JAXBException, IOException {
		File input=  new File("data/xml_data_split/dev.xml");
		SentimentDocumentSet docs= SentimentDataReader.read(input);
		
		File input_irr=  new File("data/irrelevant-documents/DEV.xml");
		SentimentDocumentSet docs_irr= SentimentDataReader.read(input_irr);
		
		setUpDocs(docs,true);
		setUpDocs(docs_irr,false);
		
		docs.getDocs().addAll(docs_irr.getDocs());
		docs.setDocs(shuffle(docs.getDocs()));
		
		
		writeXML(docs, new File(input.getName()+"_processed.xml"));
		writeTSV(docs, new File(input.getName()+"_processed.tsv"));
	}

	private static List<SentimentDocument> shuffle(List<SentimentDocument> docs) {
		Collections.shuffle(docs);
		return docs;
	}

	private static void setUpDocs(SentimentDocumentSet docs, boolean b) {
		System.out.println(docs.getDocs().size());
		int aspects=0;
		List<SentimentDocument> toRemove= new ArrayList<SentimentDocument>();
		int i=0;
		for(SentimentDocument doc: docs.getDocs()){
			doc=setDocSentiment(doc);
			doc.setRelevance(b);
			if(doc.getText()==null || doc.getText().equals("null")){
				toRemove.add(doc);
			}
//			System.out.println(doc.getId());
//			System.out.println(doc.getText());
			if(doc.getAspects()!= null && doc.getAspects().getAspects() != null){
				aspects+=doc.getAspects().getAspects().size();
//				for(SentimentAspect aspect: doc.getAspects().getAspects()){
//					System.out.println(aspect.getAspect()+ " : "+aspect.getSentiment());
//				}
			}
			i++;
		}
		//remove null text
		for(SentimentDocument doc: toRemove){
			docs.getDocs().remove(doc);
		}
		
	}

	private static void writeTSV(SentimentDocumentSet docs, File file) throws IOException {
		int i =0;
		//write header
		FileUtils.write(new File(file.getName()), "SOURCE"+"\t"+"TEXT"+"\t"+"RELEVANCE"+"\t"+"SENTIMENT"+"\t"+"CATEGORY:SENTIMENT"+"\t"+System.lineSeparator(), "UTF-8",true);

//		FileUtils.write(new File(file.getName()+"_trial"), "SOURCE"+"\t"+"TEXT"+"\t"+"RELEVANCE"+"\t"+"SENTIMENT"+"\t"+"CATEGORY:SENTIMENT"+"\t"+System.lineSeparator(), "UTF-8",true);
		for(SentimentDocument doc: docs.getDocs()){
//			if(i<100){
//				i++;
//				FileUtils.write(new File(file.getName()+"_trial"), getDocTSVString(doc)+System.lineSeparator(), "UTF-8",true);
//			}else{
				FileUtils.write(file, getDocTSVString(doc)+System.lineSeparator(), "UTF-8",true);
//			}
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
			doc.setRelevance(true );
		}else{
			doc.setRelevance(true);
		}
		return doc;
	}

	private static SentimentDocument setDocSentiment(SentimentDocument doc) {
		
		int positive=0;
		int neutral=0;
		int negative=0;
		
		if(doc.getAspects()!=null && doc.getAspects().getAspects() != null){
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
