package util.corpusManipulationUtils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import types.OriginalResource;
import types.StanceAnnotation;

public class SplitCorpusAccordingToOutcomeAndTarget {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskA", TaskATweetReader.PARAM_PATTERNS,
				"*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",TaskATweetReader.PARAM_MEMORIZE_RESOURCE,true);
		
		for (JCas jcas : new JCasIterable(reader)) {
			String outcome=JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome();
			if(outcome.equals("FAVOR")||outcome.equals("AGAINST")){
				String target =JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget();
				target=target.replace(" ", "");
				String fileName= JCasUtil.select(jcas, OriginalResource.class).iterator().next().getFileName();
				File jcasResource= new File(JCasUtil.select(jcas, OriginalResource.class).iterator().next().getLocation());
				
				createFolder(baseDir+"/semevalTask6/favorVsAgainst/targets",target);
				String newFile= baseDir+"/semevalTask6/favorVsAgainst/targets/"+target+"/"+fileName;
				System.out.println("copy file "+jcasResource+ " to "+newFile);
				FileUtils.copyFile(jcasResource, new File(newFile));
			}
		}
	}

	private static void createFolder(String dir, String target) {
		File folder = new File(dir+"/targets/"+target);
		// if the directory does not exist, create it
		if (!folder.exists()) {
		    System.out.println("creating directory: " + dir+"/targets/"+target);
		    try{
		        folder.mkdir();
		    } 
		    catch(SecurityException se){
		    	se.printStackTrace();
		    }        
		}
		
	}

}
