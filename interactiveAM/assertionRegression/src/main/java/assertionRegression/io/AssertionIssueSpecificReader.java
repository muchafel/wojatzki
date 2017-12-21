package assertionRegression.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import assertionRegression.annotationTypes.Issue;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class AssertionIssueSpecificReader  extends JCasCollectionReader_ImplBase {
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

	
	public static final String PARAM_TARGETCLASS = "TargetCLass";
	@ConfigurationParameter(name = PARAM_TARGETCLASS, mandatory = true)
	private String target;
	
	
	String id=null;
    private int tcId = 0;
	int counter=0;
//	int i=0;

	private List<String> issues;
	private Map<String,List<String>> issueToAssertion;
	
	@Override
	public void getNext(JCas aJCas) throws IOException, CollectionException {
		DocumentMetaData dmd = new DocumentMetaData(aJCas);
		dmd.setDocumentTitle("");
//		dmd.setDocumentId(String.valueOf(i++));
		dmd.setDocumentId(issues.get(counter));
		dmd.addToIndexes();

		
		aJCas.setDocumentLanguage(language);
		String text="";
		int start=0;

//		System.out.println(issueToAssertion.keySet());
//		System.out.println(issueToAssertion.get(issues.get(counter)));
//		System.out.println("*** "+issues.get(counter));
		for(String assertion: issueToAssertion.get(issues.get(counter))){
			String[] split = assertion.split("\t");
			String assertionText = split[1];
			text+=assertionText+"\n";
			
			
			int end=start+assertionText.length();
			TextClassificationTarget unit = new TextClassificationTarget(aJCas, start, end);
			TextClassificationOutcome o = new TextClassificationOutcome(aJCas, start, end);
			
			unit.setId(tcId++);
			if(target.equals("Agreement")){
				o.setOutcome(split[2]);
			}else if(target.equals("Support_ALL")){
				o.setOutcome(split[3]);
			}else if(target.equals("Support_AGREE")){
				o.setOutcome(split[4]);
			}else if(target.equals("Oppose_DISAGREE")){
				o.setOutcome(split[5]);
			}else if(target.equals("Passion")){
				o.setOutcome(split[6]);
			}else if(target.equals("Controversity")){
				o.setOutcome(String.valueOf(-Math.abs(Double.valueOf(split[2]))));
			}
			else{
				throw new IOException(target+ " not configured as target class.");
			}
			
//			System.out.println(assertionText+"\t"+split[2]+"\t"+split[7]);
			unit.addToIndexes();
			o.addToIndexes();
			start=end+1;
//			start=end;
		}
		
		
		aJCas.setDocumentText(text);
		
//		for(TextClassificationTarget unit: JCasUtil.select(aJCas, TextClassificationTarget.class)){
//			System.out.println(unit.getCoveredText());
//		}
//		System.out.println(text);
		
		Issue assertionIssue= new Issue(aJCas, 0, aJCas.getDocumentText().length());
		assertionIssue.setIssue(issues.get(counter));
		assertionIssue.addToIndexes();
		
		
		counter++;
	}

	@Override
	public boolean hasNext() {
		try {
			if (issues == null) {
				issues= new ArrayList<>();
				issueToAssertion= new HashMap<>();
				for(String line: FileUtils.readLines(inputFile)){
					String[] split = line.split("\t");
					String issue=split[7];
					if(!issues.contains(issue)){
						issues.add(issue);
						issueToAssertion.put(issue, new ArrayList<>());
					}
					issueToAssertion.get(issue).add(line);
				}
			}

			if(counter>=issues.size()){
				return false;
			}else{
				return true;
			}
			
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public Progress[] getProgress() {
		return null;
	}
}
