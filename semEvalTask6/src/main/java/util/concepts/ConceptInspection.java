package util.concepts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;

import conceptPolarityClassification.ConceptPolarityClassification_Experiment;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import util.PreprocessingPipeline;

public class ConceptInspection {
	public static void main(String[] args) throws UIMAException, IOException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		AnalysisEngineDescription preProcessing = PreprocessingPipeline.getPreprocessingSentimentFunctionalStanceAnno();

		for (File folder : getTopicFolders(baseDir + "/semevalTask6/targets/")) {

			List<String> stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");

			Set<String> concepts = ConceptUtils.getConcepts(folder, 5, stopwords);
			System.out.println("Normalized: " + concepts);
			for (String concept : concepts) {
				
			}
		}

	}
	
	/**
	 * read in a file and return a list of strings
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected static List<String> init(String path) throws IOException {
		List<String> stopwords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
		}
		return stopwords;
	}
	
	
	private static List<File> getTopicFolders(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<File> folders = new ArrayList<File>();
		for (File f : listOfFiles) {
			if (!f.getName().equals("HillaryClinton")) {
				continue;
			}
//			if (!f.getName().equals("Atheism")) {
//				continue;
//			}
			if (f.isDirectory())
				folders.add(f);
		}
		return folders;
	}
}
