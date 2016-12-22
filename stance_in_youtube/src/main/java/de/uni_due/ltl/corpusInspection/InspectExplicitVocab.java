package de.uni_due.ltl.corpusInspection;

import static de.uni_due.ltl.util.TargetSets.targets_Set1;
import static de.uni_due.ltl.util.TargetSets.targets_Set2;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import java.io.IOException;
import java.util.ArrayList;
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

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.JCasId;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.features.ngram.util.NGramUtils;

import curated.Explicit_Stance_Set1;
import curated.Explicit_Stance_Set2;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.uni_due.ltl.simpleClassifications.FunctionalPartsAnnotator;
import de.uni_due.ltl.util.CollocationMeasureHelper;
import io.YouTubeSubDebateReader;
import preprocessing.CommentText;
import preprocessing.Users;

public class InspectExplicitVocab {

	public static void main(String[] args) throws ResourceInitializationException, IOException, AnalysisEngineProcessException {
		AnalysisEngine engine= getPreprocessingEngine();

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		inspectVocab(engine,baseDir);
//		inspectDistribution(baseDir,engine);
	}

	

	private static void inspectVocab(AnalysisEngine engine, String baseDir) throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				YouTubeSubDebateReader.class, YouTubeSubDebateReader.PARAM_SOURCE_LOCATION,
				baseDir + "/youtubeStance/corpus_curated_woInference/bin_preprocessed/",
				YouTubeSubDebateReader.PARAM_LANGUAGE, "en", YouTubeSubDebateReader.PARAM_PATTERNS, "*.bin",
				YouTubeSubDebateReader.PARAM_TARGET_LABEL, "DEATH PENALTY", YouTubeSubDebateReader.PARAM_TARGET_SET,
				"1", YouTubeSubDebateReader.PARAM_MERGE_TO_BINARY, false, YouTubeSubDebateReader.PARAM_EXCLUDE_NONE_DEBATE_STANCE,false);
		for (String explicitTarget : targets_Set1) {
			
			FrequencyDistribution<String> tokens= new FrequencyDistribution<>();
			
			for (JCas jcas : new JCasIterable(reader)) {
				for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
					for(curated.Explicit_Stance_Set1 explicitTarget1: JCasUtil.selectCovered(curated.Explicit_Stance_Set1.class, sentence)){
						if(explicitTarget1.getTarget().equals(explicitTarget) && !explicitTarget1.getPolarity().equals("NONE")){
							System.out.println(explicitTarget1.getCoveredText());
							List<String> words= getWords(explicitTarget1);
							tokens.incAll(words);
						}
					}
				}
			}
			System.out.println(explicitTarget+" "+tokens.getMostFrequentSamples(40));
			System.out.println();
		}
		for (String explicitTarget : targets_Set2) {
			
			FrequencyDistribution<String> tokens= new FrequencyDistribution<>();
			
			for (JCas jcas : new JCasIterable(reader)) {
				for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
					for(curated.Explicit_Stance_Set2 explicitTarget2: JCasUtil.selectCovered(curated.Explicit_Stance_Set2.class, sentence)){
						if(explicitTarget2.getTarget().equals(explicitTarget) && !explicitTarget2.getPolarity().equals("NONE")){
							System.out.println(explicitTarget2.getCoveredText());
							List<String> words= getWords(explicitTarget2);
							tokens.incAll(words);
						}
					}
				}
			}
			System.out.println(explicitTarget+" "+tokens.getMostFrequentSamples(40));
			System.out.println();
		}
		
	}



	private static List<String> getWords(Explicit_Stance_Set2 explicitTarget2) {
		List<String> words= new ArrayList<>();
		for(Token t: JCasUtil.selectCovered(Token.class,explicitTarget2)){
			words.add(t.getCoveredText().toLowerCase());
		}
		return words;
	}



	private static List<String> getWords(Explicit_Stance_Set1 explicitTarget1) {
		List<String> words= new ArrayList<>();
		for(Token t: JCasUtil.selectCovered(Token.class,explicitTarget1)){
			words.add(t.getCoveredText().toLowerCase());
		}
		return words;
	}



	private static void inspectDistribution(String baseDir, AnalysisEngine engine) throws ResourceInitializationException, AnalysisEngineProcessException {
		for (String explicitTarget : targets_Set1) {
			CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
					YouTubeSubDebateReader.class, YouTubeSubDebateReader.PARAM_SOURCE_LOCATION,
					baseDir + "/youtubeStance/corpus_curated_woInference/bin_preprocessed/",
					YouTubeSubDebateReader.PARAM_LANGUAGE, "en", YouTubeSubDebateReader.PARAM_PATTERNS, "*.bin",
					YouTubeSubDebateReader.PARAM_TARGET_LABEL, explicitTarget, YouTubeSubDebateReader.PARAM_TARGET_SET,
					"1", YouTubeSubDebateReader.PARAM_MERGE_TO_BINARY, false, YouTubeSubDebateReader.PARAM_EXCLUDE_NONE_DEBATE_STANCE,false);

			FrequencyDistribution<String> fd_favor= new FrequencyDistribution<>();
			FrequencyDistribution<String> fd_against= new FrequencyDistribution<>();
			FrequencyDistribution<String> fd_none= new FrequencyDistribution<>();
			FrequencyDistribution<String> fd_present= new FrequencyDistribution<>();
			
			FrequencyDistribution<String> fd_none_bigrams= new FrequencyDistribution<>();
			FrequencyDistribution<String> fd_present_bigrams= new FrequencyDistribution<>();
			
			FrequencyDistribution<String> fd_none_trigrams= new FrequencyDistribution<>();
			FrequencyDistribution<String> fd_present_trigrams= new FrequencyDistribution<>();


			for (JCas jcas : new JCasIterable(reader)) {
				jcas.setDocumentLanguage("en");
				FrequencyDistribution<String> polarity= new FrequencyDistribution<>();
				engine.process(jcas);
				for (TextClassificationOutcome outcome : JCasUtil.select(jcas, TextClassificationOutcome.class)) {
					if (outcome.getOutcome().equals("FAVOR")) {
						polarity.inc("PRESENT");
//						fd_present=incTokens(fd_present,JCasUtil.selectCovered(CommentText.class,outcome).iterator().next());
//						fd_favor=incTokens(fd_favor,JCasUtil.selectCovered(CommentText.class,outcome).iterator().next());
//						fd_present_bigrams=incNgrams(fd_present_bigrams, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),2));
//						fd_present_trigrams=incNgrams(fd_present_trigrams, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),3));
					}
					if (outcome.getOutcome().equals("AGAINST")) {
						polarity.inc("PRESENT");
//						fd_present=incTokens(fd_present,JCasUtil.selectCovered(CommentText.class,outcome).iterator().next());
//						fd_against=incTokens(fd_against,JCasUtil.selectCovered(CommentText.class,outcome).iterator().next());
//						fd_present_bigrams=incNgrams(fd_present_bigrams, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),2));
//						fd_present_trigrams=incNgrams(fd_present_trigrams, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),3));
					}
					if (outcome.getOutcome().equals("NONE")) {
						polarity.inc("NONE");
//						fd_none=incTokens(fd_none,JCasUtil.selectCovered(CommentText.class,outcome).iterator().next());
//						fd_none_bigrams=incNgrams(fd_none_bigrams, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),2));
//						fd_none_trigrams=incNgrams(fd_none_trigrams, getNgrams(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next(),3));
					}
				}
				System.out.println(JCasUtil.select(jcas,DocumentMetaData.class).iterator().next().getDocumentId());
				System.out.println(explicitTarget+" PRESENT:"+polarity.getCount("PRESENT")+" NONE:"+polarity.getCount("NONE"));
			}
//			System.out.println(explicitTarget+" "+fd_against.getMostFrequentSamples(50)+" "+fd_favor.getMostFrequentSamples(50));
//			Set<String> presentWords=fd_present.getKeys();
//			Set<String> presentWords=getVocabWithMin2Occurrences(fd_present);
//			presentWords.removeAll(fd_none.getKeys());
//			System.out.println(StringUtils.join(presentWords,"\",\""));
//			System.out.println("");

//			Map<String, Float> map=createLexiconMap(fd_present,fd_none);
//			map = sortMap(map);
//			int i=0;
//			Set<String> presentWords=new HashSet<>();
//			for(String word:map.keySet()){
//				System.out.println(word+" "+map.get(word));
//				if (map.get(word) > 0.005) {
//					presentWords.add(word);
//				}else{
//					break;
//				}
//			}
			Map<String, Float> map=createLexiconMap(fd_present_trigrams,fd_none_trigrams);
			map = sortMap(map);
			Set<String> presentWords=new HashSet<>();
			for(String word:map.keySet()){
				System.out.println(word+" "+map.get(word));
				if (map.get(word) > 0.005) {
					presentWords.add(word);
				}else{
					break;
				}
			}
			System.out.println(StringUtils.join(presentWords,"\",\""));
			System.out.println();
		}
		
	}



	private static FrequencyDistribution<String> incNgrams(FrequencyDistribution<String> fd,
			FrequencyDistribution<String> currentNgrams) {
		for(String n_gram:currentNgrams.getKeys()){
			fd.addSample(n_gram, currentNgrams.getCount(n_gram));
		}
		return fd;
	}



	private static FrequencyDistribution<String> getNgrams(CommentText text, int n_gramSize) {
		FrequencyDistribution<String> annoNgrams = new FrequencyDistribution<String>();

		for (List<String> ngram : new NGramStringListIterable(toText(selectCovered(Token.class, text)), n_gramSize,
				n_gramSize)) {
			ngram = lower(ngram);
			String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
			annoNgrams.inc(ngramString);
		}
		return annoNgrams;
	}



	private static Set<String> getVocabWithMin2Occurrences(FrequencyDistribution<String> fd) {
		Set<String> result= new HashSet<>();
		for(String word: fd.getKeys()){
			if(fd.getCount(word)>1){
				result.add(word);
			}
		}
		return result;
	}

	private static FrequencyDistribution<String> incTokens(FrequencyDistribution<String> fd, CommentText text) {
		for(Token t: JCasUtil.selectCovered(Token.class, text)){
			if(t.getPos().getPosValue().equals("N")||t.getPos().getPosValue().equals("V")||t.getPos().getPosValue().equals("A")){
				fd.inc(t.getCoveredText().toLowerCase());
			}
		}
		return fd;
	}

	/**
	 * uses the two FrequencyDistributions to generate a map by calculating
	 * getDiffOfGMeans for each word in the two distributions
	 * 
	 * @param favour
	 * @param fd2
	 * @return
	 */
	protected static Map<String, Float> createLexiconMap(FrequencyDistribution<String> fd1,
			FrequencyDistribution<String> fd2) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		// add all cands. dublicates will be removed because map stores just
		// unique entries
		candidates.addAll(fd1.getKeys());
		candidates.addAll(fd2.getKeys());

		CollocationMeasureHelper helper = new CollocationMeasureHelper(fd1, fd2);

		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfDice(word));
		}

		return lexcicon;
	}
	
	private static Map<String, Float> sortMap(Map<String, Float> unsortMap) {
		// Convert Map to List
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Collections.reverse(list);

		// Convert sorted map back to a Map
		Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
		for (Iterator<Map.Entry<String, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static AnalysisEngine getPreprocessingEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(ArktweetPosTagger.class)
					));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
	
	public static List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}
}
