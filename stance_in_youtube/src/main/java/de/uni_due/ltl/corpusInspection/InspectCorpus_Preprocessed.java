package de.uni_due.ltl.corpusInspection;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.YouTubeReader;
import preprocessing.CommentText;
import preprocessing.CommentType;
import preprocessing.Users;

public class InspectCorpus_Preprocessed {

	public static void main(String[] args) throws ResourceInitializationException, AnalysisEngineProcessException {
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin_preprocessed", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");
		
		inspectSentimemts(reader);
	
	}

	private static void inspectSentimemts(CollectionReaderDescription reader) throws AnalysisEngineProcessException {
		for (JCas jcas : new JCasIterable(reader)) {
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				System.out.println(outcome.getCoveredText()+ " "+outcome.getOutcome());
//				System.out.println(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getCoveredText());
				for(StanfordSentimentAnnotation sentiment:JCasUtil.selectCovered(StanfordSentimentAnnotation.class,outcome)){
					System.out.println("++ "+sentiment.getVeryPositive()+"\t"+sentiment.getPositive()+"\t"+sentiment.getNeutral()+"\t"+sentiment.getNegative()+"\t"+sentiment.getVeryNegative()+" --");
				}
			}
		}
	}
	
	
}
