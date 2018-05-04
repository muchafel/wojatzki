package io;

import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;

public class AssertionReader extends JCasCollectionReader_ImplBase {
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
	
	
	String text = null;
	String ad_score = null;
	String hatespeechScore = null;
	int i=0;

	private BufferedReader br;

	
	@Override
	public void getNext(JCas aJCas) throws IOException, CollectionException {
		DocumentMetaData dmd = new DocumentMetaData(aJCas);
		dmd.setDocumentTitle("");
		dmd.setDocumentId(String.valueOf(i++));
//		System.out.println(i++);
//		dmd.setDocumentId(id);
		dmd.addToIndexes();

		aJCas.setDocumentText(text);
//		System.out.println(text);
		aJCas.setDocumentLanguage(language);

		TextClassificationOutcome o = new TextClassificationOutcome(aJCas, 0, aJCas.getDocumentText().length());
		if(target.equals("Agreement")){
			o.setOutcome(ad_score);
		}else if(target.equals("HateSpeech")){
			o.setOutcome(hatespeechScore);
		}
		else{
			throw new IOException(target+ " not configured as target class.");
		}
		
		
//		System.out.println(text+ " "+ o);
		o.addToIndexes();
	}

	@Override
	public boolean hasNext() {
		try {
			if (br == null) {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
			}

			String entry = br.readLine();

			if (entry == null || entry.isEmpty()) {
				return false;
			}

			String[] split = entry.split("\t");
			
			text = split[0];
//			System.out.println(text);
			hatespeechScore = split[1];
			ad_score = split[2];
			
			
			
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
