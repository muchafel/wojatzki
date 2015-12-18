package util.concepts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import conceptPolarityClassification.ConceptPolarityClassification_Experiment;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import types.StanceAnnotation;
import util.PreprocessingPipeline;
import util.SimilarityHelper;

public class ConceptInspection {
	
		
	public static void main(String[] args) throws UIMAException, IOException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		AnalysisEngine engine;
		AggregateBuilder aggregateBuilder= new AggregateBuilder();
        aggregateBuilder.add(PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos());
        engine=aggregateBuilder.createAggregate();
		
		for (File folder : getTopicFolders(baseDir + "/semevalTask6/targets/")) {
			System.out.println("+++ "+folder+" +++ ");
			List<String> stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");

			Set<String> concepts = ConceptUtils.getConcepts(folder, 20, stopwords);
			System.out.println("Normalized: " + concepts);
			String distribution="";
			for (String concept : concepts) {
				distribution+=conceptDistribution(concept,folder,engine)+System.lineSeparator();
			}
			System.out.println(distribution);
		}

	}
	
	private static String conceptDistribution(String concept, File folder,AnalysisEngine engine) throws ResourceInitializationException {
		FrequencyDistribution<String> fd = new FrequencyDistribution<String>();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, folder,
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
		
		Iterator<JCas> it= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
		int pro=0;
		int contra=0;
		while (it.hasNext()) {
			JCas jcas = it.next();
				String outcome= JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome();
				if (outcome.equals("FAVOR")) {
					if(conceptContained(concept,jcas,engine))pro++;
				}else if(outcome.equals("AGAINST")){
					if(conceptContained(concept,jcas,engine))contra++;
				}
		}
		String result= concept+"--> # in favor: "+String.valueOf(pro)+"  # against: "+String.valueOf(contra);
		return result;
	}

	private static boolean conceptContained(String concept, JCas jcas, AnalysisEngine engine) {
		try {
			engine.process(jcas);
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
		//just use FAVOR and AGAINST
		
		//check if one noun equals the concept
		for(Token t: JCasUtil.select(jcas, Token.class)){
			if(t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NNS")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NPS")){
				if(t.getCoveredText().equals(concept)||SimilarityHelper.wordsAreSimilar(t.getCoveredText(),concept)){
					return true;
				}
			}
		}
		return false;
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
//			if (!f.getName().equals("HillaryClinton")) {
//				continue;
//			}
			if (!f.getName().equals("Atheism")) {
				continue;
			}
			if (f.isDirectory())
				folders.add(f);
		}
		return folders;
	}
}
