package assertionRegression.io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;

public class AssertionSimilarityPairReader_deep extends JCasCollectionReader_ImplBase {

	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true)
	private File inputFile;
	
	private List<String> lines;
	private String[] columnHeaders;
	private int rowCounter;
	private int columnCounter;
	private int numberOfLines;
	private Set<String>map= new HashSet();
	
	
	@Override
	public Progress[] getProgress() {
		  return new Progress[] { new ProgressImpl(rowCounter*columnCounter, numberOfLines*numberOfLines,Progress.ENTITIES) }; 
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		if (lines == null) {
			lines= FileUtils.readLines(inputFile, "utf-8");
			columnHeaders=lines.get(0).split("\t");
			numberOfLines= lines.size();
			columnCounter = 0;
			rowCounter = 1;
		}

		columnCounter++;
		
		if(columnCounter>=numberOfLines) {
//			columnCounter=1;
//			columnCounter=rowCounter+1;
			columnCounter=rowCounter;
			rowCounter++;
		}
		
		//exclude diagonal
		if(columnHeaders[columnCounter].equals(lines.get(rowCounter).split("\t")[0])) {
			hasNext();
		}
		
//		if(rowCounter==2) {
//			return false;
//		}
		
		
		if (rowCounter>=numberOfLines-1) {
//			System.out.println("number of instances "+map.size());
			return false;
		}


		return true;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		
		try {
			createMetaData(jcas,getCollectionId1() + "_" + getCollectionId2(),getDocumentId1() + "_" + getDocumentId2(),getTitle1() + " " + getTitle2());
			jcas.setDocumentText(getText1()+" $ "+getText2());
			map.add(getDocumentId1() + "_" + getDocumentId2());
//			System.out.println(getDocumentId1() + "_" + getDocumentId2());
		} catch (TextClassificationException e) {
			throw new CollectionException(e);
		}
		
		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
        String outcomeString = getTextClassificationOutcome(jcas);
        outcome.setOutcome(outcomeString);
        outcome.addToIndexes();
		
	}
	
	public String getCollectionId1() throws TextClassificationException {
		return String.valueOf(rowCounter);
	}

	public String getCollectionId2() throws TextClassificationException {
		return String.valueOf(columnCounter);
	}

	public String getDocumentId1() throws TextClassificationException {
		return String.valueOf(rowCounter);
	}

	public String getDocumentId2() throws TextClassificationException {
		return String.valueOf(columnCounter);
	}

	public String getTitle1() throws TextClassificationException {
		return String.valueOf(rowCounter);
	}

	public String getTitle2() throws TextClassificationException {
		return String.valueOf(columnCounter);
	}

	public String getLanguage1() throws TextClassificationException {
		return "en";
	}

	public String getLanguage2() throws TextClassificationException {
		return "en";
	}
	
	

	public String getText1() throws TextClassificationException {
		String text= lines.get(rowCounter).split("\t")[0];
		text= text.replace(".", " ");
//		System.out.println("row "+text);
		return text;
	}

	public String getText2() throws TextClassificationException {
		String text= columnHeaders[columnCounter];
		text= text.replace(".", " ");
//		System.out.println("col "+text);
		return text;
	}
	

	public String getTextClassificationOutcome(JCas arg0) throws CollectionException {
//		System.out.println(lines.get(rowCounter).split("\t")[columnCounter]);
		if(columnHeaders[columnCounter].equals(lines.get(rowCounter).split("\t")[0])) {
			return "1.0";
		}
		return lines.get(rowCounter).split("\t")[columnCounter];
	}
	
	 protected void createMetaData(JCas jcas, String collectionId, String docId,
	            String docTitle)
	    {
	        DocumentMetaData metaData = DocumentMetaData.create(jcas);
//	        metaData.setCollectionId(collectionId);
//	        metaData.setDocumentBaseUri("");
//	        metaData.setDocumentUri("/" + docId);
//	        metaData.setDocumentTitle(docTitle);
	        metaData.setDocumentId(docId);
	        metaData.addToIndexes();
	    }

}
