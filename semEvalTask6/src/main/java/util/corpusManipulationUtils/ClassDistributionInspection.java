package util.corpusManipulationUtils;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import dataInspection.PreprocessingTwitterSpecificAnnotatorInspector;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import types.TwitterSpecificPOS;
import util.PreprocessingPipeline;

public class ClassDistributionInspection {

	public static final String TOPIC_FOLDERS = "/semevalTask6/targets/";

	public static void main(String[] args) throws UIMAException, IOException {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);

		for (File folder : getTopicFolders(baseDir + TOPIC_FOLDERS)) {
			CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
					TaskATweetReader.PARAM_LANGUAGE, "en", TaskATweetReader.PARAM_SOURCE_LOCATION,
					folder.getAbsolutePath(), TaskATweetReader.PARAM_PATTERNS, "*.xml");
			int favor=0;
			int against=0;
			int none=0;
			
			for (JCas jcas : new JCasIterable(reader)) {
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("FAVOR")) {
					favor++;
				}
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("AGAINST")) {
					against++;
				}
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("NONE")) {
					none++;
				}
			}
			System.out.println(folder);
			System.out.println("favor: "+favor);
			System.out.println("against: "+against);
			System.out.println("none: "+none);
		}
	}
	private static List<File> getTopicFolders(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<File> folders= new ArrayList<File>();
		for(File f: listOfFiles){
			if(f.isDirectory())folders.add(f);
		}
		return folders;
	}

}
