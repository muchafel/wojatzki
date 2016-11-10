package de.uni_due.ltl.corpusInspection;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.List;

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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.uni_due.ltl.simpleClassifications.FunctionalPartsAnnotator;
import io.YouTubeReader;
import preprocessing.CommentText;
import preprocessing.CommentType;
import preprocessing.Users;

public class InspectCorpus {

	public static void main(String[] args) throws ResourceInitializationException, AnalysisEngineProcessException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");
		AnalysisEngine engine= getPreprocessingEngine();
		
		
		
		
		FrequencyDistribution<String> fd_author= new FrequencyDistribution<>();
		FrequencyDistribution<String> fd_referee= new FrequencyDistribution<>();
		for (JCas jcas : new JCasIterable(reader)) {
			engine.process(jcas);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				fd_author.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor());
				fd_referee.inc(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
			}
		}
//		printfd(fd_author);
//		printfd(fd_referee);
		
		
		/**
		 * inspect users & comment type
		 */
//		for (JCas jcas : new JCasIterable(reader)) {
//			engine.process(jcas);
//			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
//				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
//				System.out.println(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor()+" "+JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
//				System.out.println(JCasUtil.selectCovered(CommentType.class,outcome).iterator().next().getCommentNotReply());
//			}
//		}
		
		/**
		 * inspect comment text
		 */
//		for (JCas jcas : new JCasIterable(reader)) {
//			engine.process(jcas);
//			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
//				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
//				System.out.println(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getCoveredText());
//			}
//		}
		
		/**
		 * inspect outcome
		 */
//		for (JCas jcas : new JCasIterable(reader)) {
//			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
//				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
//				List<String> tokens=getList(JCasUtil.selectCovered(jcas,Token.class,outcome));
//				System.out.println(StringUtils.join(tokens, " - "));
//			}
//		}
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
					createEngineDescription(FunctionalPartsAnnotator.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

}
