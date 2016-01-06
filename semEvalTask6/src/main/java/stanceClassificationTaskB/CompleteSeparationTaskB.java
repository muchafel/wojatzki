package stanceClassificationTaskB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import util.PreprocessingPipeline;

public class CompleteSeparationTaskB {

	public static void main(String[] args) throws IOException, ResourceInitializationException {

		int topI = 50;
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		ArrayList<String> targets = new ArrayList<String>(Arrays.asList("Atheism", "ClimateChangeisaRealConcern",
				"HillaryClinton", "FeministMovement", "LegalizationofAbortion"));
		Map<String, FrequencyDistribution<String>> targetToFd = new HashMap<>();

		CollectionReaderDescription trumpReader = CollectionReaderFactory.createReaderDescription(
				TaskATweetReader.class, TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskB/",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
//		CollectionReaderDescription testReader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
//				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/Atheism/",
//				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
//				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

//		// create target fds
		for (String target : targets) {

			CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
					TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/" + target + "/",
					TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
					TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

			Iterator<JCas> it = SimplePipeline
					.iteratePipeline(reader, PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos())
					.iterator();
			FrequencyDistribution<String> fd = new FrequencyDistribution<>();
			int numberOfTweets = 0;
			int numberOfNouns = 0;
			while (it.hasNext()) {
				JCas jcas = it.next();
				numberOfTweets++;
				for (Token t : JCasUtil.select(jcas, Token.class)) {
					numberOfNouns++;
					String lowerCase = t.getCoveredText().toLowerCase();
					if (t.getPos().getPosValue().equals("NN") || t.getPos().getPosValue().equals("NP")
							|| t.getPos().getPosValue().equals("NPS") || t.getPos().getPosValue().equals("NNS")) {
						fd.inc(lowerCase);
					}
				}
			}
			System.out.println(fd.getMostFrequentSamples(topI));
			targetToFd.put(target, fd);
		}
		
//		List<JCas> allTrump= getTrumpJcases(trumpReader);
		FrequencyDistribution<String> trumpFd= new FrequencyDistribution<String>();
	
		//create trump fd
		Iterator<JCas> it2= SimplePipeline.iteratePipeline(trumpReader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
		int noOfTrumpJcases=0;
		while (it2.hasNext()) {
			JCas trumpJcas = it2.next();
			noOfTrumpJcases++;
			for(Token t : JCasUtil.select(trumpJcas, Token.class)){
				String lowerCase= t.getCoveredText().toLowerCase();
				if(t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NNS")){
					trumpFd.inc(t.getCoveredText().toLowerCase());
				}
			}
		}
		
		System.out.println("all trump tweets: "+noOfTrumpJcases);
		System.out.println(trumpFd.getMostFrequentSamples(topI));
		
		//filter NONEs
		List<String> stanceCasIds= new ArrayList<String>();
//		List<JCas> stanceJcases= new ArrayList<JCas>();
		int numberOfNones=0;
		Iterator<JCas> it3= SimplePipeline.iteratePipeline(trumpReader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
		while (it3.hasNext()) {
			JCas jcas = it3.next();
			boolean stance=false;
			for (Token t : JCasUtil.select(jcas, Token.class)) {
				if (trumpFd.getMostFrequentSamples(topI).contains(t.getCoveredText().toLowerCase())) {
//					System.out.println(t.getCoveredText());
					stanceCasIds.add(DocumentMetaData.get(jcas).getDocumentId());
//					stanceJcases.add(jcas);
					stance=true;
					break;
				}
			}
			if(!stance){
//				System.out.println("no stance");
				numberOfNones++;
			}
		}
		System.out.println("stance tweets: "+stanceCasIds.size()+ " / none: "+ numberOfNones);
		//result fd
		FrequencyDistribution<String> combined = new FrequencyDistribution<>();
		//add filtered jcases
		combined.addSample("NONEs", numberOfNones);
		Iterator<JCas> it4= SimplePipeline.iteratePipeline(trumpReader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
		while (it4.hasNext()) {
			JCas jcas = it4.next();
			if(stanceCasIds.contains(DocumentMetaData.get(jcas).getDocumentId())){
				String result="";
				for (String target : targetToFd.keySet()) {
					for (Token t : JCasUtil.select(jcas, Token.class)) {
						if (targetToFd.get(target).getMostFrequentSamples(topI).contains(t.getCoveredText().toLowerCase())) {
							result+=" "+target;
							break;
						}
					}
				}
				combined.inc(result);
			}
		}
		for(String combi: combined.getKeys()){
			System.out.println(combi+" "+combined.getCount(combi));
		}
		
		// total: 68.513
		// stance tweets: 62620 / none: 5893
		// HillaryClinton top 50 nouns words in trump: 28.696
		// LegalizationofAbortion top 50 nouns in trump: 10.882
		// Atheism top 50 nouns in trump: 11.478
		// ClimateChangeisaRealConcern top 50 nouns in trump: 10.857
		// FeministMovement top 50 nouns in trump: 13.526
		
//		 FeministMovement LegalizationofAbortion 800
//		 HillaryClinton FeministMovement 1371
//		 Atheism ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 14
//		 HillaryClinton 14237
//		 HillaryClinton Atheism ClimateChangeisaRealConcern FeministMovement 852
//		 Atheism ClimateChangeisaRealConcern FeministMovement 30
//		 HillaryClinton Atheism FeministMovement LegalizationofAbortion 402
//		 ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 38
//		NONEs 5893
//		 Atheism FeministMovement 320
//		 Atheism 860
//		 HillaryClinton LegalizationofAbortion 543
//		 HillaryClinton Atheism ClimateChangeisaRealConcern 133
//		 FeministMovement 1424
//		 HillaryClinton ClimateChangeisaRealConcern FeministMovement 735
//		 ClimateChangeisaRealConcern LegalizationofAbortion 110
//		 Atheism LegalizationofAbortion 368
//		 HillaryClinton ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 96
//		 Atheism ClimateChangeisaRealConcern 210
//		 HillaryClinton Atheism ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 5581
//		 HillaryClinton Atheism ClimateChangeisaRealConcern LegalizationofAbortion 76
//		 ClimateChangeisaRealConcern FeministMovement 81
//		 HillaryClinton Atheism LegalizationofAbortion 663
//		 Atheism FeministMovement LegalizationofAbortion 93
//		 HillaryClinton ClimateChangeisaRealConcern LegalizationofAbortion 116
//		 HillaryClinton Atheism FeministMovement 262
//		 ClimateChangeisaRealConcern 1424
//		 HillaryClinton Atheism 1083
//		 HillaryClinton ClimateChangeisaRealConcern 842
//		 28486
//		 LegalizationofAbortion 422
//		 HillaryClinton FeministMovement LegalizationofAbortion 887
//		 Atheism ClimateChangeisaRealConcern LegalizationofAbortion 61
		
	}

	private static List<JCas> getTrumpJcases(CollectionReaderDescription reader) {
		List<JCas> result= new ArrayList<>();
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos());
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		int i=0;
		for (JCas read : new JCasIterable(reader)) {
			JCas jcas = null;
			try {
				jcas = engine.newJCas();
				jcas.setDocumentText( read.getDocumentText() );
				DocumentMetaData.create( jcas );
				DocumentMetaData.get( jcas ).setLanguage( "en" );
				System.out.println(i);
				i++;
				engine.process( jcas );
			} catch (ResourceInitializationException | AnalysisEngineProcessException e) {
				e.printStackTrace();
			}
			result.add(jcas);
		}
		return result;
	}
}
