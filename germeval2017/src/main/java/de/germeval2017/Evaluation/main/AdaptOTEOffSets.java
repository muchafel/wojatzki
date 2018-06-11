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

public class AdaptOTEOffSets {

	public static void main(String[] args) throws Exception {
		GermevalReader reader= new GermevalReader();
//		SentimentDocumentSet set =reader.read(new File("/Users/michael/Downloads/train.xml"));
		SentimentDocumentSet set =reader.read(new File("/Users/michael/Desktop/Germevalv1.3/train_v1.3.xml"));
		SentimentDocumentSet set2 =reader.read(new File("/Users/michael/Desktop/Germevalv1.3/train_v1.3.xml"));
		set=updateOffSets(set,set2);
		/**
		 * important: does not remove relevance rating
		 */
		write(set);
		
	}

	/**
	 * important: does not remove relevance rating
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	private static void write(SentimentDocumentSet set) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(SentimentDocumentSet.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


        // Write to File
        m.marshal(set, new File("train_v1.2.xml"));
        for(String line: FileUtils.readLines(new File("train_v1.2.xml"))){
        	line=line.replace(" from2=\\\"0\\\"", "");
        	line=line.replace(" to2=\\\"0\\\"", "");
        	line=line.replaceAll(" from2=\\\"\\d+\\\"", "");
        	line=line.replaceAll(" to2=\\\"\\d+\\\"", "");
//        	
//        	if(line.contains("to2=\\\"\\d+\\\"")){
//        		line=line.replaceAll(" to=\\\"\\d+\\\"", "");
//            	line=line.replaceAll("to2", "to");
//        	}
//        	if(line.contains("from2=")){
//        		line=line.replaceAll("from=\\\"\\d+\\\"", "");
//        	}
//        	line=line.replaceAll("from2", "from");
        	FileUtils.write(new File("train_v1.3.xml"), line+System.lineSeparator(), "UTF-8",true);
        }
		
	}

	private static SentimentDocumentSet updateOffSets(SentimentDocumentSet set,SentimentDocumentSet set2) throws Exception {
		for(SentimentDocument doc:set.getDocs()){
			if(doc.getAspects() == null || doc.getAspects().getAspects()==null || doc.getAspects().getAspects().isEmpty()) continue;
			for(SentimentAspect aspect:doc.getAspects().getAspects()){
				if(aspect.getOte().contains(" * ")){
					int newOffset=getOffset2(set2, doc, aspect);
					aspect.setEnd(newOffset);
				}
			}
			
		}
		return set;
	}

	private static int getOffset2(SentimentDocumentSet set, SentimentDocument searchedDoc, SentimentAspect searchedAspect) {
		for (SentimentDocument doc : set.getDocs()) {
			if (doc.getAspects() == null || doc.getAspects().getAspects() == null
					|| doc.getAspects().getAspects().isEmpty())
				continue;
//			System.out.println(doc.getText()+ " "+searchedDoc.getText());
			if(doc.getId().equals(searchedDoc.getId())){
				for (SentimentAspect aspect : doc.getAspects().getAspects()) {
					if(aspect.getOte().equals(searchedAspect.getOte())){
						System.out.println("new offset "+aspect.getEnd2());
						return aspect.getEnd2();
					}
				}
			}
		}
		return searchedAspect.getEnd();
	}

	private static String getLastParticle(String ote) {
		String[] particel= ote.split(" \\* ");
		return particel[particel.length-1].replace("+", "");
	}

}
