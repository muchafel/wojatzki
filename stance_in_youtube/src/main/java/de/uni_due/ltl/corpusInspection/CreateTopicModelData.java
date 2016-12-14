package de.uni_due.ltl.corpusInspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.JCasId;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import io.YouTubeReader;
import preprocessing.CommentText;

public class CreateTopicModelData {
	public static void main(String[] args) throws ResourceInitializationException, IOException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_minorityVote/bin_preprocessed", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");
		
		for (JCas jcas : new JCasIterable(reader)) {
			System.out.println(jcas.getDocumentText().length());
			int i=0;
			for(TextClassificationTarget unit: JCasUtil.select(jcas, TextClassificationTarget.class)){
				String tokens=getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,unit).iterator().next());
				String id=JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId()+"_"+String.valueOf(i);
				i++;
				FileUtils.write(new File("src/main/resources/topicModellingData/data_mapped.txt"), id+"\t"+tokens+"\n",true);
				FileUtils.write(new File("src/main/resources/topicModellingData/data.txt"),tokens+"\n",true);
			}
		}
		
	
	}
	private static String getWhitespaceSpearatedTokens(CommentText commentText) {
		List<String>tokenTexts=new ArrayList<>();
		for(Token t:JCasUtil.selectCovered(Token.class,commentText)){
			tokenTexts.add(t.getCoveredText());
		}
		return StringUtils.join(tokenTexts, " ");
	}
}
