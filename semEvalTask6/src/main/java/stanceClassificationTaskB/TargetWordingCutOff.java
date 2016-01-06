package stanceClassificationTaskB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import io.TaskATweetReader;
import util.PreprocessingPipeline;

public class TargetWordingCutOff {

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
		Iterator<JCas> it2 = SimplePipeline
				.iteratePipeline(trumpReader, PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos())
				.iterator();
		FrequencyDistribution<String> combined = new FrequencyDistribution<>();

		
		while (it2.hasNext()) {
			String result="";
			JCas jcas = it2.next();
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
		for(String combi: combined.getKeys()){
			System.out.println(combi+" "+combined.getCount(combi));
		}
		
		// total: 68.513
		// HillaryClinton top 50 nouns words in trump: 28.696
		// LegalizationofAbortion top 50 nouns in trump: 10.882
		// Atheism top 50 nouns in trump: 11.478
		// ClimateChangeisaRealConcern top 50 nouns in trump: 10.857
		// FeministMovement top 50 nouns in trump: 13.526
		
//		 FeministMovement LegalizationofAbortion 967
//		 HillaryClinton FeministMovement 1389
//		 Atheism ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 18
//		 HillaryClinton 14641
//		 HillaryClinton Atheism ClimateChangeisaRealConcern FeministMovement 884
//		 HillaryClinton Atheism FeministMovement LegalizationofAbortion 430
//		 ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 47
//		 Atheism ClimateChangeisaRealConcern FeministMovement 31
//		 Atheism FeministMovement 331
//		 Atheism 990
//		 HillaryClinton LegalizationofAbortion 576
//		 HillaryClinton Atheism ClimateChangeisaRealConcern 142
//		 FeministMovement 1549
//		 HillaryClinton ClimateChangeisaRealConcern FeministMovement 737
//		 ClimateChangeisaRealConcern LegalizationofAbortion 139
//		 Atheism LegalizationofAbortion 428
//		 HillaryClinton ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 105
//		 Atheism ClimateChangeisaRealConcern 243
//		 HillaryClinton Atheism ClimateChangeisaRealConcern FeministMovement LegalizationofAbortion 5621
//		 HillaryClinton Atheism ClimateChangeisaRealConcern LegalizationofAbortion 79
//		 ClimateChangeisaRealConcern FeministMovement 92
//		 HillaryClinton Atheism LegalizationofAbortion 680
//		 Atheism FeministMovement LegalizationofAbortion 99
//		 HillaryClinton ClimateChangeisaRealConcern LegalizationofAbortion 119
//		 HillaryClinton Atheism FeministMovement 268
//		 ClimateChangeisaRealConcern 1631
//		 HillaryClinton Atheism 1166
//		 HillaryClinton ClimateChangeisaRealConcern 901
//		 32636
//		 LegalizationofAbortion 548
//		 HillaryClinton FeministMovement LegalizationofAbortion 958
//		 Atheism ClimateChangeisaRealConcern LegalizationofAbortion 68
		
	}
}
