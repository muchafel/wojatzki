package de.uni_due.ltl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.unidue.ltl.toobee.twitterUtils.RawJsonTweetReaderFIFO;

public class SearchTwitterCollection {

	public static void main(String[] args) throws ResourceInitializationException, IOException {
		
		Config config= new Config();

		String[] keywordsArray = config.getHashtags();
		System.out.println("Search for "+Arrays.deepToString(keywordsArray)+ " in "+ config.getTweetFolder() + " and write it to "+config.getTargetFile());
		
		String location=config.getTweetFolder();
		File targetFile= new File(config.getTargetFile());
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(RawJsonTweetReaderFIFO.class,
				RawJsonTweetReaderFIFO.PARAM_SOURCE_LOCATION, location, RawJsonTweetReaderFIFO.PARAM_PATTERNS, "*/*.gz");
		
		for (JCas jcas : new JCasIterable(reader)) {
			String text=jcas.getDocumentText().toLowerCase();
			if(hashtagContainedInTweet(text,keywordsArray)){
				System.out.println(text);
				FileUtils.write(targetFile, text+"\n", "UTF-8",true);
			}
		}

	}

	/**
	 * returns true if the tweet contains the hashtag specified in the config
	 * TODO: use only hashtags at the end?
	 * @param lowerCaseText
	 * @param keywordsArray
	 * @return
	 */
	private static boolean hashtagContainedInTweet(String lowerCaseText, String[] keywordsArray) {
		for(String hashtag:keywordsArray){
			if(lowerCaseText.contains(hashtag))return true;
		}
		return false;
	}

}
