package util.lexiconGenerationUtils;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.util.NGramUtils;
import io.TaskATweetReader;
import mulan.examples.GettingPredictionsOnUnlabeledData;
import types.OriginalResource;
import types.StanceAnnotation;
import util.CollocationMeasureHelper;
import util.PreprocessingPipeline;

/**
 * creates a lexicon in the form TOKEN(String):STANCE(float)
 * on the basis of the two frequency distributions (tokens of tweets in favour vs tokens of tweets against)
 * uses gmean score to extract the difference between expected (under 0 hypothesis) and observed frequency
 * 
 * @author michael
 *
 */
public class CreateStanceLexiconTargetWise {
	public static final String TOPIC_FOLDERS = "/semevalTask6/targets/";
	/**
	 * stopwords from https://code.google.com/p/stop-words/
	 * 
	 * @param args
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static void main(String[] args) throws IOException, UIMAException {

		List<String> stopwords = new ArrayList<String>();
//				init("src/main/resources/lists/stop-words_english_6_en.txt");
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		// String target = "Atheism";
//		List<String> targets = new ArrayList<String>(Arrays.asList(
//		"Atheism",
//		 "Climate Change is a Real Concern",
//		 "Feminist Movement",
//		 "Hillary Clinton",
//		 "Legalization of Abortion"
//		));


		for (File folder : getTopicFolders(baseDir + TOPIC_FOLDERS)) {
			CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
					TaskATweetReader.PARAM_LANGUAGE, "en", TaskATweetReader.PARAM_SOURCE_LOCATION,
					folder.getAbsolutePath(), TaskATweetReader.PARAM_PATTERNS, "*.xml");
			
			// create favor and against fds foreach target
			FrequencyDistribution<String> favour = new FrequencyDistribution<String>();
			FrequencyDistribution<String> against = new FrequencyDistribution<String>();
			for (JCas jcas : new JCasIterable(reader)) {
					AnalysisEngineFactory
							.createEngine(PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos())
							.process(jcas);

						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("FAVOR")) {
							System.out.println(incAll(favour, JCasUtil.select(jcas, Token.class), stopwords).getKeys());
							favour = incAll(favour, JCasUtil.select(jcas, Token.class), stopwords);
						}
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("AGAINST")) {
							against = incAll(against, JCasUtil.select(jcas, Token.class), stopwords);
						}
				}
			mkdirs(folder.getName()+"_ordered");
			write(folder.getName()+"_ordered","favor",favour.getMostFrequentSamples(500),favour);
			write(folder.getName()+"_ordered","against",against.getMostFrequentSamples(500),against);
			Map<String, Float> lexcicon = createLexicon(favour, against);
			writeLexicon(folder.getName()+"_ordered", lexcicon);
		}

	}
	
	
		private static void write(String target, String polarity, List<String> mostFrequentSamples, FrequencyDistribution<String> fd) {
			try (PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("src/main/resources/lists/stanceLexicons/" + target + "/"+polarity+".txt", true)))) {
				for (String key : mostFrequentSamples) {
					out.println(key +" "+fd.getCount(key));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
/**
 * writes to the specified resource in the form Token:Stance
 * @param target
 * @param lexcicon
 */
	private static void writeLexicon(String target, Map<String, Float> lexcicon) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("src/main/resources/lists/stanceLexicons/" + target + "/stanceLexicon_top500.txt", true)))) {
			for (String key : lexcicon.keySet()) {
				out.println(key + ":" + lexcicon.get(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
 * creates basic map structure
 * @param favour
 * @param against
 * @return
 */
	private static Map<String, Float> createLexicon(FrequencyDistribution<String> favour,
			FrequencyDistribution<String> against) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		//add all cands. dublicates will be removed because map stores just unique entries
		candidates.addAll(favour.getKeys());
		candidates.addAll(against.getKeys());
		
		CollocationMeasureHelper helper = new CollocationMeasureHelper(favour, against);
		
		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfGMeans(word));
		}

		return sortMap(lexcicon);
	}

	private static Map<String, Float> sortMap(Map<String, Float> unsortMap) {
		// Convert Map to List
				List<Map.Entry<String, Float>> list = 
					new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

				// Sort list with comparator, to compare the Map values
				Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
					public int compare(Map.Entry<String, Float> o1,
		                                           Map.Entry<String, Float> o2) {
						return (o1.getValue()).compareTo(o2.getValue());
					}
				});

				// Convert sorted map back to a Map
				Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
				for (Iterator<Map.Entry<String, Float>> it = list.iterator(); it.hasNext();) {
					Map.Entry<String, Float> entry = it.next();
					sortedMap.put(entry.getKey(), entry.getValue());
				}
	return sortedMap;
}


	private static void mkdirs(String target) {
		File dir = new File("src/main/resources/lists/stanceLexicons/" + target);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private static List<String> init(String path) throws IOException {
		List<String> negationWords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				negationWords.add(line);
			}
		}
		return negationWords;
	}

	private static FrequencyDistribution<String> incAll(FrequencyDistribution<String> freq, Collection<Token> tokens,
			List<String> stopwords) {
		for (Token t : tokens) {
			if (!stopwords.contains(t.getCoveredText())) {
				freq.inc(t.getCoveredText());
			}
		}
		return freq;
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
