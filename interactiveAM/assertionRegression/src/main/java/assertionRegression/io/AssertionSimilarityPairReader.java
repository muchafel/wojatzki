package assertionRegression.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.io.TCReaderSingleLabel;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.core.io.PairReader_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;

public class AssertionSimilarityPairReader extends PairReader_ImplBase implements TCReaderSingleLabel {

	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true)
	private File inputFile;
	
	private List<String> lines;
	private String[] columnHeaders;
	private int rowCounter;
	private int columnCounter;
	private int numberOfLines;

	
	@Override
	public String getCollectionId1() throws TextClassificationException {
		return String.valueOf(rowCounter);
	}

	@Override
	public String getCollectionId2() throws TextClassificationException {
		return String.valueOf(columnCounter);
	}

	@Override
	public String getDocumentId1() throws TextClassificationException {
		return String.valueOf(rowCounter);
	}

	@Override
	public String getDocumentId2() throws TextClassificationException {
		return String.valueOf(columnCounter);
	}

	@Override
	public String getTitle1() throws TextClassificationException {
		return String.valueOf(rowCounter);
	}

	@Override
	public String getTitle2() throws TextClassificationException {
		return String.valueOf(columnCounter);
	}

	@Override
	public String getLanguage1() throws TextClassificationException {
		return "en";
	}

	@Override
	public String getLanguage2() throws TextClassificationException {
		return "en";
	}

	@Override
	public String getText1() throws TextClassificationException {
		String text= lines.get(rowCounter).split("\t")[0];
		text= text.replace(".", " ");
//		System.out.println("row "+text);
		return text;
	}

	@Override
	public String getText2() throws TextClassificationException {
		String text= columnHeaders[columnCounter];
		text= text.replace(".", " ");
//		System.out.println("col "+text);
		return text;
	}

	 @Override
	    public void getNext(JCas jcas)
	        throws IOException, CollectionException{
	        super.getNext(jcas);
//	        System.out.println(columnCounter+" ("+numberOfLines+") "+rowCounter);
	        TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
	        String outcomeString = getTextClassificationOutcome(jcas);
	        outcome.setOutcome(outcomeString);
	        outcome.addToIndexes();

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
		
//		if(columnCounter>=numberOfLines) {
//			columnCounter=1;
//			rowCounter++;
//		}
		
//		if (rowCounter>=2) {
//			return false;
//		}
		
		if (rowCounter>=numberOfLines-1) {
			return false;
		}


		return true;
	}

	@Override
	public Progress[] getProgress() {
		  return new Progress[] { new ProgressImpl(rowCounter*columnCounter, numberOfLines*numberOfLines,Progress.ENTITIES) }; 
	}

	@Override
	public String getTextClassificationOutcome(JCas arg0) throws CollectionException {
//		System.out.println(lines.get(rowCounter).split("\t")[columnCounter]);
		if(columnHeaders[columnCounter].equals(lines.get(rowCounter).split("\t")[0])) {
			return "1.0";
		}
		return lines.get(rowCounter).split("\t")[columnCounter];
	}

}
