package assertionRegression.corpusProcessing;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import com.ibm.icu.text.CharsetDetector;

import assertionRegression.annotationTypes.Issue;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase.Resource;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;

public class IssueTweetReader extends JCasCollectionReader_ImplBase
{
	
	
	Iterator<File> files;
	
	
	
	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true)
	private File inputFile;
	
	/**
	 * Automatically detect encoding.
	 *
	 * @see CharsetDetector
	 */
	public static final String ENCODING_AUTO = "auto";

	/**
	 * Name of configuration parameter that contains the character encoding used by the input files.
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


	private String getText(File file ) throws IOException {
		StringBuilder sb= new StringBuilder();
		for(String line: FileUtils.readLines(file, "UTF-8")){
			
//			System.out.println(line);
			String[] parts=line.split("\t");
			if(!line.isEmpty() && parts.length>2){
				//if line != retweet
				if(parts[2].equals("false")){
					sb.append(parts[1].replaceAll("\n", " ")+"\n");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		if(files==null){
			List<File> fileList= Arrays.asList(inputFile.listFiles());
			files = fileList.iterator();
		}
		
		return files.hasNext();
	}

	@Override
	public Progress[] getProgress() {
		return null;
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		File file= files.next();
		jCas.setDocumentText(getText(file));
		jCas.setDocumentLanguage(language);
		
		DocumentMetaData dmd = new DocumentMetaData(jCas);
		dmd.setDocumentTitle(file.getName());
		dmd.setDocumentId(file.getName());
		dmd.addToIndexes();
	}
}
