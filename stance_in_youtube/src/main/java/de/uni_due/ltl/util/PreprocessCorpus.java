package de.uni_due.ltl.util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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

import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasWriter;
import de.tudarmstadt.ukp.dkpro.core.sentiment.type.StanfordSentimentAnnotation;
import de.uni_due.ltl.corpusInspection.SentimentCommentAnnotator;
import de.uni_due.ltl.simpleClassifications.FunctionalPartsAnnotator;
import io.YouTubeReader;
import preprocessing.CommentText;

public class PreprocessCorpus {

	public static void main(String[] args) throws ResourceInitializationException, AnalysisEngineProcessException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");

		AnalysisEngine engineSentiment= getSentimentPreprocessingEngine();
//		inspectSentimemts(reader,engineSentiment);

	}

	private static AnalysisEngine getSentimentPreprocessingEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(FunctionalPartsAnnotator.class),
					createEngineDescription(SentimentCommentAnnotator.class)
					,createEngineDescription(BinaryCasWriter.class,BinaryCasWriter.PARAM_OVERWRITE, true, BinaryCasWriter.PARAM_TARGET_LOCATION,"/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin_preprocessed")
					));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
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
	
	
}
