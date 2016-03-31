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
import java.util.HashSet;
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
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.features.ngram.util.NGramUtils;
import io.TaskATweetReader;
import mulan.examples.GettingPredictionsOnUnlabeledData;
import types.OriginalResource;
import types.StanceAnnotation;
import util.CollocationMeasureHelper;
import util.PreprocessingPipeline;

/**
 * 
 * @author michael
 *
 */
public class TargetLexiconOverlapping {
	/**
	 * stopwords from https://code.google.com/p/stop-words/
	 * 
	 * @param args
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static void main(String[] args) throws IOException, UIMAException {

		List<String> stopwords = new ArrayList<>();
//				init("src/main/resources/lists/stop-words_english_6_en.txt");
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		// String target = "Atheism";
		List<String> targets = new ArrayList<String>(Arrays.asList(
		"Atheism",
		 "Climate Change is a Real Concern",
		 "Feminist Movement",
		 "Hillary Clinton",
		 "Legalization of Abortion"
		));

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskA",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

		List<FrequencyDistribution<String>> fdFavor= new ArrayList<FrequencyDistribution<String>>();
		List<FrequencyDistribution<String>> fdAgainst= new ArrayList<FrequencyDistribution<String>>();
		
		for (String target : targets) {
			// create favor and against fds foreach target
			FrequencyDistribution<String> favour = new FrequencyDistribution<String>();
			FrequencyDistribution<String> against = new FrequencyDistribution<String>();
			for (JCas jcas : new JCasIterable(reader)) {
				if (JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget().equals(target)) {
					AnalysisEngineFactory
							.createEngine(PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos())
							.process(jcas);

					if (JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget().equals(target)) {
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("FAVOR")) {
//							System.out.println(incAll(favour, JCasUtil.select(jcas, Token.class), stopwords).getKeys());
							favour = incAll(favour, JCasUtil.select(jcas, Token.class), stopwords);
						}
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("AGAINST")) {
							against = incAll(against, JCasUtil.select(jcas, Token.class), stopwords);
						}
					}
				}
			}
			mkdirs(target);
			fdFavor.add(favour);
			fdAgainst.add(against);
		}
		Set<String> overlapFavor=getOverlap(fdFavor);
		Set<String> overlapAgainst=getOverlap(fdAgainst);
		write(overlapFavor);
		write(overlapAgainst);
	}
	private static void write(Set<String> overlap) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("src/main/resources/lists/stanceLexicons/overlap.txt", true)))) {
			for (String key : overlap) {
				out.println(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static Set<String> getOverlap(List<FrequencyDistribution<String>> fds) {
		Set<String> result= fds.get(0).getKeys();
		for(FrequencyDistribution<String> fd : fds){
				result.retainAll(fd.getKeys());
		}
	return result;
	}
/**
 * writes to the specified resource in the form Token:Stance
 * @param target
 * @param lexcicon
 */
	private static void writeLexicon(String target, Map<String, Float> lexcicon) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("src/main/resources/lists/stanceLexicons/" + target + "/stanceLexicon.txt", true)))) {
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

		return lexcicon;
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

}
