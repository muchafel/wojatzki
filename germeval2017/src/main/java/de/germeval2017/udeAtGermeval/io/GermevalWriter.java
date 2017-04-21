package de.germeval2017.udeAtGermeval.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.germeval2017.udeAtGermeval.objectBindings.SentimentAspect;
import de.germeval2017.udeAtGermeval.objectBindings.SentimentDocument;
import de.germeval2017.udeAtGermeval.objectBindings.SentimentDocumentSet;

public class GermevalWriter {

	public void writeXML(SentimentDocumentSet docs, File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(SentimentDocumentSet.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(docs, file);
	}

	public void writeTSV(SentimentDocumentSet docs, File file) throws IOException {
		// write header
		FileUtils.write(new File(file.getName()), "SOURCE" + "\t" + "TEXT" + "\t" + "RELEVANCE" + "\t" + "SENTIMENT"
				+ "\t" + "CATEGORY:SENTIMENT" + "\t" + System.lineSeparator(), "UTF-8", true);

		for (SentimentDocument doc : docs.getDocs()) {
			FileUtils.write(file, getDocTSVString(doc) + System.lineSeparator(), "UTF-8", true);
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
	
}
