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

public class CreateDL_Data {

	public static void main(String[] args) throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_curated/bin_preprocessed", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");
		AnalysisEngine engine= getPreprocessingEngine();
		
		createDLDataSets(reader,engine);
		
	}

	
	private static void createDLDataSets(CollectionReaderDescription reader, AnalysisEngine engine) throws IOException, AnalysisEngineProcessException {
		for (JCas jcas : new JCasIterable(reader)) {
			DocumentMetaData metaData = DocumentMetaData.get(jcas);
			String id = metaData.getDocumentId();
			File folder= new File("src/main/resources/dl_data/"+id);
			int i=0;
			int sentences=0;
			for (JCas jcas_inner : new JCasIterable(reader)) {
				engine.process(jcas_inner);
				DocumentMetaData metaData_inner = DocumentMetaData.get(jcas_inner);
				String id_inner = metaData_inner.getDocumentId();
				String trainOrTest="";
				if(id_inner.equals(id)){
					trainOrTest="test";
				}else{
					trainOrTest="train";
				}
				for (TextClassificationOutcome outcome : JCasUtil.select(jcas_inner, TextClassificationOutcome.class)) {
					if(outcome.getOutcome().equals("NONE")){
						FileUtils.write(new File(folder+"/"+trainOrTest+"/none.txt"),String.valueOf(i)+"_"+ String.valueOf(sentences)+"\t" +getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next())+"\n",true);
					}
					if(outcome.getOutcome().equals("FAVOR")){
						FileUtils.write(new File(folder+"/"+trainOrTest+"/favor.txt"),String.valueOf(i)+"_"+ String.valueOf(sentences)+"\t"+ getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next())+"\n",true);
					}
					if(outcome.getOutcome().equals("AGAINST")){
						FileUtils.write(new File(folder+"/"+trainOrTest+"/against.txt"), String.valueOf(i)+"_"+ String.valueOf(sentences)+"\t"+getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next())+"\n",true);
					}
					sentences++;
				}
				i++;
			}
			System.out.println(i);
		}
		
	}


	private static CharSequence getWhitespaceSpearatedTokens(CommentText commentText) {
		List<String>tokenTexts=new ArrayList<>();
		for(Token t:JCasUtil.selectCovered(Token.class,commentText)){
			tokenTexts.add(t.getCoveredText());
		}
		return StringUtils.join(tokenTexts, " ");
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
