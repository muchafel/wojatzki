package assertionRegression.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import assertionRegression.annotationTypes.Issue;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;

public class AssertionIssueSpecificReaderTrainTest  extends JCasCollectionReader_ImplBase {
	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true)
	private File inputFile;

	/**
	 * Character encoding of the input data
	 */
	public static final String PARAM_ENCODING = ComponentParameters.PARAM_SOURCE_ENCODING;
	@ConfigurationParameter(name = PARAM_ENCODING, mandatory = true, defaultValue = "UTF-8")
	private String encoding;

	/**
	 * Language of the input data
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
	private String language;

	public static final String PARAM_ISSUE = "TrainIssue";
	@ConfigurationParameter(name = PARAM_ISSUE, mandatory = true)
	private String trainIssue;
	
	public static final String PARAM_IS_TRAIN = "ReaderIsTrain";
	@ConfigurationParameter(name = PARAM_IS_TRAIN, mandatory = true)
	private boolean isTrain;
	
	public static final String PARAM_TARGETCLASS = "TargetCLass";
	@ConfigurationParameter(name = PARAM_TARGETCLASS, mandatory = true)
	private String target;
	
	
	String text = null;
	String ad_score = null;
	String controversity = null;
	String passion_score = null;
	String support_score = null;
	String oppose_score = null;
	String support_oppose_score = null;
	String issue=null;
	String id=null;
//	int i=0;

	private BufferedReader br;

	
	@Override
	public void getNext(JCas aJCas) throws IOException, CollectionException {
		DocumentMetaData dmd = new DocumentMetaData(aJCas);
		dmd.setDocumentTitle("");
//		dmd.setDocumentId(String.valueOf(i++));
		dmd.setDocumentId(id);
		dmd.addToIndexes();

		aJCas.setDocumentText(text);
		aJCas.setDocumentLanguage(language);

		TextClassificationOutcome o = new TextClassificationOutcome(aJCas, 0, aJCas.getDocumentText().length());
		if(target.equals("Agreement")){
			o.setOutcome(ad_score);
		}else if(target.equals("Support_ALL")){
			o.setOutcome(support_oppose_score);
		}else if(target.equals("Support_AGREE")){
			o.setOutcome(support_score);
		}else if(target.equals("Oppose_DISAGREE")){
			o.setOutcome(oppose_score);
		}else if(target.equals("Passion")){
			o.setOutcome(passion_score);
		}else if(target.equals("Controversity")){
			o.setOutcome(controversity);
		}
		else{
			throw new IOException(target+ " not configured as target class.");
		}
		
		Issue assertionIssue= new Issue(aJCas, 0, aJCas.getDocumentText().length());
		assertionIssue.setIssue(issue);
		assertionIssue.addToIndexes();
		
//		System.out.println(text+ " "+ o);
		o.addToIndexes();
	}

	@Override
	public boolean hasNext() {
		try {
			if (br == null) {
//				System.out.println("______");
//				System.out.println("train "+ isTrain);
//				System.out.println("______");
//				System.out.println();
				br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			}

			String entry = br.readLine();

			if (entry == null || entry.isEmpty()) {
				return false;
			}

			
			
			String[] split = entry.split("\t");
			
			if(split[7].equals(trainIssue) && isTrain) {
				return hasNext();
			}
			if(!split[7].equals(trainIssue) && ! isTrain) {
				return hasNext();
			}
			
			
			id = split[0];
//			System.out.println(id+ " "+ trainIssue+ " "+split[7]);
			text = split[1];
			ad_score = split[2];
//			ad_score=String.valueOf(Double.parseDouble(split[2])*100);
			support_oppose_score = split[3];
			support_score = split[4];
			oppose_score = split[5];
			passion_score = split[6];
			issue=split[7];
			controversity=String.valueOf(1-Math.abs(Double.valueOf(ad_score)));
			
			
			return true;
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public Progress[] getProgress() {
		return null;
	}

}

