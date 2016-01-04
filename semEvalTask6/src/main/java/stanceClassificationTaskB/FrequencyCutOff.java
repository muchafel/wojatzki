package stanceClassificationTaskB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
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

public class FrequencyCutOff {

	public static void main(String[] args) throws ResourceInitializationException, IOException {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
//		String target="Atheism";
//		String target="ClimateChangeisaRealConcern";
//		String target="HillaryClinton";
//		String target="FeministMovement";
		String target="LegalizationofAbortion";
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir+"/semevalTask6/targets/"+target+"/",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
//		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
//				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir+"/semevalTask6/tweetsTaskB/",
//				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
//				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
		
		Iterator<JCas> it= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		List<String> stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");

		int numberOfTweets=0;
		int numberOfNouns=0;
		while (it.hasNext()) {
			JCas jcas = it.next();
			numberOfTweets++;
			for(Token t : JCasUtil.select(jcas, Token.class)){
				numberOfNouns++;
				String lowerCase= t.getCoveredText().toLowerCase();
//				if(!stopwords.contains(lowerCase) && (!t.getPos().getPosValue().equals(".")||!t.getPos().getPosValue().equals(","))){
//					fd.inc(t.getCoveredText().toLowerCase());
//				}
				if(t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NNS")){
					fd.inc(t.getCoveredText().toLowerCase());
				}
			}
		}
		System.out.println(fd.getMostFrequentSamples(100));
//		for(String noun: fd.getKeys()){
//			//>0,5% of number of tokens
//			if(fd.getCount(noun)>fd.getN()/20)System.out.println(noun+ " "+ fd.getCount(noun));
//		}
//		for(String noun: fd.getKeys()){
//			//>5% of unigrams
//			if(fd.getCount(noun)>fd.getKeys().size()*0.05)System.out.println(noun+ " "+ fd.getCount(noun));
//		}
		
		
		Map<Integer,Integer> distributionTotal= new TreeMap<>();
		Map<Integer,Integer> distributionNone= new TreeMap<>();
		for(int i=1;i<20;i++){
			int numberOfTweetsContainingTopi=0;
			int numberOfNoneTweetsContainingTopi=0;
			Iterator<JCas> it2= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
			while (it2.hasNext()) {
				JCas jcas = it2.next();
				numberOfTweets++;
				for(Token t : JCasUtil.select(jcas, Token.class)){
					if(JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("NONE")){
						if(fd.getMostFrequentSamples(10*i).contains(t.getCoveredText().toLowerCase())){
							numberOfNoneTweetsContainingTopi++;
							numberOfTweetsContainingTopi++;
							break;
						}
					}else{
						if(fd.getMostFrequentSamples(10*i).contains(t.getCoveredText().toLowerCase())){
							numberOfTweetsContainingTopi++;
							break;
						}
					}
				}
			}
			distributionTotal.put(i*10, numberOfTweetsContainingTopi);
			distributionNone.put(i*10, numberOfNoneTweetsContainingTopi);
		}
		for(int i: distributionTotal.keySet()){
			System.out.println("top "+i+" tokens contained in :"+distributionTotal.get(i)+ " containing nones: "+distributionNone.get(i));
		}
	
		
		
//		Iterator<JCas> it2= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
//		int numberOfTweetsWithTop100=0;
//		while (it2.hasNext()) {
//			JCas jcas = it2.next();
//			numberOfTweets++;
//			for(Token t : JCasUtil.select(jcas, Token.class)){
//				if(fd.getMostFrequentSamples(100).contains(t.getCoveredText().toLowerCase())){
//					numberOfTweetsWithTop100++;
//					break;
//				}
//			}
//		}
//		System.out.println(fd.getMostFrequentSamples(100));
//		System.out.println("numberOfTweets "+numberOfTweets); //137.026
//		System.out.println("numberOfNouns"+numberOfNouns); //1.175.661
//		System.out.println("numberOfTweetsWithTop100 "+numberOfTweetsWithTop100); //64.247
	}
	/**
	 * read in a file and return a list of strings
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected static List<String> init(String path) throws IOException {
		List<String> stopwords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
		}
		return stopwords;
	}
}
