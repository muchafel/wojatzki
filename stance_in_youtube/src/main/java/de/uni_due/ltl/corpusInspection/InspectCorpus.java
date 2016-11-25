package de.uni_due.ltl.corpusInspection;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.sentiment.type.StanfordSentimentAnnotation;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.uni_due.ltl.simpleClassifications.FunctionalPartsAnnotator;
import de.uni_due.ltl.simpleClassifications.SentimentCommentAnnotator;
import io.YouTubeReader;
import preprocessing.CommentText;
import preprocessing.CommentType;
import preprocessing.Users;

public class InspectCorpus {

	public static void main(String[] args) throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");
		AnalysisEngine engine= getPreprocessingEngine();
		
		
		printVocab(reader,engine);
//		inspectExplicitTarget(engine);
//		inspectOutcomePerDoc(reader,engine);
//		inspectAuthorAndRefereesPerPolarity();
//		inspectAuthorAndReferees(reader,engine);
//		inspectUsersAndCommentType(reader,engine);
//		inspectText(reader,engine);
//		inspectOutcome(reader,engine);
//		AnalysisEngine engineSentiment= getSentimentPreprocessingEngine();
//		inspectSentimemts(reader,engineSentiment);
	}

	private static void printVocab(CollectionReaderDescription reader, AnalysisEngine engine) throws AnalysisEngineProcessException, IOException {
		FrequencyDistribution<String> words= new FrequencyDistribution<>();
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			Collection<CommentText> comments=JCasUtil.select(jcas,CommentText.class);
			for (CommentText comment : comments) {
				for(Token t: JCasUtil.selectCovered(Token.class, comment)){
					words.inc(t.getCoveredText().toLowerCase());
				}
			}
		}
		int i=0;
		for(String word: words.getMostFrequentSamples(words.getKeys().size())){
//			System.out.println(word +"\t"+words.getCount(word));
			i++;
			FileUtils.write(new File("src/main/resources/list/vocab"), word+System.lineSeparator(), true);
		}
		System.out.println(i);
	}

	private static void inspectSentimemts(CollectionReaderDescription reader, AnalysisEngine engineSentiment) throws AnalysisEngineProcessException {
		for (JCas jcas : new JCasIterable(reader)) {
			engineSentiment.process(jcas);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
//				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
				System.out.println(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getCoveredText());
				for(StanfordSentimentAnnotation sentiment:JCasUtil.selectCovered(StanfordSentimentAnnotation.class,outcome)){
					System.out.println("++ "+sentiment.getVeryPositive()+"\t"+sentiment.getPositive()+"\t"+sentiment.getNeutral()+"\t"+sentiment.getNegative()+"\t"+sentiment.getVeryNegative()+" --");
				}
			}
		}
	}

	private static void inspectExplicitTarget(AnalysisEngine engine) throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"Death Penalty (Debate)", YouTubeReader.PARAM_TARGET_SET,"1");
		int i=0;
		for (JCas jcas : new JCasIterable(reader)) {
			DocumentMetaData metaData = DocumentMetaData.get(jcas);
			String id = metaData.getDocumentId();
			
			for (TextClassificationOutcome outcome : JCasUtil.select(jcas, TextClassificationOutcome.class)) {
				if(!outcome.getOutcome().equals("NONE")){
					System.out.println(outcome.getOutcome());
					i++;
				}
			}
		}
		System.out.println(i);
	}

	private static void inspectOutcomePerDoc(CollectionReaderDescription reader, AnalysisEngine engine) {
		Map<String,FrequencyDistribution<String>> docId2outcome= new HashMap<>();
		for (JCas jcas : new JCasIterable(reader)) {
			DocumentMetaData metaData= DocumentMetaData.get(jcas);
			String id= metaData.getDocumentId();
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
				if(docId2outcome.containsKey(id)){
					docId2outcome.get(id).inc(outcome.getOutcome());
				}else{
					FrequencyDistribution<String> fd= new FrequencyDistribution<>();
					fd.inc(outcome.getOutcome());
					docId2outcome.put(id, fd);
				}
			}
		}
		for(String id:docId2outcome.keySet()){
			System.out.println(id);
			printfd(docId2outcome.get(id));
		}
		
	}

	private static void inspectOutcome(CollectionReaderDescription reader, AnalysisEngine engine) {
		for (JCas jcas : new JCasIterable(reader)) {
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
				List<String> tokens=getList(JCasUtil.selectCovered(jcas,Token.class,outcome));
				System.out.println(StringUtils.join(tokens, " - "));
			}
		}
		
	}

	private static void inspectText(CollectionReaderDescription reader, AnalysisEngine engine) throws AnalysisEngineProcessException {
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
				System.out.println(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getCoveredText());
			}
		}
		
	}

	private static void inspectUsersAndCommentType(CollectionReaderDescription reader, AnalysisEngine engine) throws AnalysisEngineProcessException {
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
				System.out.println(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor()+" "+JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
				System.out.println(JCasUtil.selectCovered(CommentType.class,outcome).iterator().next().getCommentNotReply());
			}
		}
		
	}

	private static void inspectAuthorAndReferees(CollectionReaderDescription reader, AnalysisEngine engine) throws AnalysisEngineProcessException {
		/**
		 * users & refereees
		 */
		FrequencyDistribution<String> fd_author= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_referee= new FrequencyDistribution<>();
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				fd_author.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor());
				fd_referee.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
			}
		}
		System.out.println("# of authors "+(fd_author.getN()-fd_author.getCount("None")));
		System.out.println("# of referees "+(fd_referee.getN()-fd_referee.getCount("None")));
//		printfd(fd_author);
//		printfd(fd_referee);
		
	}

	private static void inspectAuthorAndRefereesPerPolarity(CollectionReaderDescription reader, AnalysisEngine engine) throws AnalysisEngineProcessException {
		/**
		 * users & refereees
		 */
		FrequencyDistribution<String> fd_author_FAVOR= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_referee_FAVOR= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_author_AGAINST= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_referee_AGAINST= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_author_NONE= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_referee_NONE= new FrequencyDistribution<>();
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				if(outcome.getOutcome().equals("FAVOR")){
					fd_author_FAVOR.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor());
					fd_referee_FAVOR.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
					}
				if(outcome.getOutcome().equals("AGAINST")){
					fd_author_AGAINST.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor());
					fd_referee_AGAINST.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
					}
				if(outcome.getOutcome().equals("NONE")){
					fd_author_NONE.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor());
					fd_referee_NONE.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
					}
				}
			}
		System.out.println("--- FAVOR ---");
		System.out.println(fd_author_FAVOR.getN()-fd_author_FAVOR.getCount("Unknown"));
		System.out.println(fd_referee_FAVOR.getN()-fd_referee_FAVOR.getCount("None"));
		System.out.println(((double)fd_referee_FAVOR.getN()-fd_referee_FAVOR.getCount("None"))/821);
		System.out.println("--- AGAINST ---");
		System.out.println(fd_author_AGAINST.getN()-fd_author_AGAINST.getCount("Unknown"));
		System.out.println(fd_referee_AGAINST.getN()-fd_referee_AGAINST.getCount("None"));
		System.out.println(((double)fd_referee_AGAINST.getN()-fd_referee_AGAINST.getCount("None"))/821);
		System.out.println("--- NONE ---");
		System.out.println(fd_author_NONE.getN()-fd_author_NONE.getCount("Unknown"));
		System.out.println(fd_referee_NONE.getN()-fd_referee_NONE.getCount("None"));
		System.out.println(((double)fd_referee_NONE.getN()-fd_referee_NONE.getCount("None"))/821);
		
	}

	private static void printfd(FrequencyDistribution<String> fd) {
		for(String sample: fd.getMostFrequentSamples(fd.getKeys().size())){
			System.out.println(sample+" "+fd.getCount(sample));
		}
		
	}

	private static List<String> getList(List<Token> selectCovered) {
		List<String> result= new ArrayList<>();
		for(Token token: selectCovered){
			result.add(token.getCoveredText());
		}
		return result;
	}
	
	private static AnalysisEngine getPreprocessingEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(FunctionalPartsAnnotator.class)
					));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

	
	private static AnalysisEngine getSentimentPreprocessingEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(FunctionalPartsAnnotator.class),
					createEngineDescription(SentimentCommentAnnotator.class)
					));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

}
