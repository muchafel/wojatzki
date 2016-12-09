package de.uni_due.ltl.wordEmbeddings;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.generic.AASTORE;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingHelper;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingLexicon;
import de.uni_due.ltl.util.VectorUtil;


public class WordEmbeddingHelperTest {
	
	@Test
    public void helperTest() throws Exception {
		WordEmbeddingLexicon lexicon = null;
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		List<Double> testVec= new ArrayList<Double>(Arrays.asList(2.0,2.0,2.0,2.0));
		System.out.println(testVec);
		System.out.println(helper.average(testVec, 2));
		Assert.assertEquals(helper.average(testVec, 2), new ArrayList<Double>(Arrays.asList(1.0,1.0,1.0,1.0)));
		
	}
	
	@Test
    public void helperTest2() throws Exception {
		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
		map.put("a", new ArrayList<Double>(Arrays.asList(1.0,1.0,1.0,1.0)));
		map.put("b", new ArrayList<Double>(Arrays.asList(1.0,1.0,1.0,1.0)));
		map.put("c", new ArrayList<Double>(Arrays.asList(1.0,1.0,1.0,1.0)));
		WordEmbeddingLexicon lexicon = new WordEmbeddingLexicon(map);
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		List<String> w1 = new ArrayList<String>(Arrays.asList("a", "b", "c"));
		System.out.println(w1+" avg: "+helper.getAveragedSentenceVector(w1));
		Assert.assertEquals(helper.getAveragedSentenceVector(w1), new ArrayList<Double>(Arrays.asList(1.0,1.0,1.0,1.0)));
	}
	@Test
    public void wordEmbeddingTest() throws Exception {
		WordEmbeddingLexicon lexicon = new WordEmbeddingLexicon("src/main/resources/list/prunedEmbeddings.84B.300d.txt");
		WordEmbeddingHelper helper=new WordEmbeddingHelper(lexicon);
		List<String> w1 = new ArrayList<String>(Arrays.asList("penalty"));
		List<String> w2 = new ArrayList<String>(Arrays.asList("penalty","penalty"));
		List<Double> v1= helper.getAveragedSentenceVector(w1);
		List<Double> v2= helper.getAveragedSentenceVector(w2);
		System.out.println(w1+" "+ w2+ " "+VectorUtil.cosineSimilarity(v1,v2));
		Assert.assertEquals(1.0, VectorUtil.cosineSimilarity(v1,v2),0.0000001);
		
	}
	
	@Test
    public void wordEmbeddingTest2() throws Exception {
		WordEmbeddingLexicon lexicon = new WordEmbeddingLexicon("src/main/resources/list/prunedEmbeddings.84B.300d.txt");
		WordEmbeddingHelper helper = new WordEmbeddingHelper(lexicon);
		String text1 = "I oppose lethal injection . It's too much like a medical procedure . I don't think there should be any question about what's happening : the State is taking revenge . No one's gonna confuse it with a booster shot . State sponsored executions are state sponsored executions , however they're dressed up . That said , I'm willing to accept that occasionally innocents will be executed . That's regrettable , but the doctrine of double effect , in my view , applies to this and other similar cases ( e.g. , unintended but foreseeable killing of civilians in wartime ) . Quick review of the doctrine : The New Catholic Encyclopedia provides four conditions for the application of the principle of double effect : The act itself must be morally good or at least indifferent . The agent may not positively will the bad effect but may permit it . If he could attain the good effect without the bad effect he should do so . The bad effect is sometimes said to be indirectly voluntary . The good effect must flow from the action at least as immediately ( in the order of causality , though not necessarily in the order of time ) as the bad effect . In other words the good effect must be produced directly by the action , not by the bad effect . Otherwise the agent would be using a bad means to a good end , which is never allowed . The good effect must be sufficiently desirable to compensate for the allowing of the bad effect “ ( p . 1021 ) . To be honest , I think execution fails the first condition , or at the very least the fourth . I * don't * think executing murderers is a good effect , or at the very least the increased benefit of killing them rather than locking them away for life doesn't justify killing innocent people . This isn't a direct contradiction of any of your points , but [ evidence good enough to convince SCOTUS was presented that the death penalty in America is * strongly * racially biased]( http://en.wikipedia.org/wiki/McCleskey_v._Kemp ) . Not only are we occasionally killing innocents , but we're deciding who to kill on the basis of skin color .";
		String text2="Advertising is an audio or visual form of marketing communication that employs an openly sponsored, nonpersonal message to promote or sell a product, service or idea . Sponsors of advertising are often businesses who wish to promote their products or services . Advertising is differentiated from public relations in that an advertiser usually pays for and has control over the message .";
		String text3="No one's gonna confuse it with a booster shot . State sponsored executions are state sponsored executions , however they're dressed up . But they might confuse it with euthanasia . Putting an animal out of its misery or getting rid of an inconvenient human being , rather than holding a criminal to account . I don't think there should be any confusion on that question . To be honest , I think execution fails the first condition , or at the very least the fourth . I don't think executing murderers is a good effect , or at the very least the increased benefit of killing them rather than locking them away for life doesn't justify killing innocent people . I was invoking double effect on the narrow question of unintended killing of innocents , not as the justification for the death penalty itself . But I do think there are unique reasons why the death penalty is preferable to life in prison , in some circumstances . A murderer took away something irreplaceable , a human life . I don't see the injustice in exacting a similar penalty . Hannah Arendt was no hanging enthusiast , but she put the case as clearly as possible in * Eichmann in Jerusalem * : Just as you [ Eichmann ] supported and carried out a policy of not wanting to share the earth with the Jewish people and the people of a number of other nations — as though you and your superiors had any right to determine who should and who should not inhabit the world — we find that no one , that is , no member of the human race , can be expected to want to share the earth with you . This is the reason , and the only reason , you must hang . An extreme instance , sure , but one where the abolitionists would also forego the death penalty . I grew up admiring Clarence Darrow and can still recite parts of his speech from memory . So I'm open to changing my mind on this question again . But what pulls me up is this reflection : Nathan Leopold got to see the sun every day . He got to work and read books and even get married after being released on parole . He got to make something of his life . And the little boy who he bludgeoned to death never did . Loeb was stabbed to death by a prison inmate , and I take no satisfaction from that . He didn't die for his crimes ; he died because he upset another murderer . This isn't a direct contradiction of any of your points , but evidence good enough to convince SCOTUS was presented that the death penalty in America is strongly racially biased . Not only are we occasionally killing innocents , but we're deciding who to kill on the basis of skin color . I don't support racial bias in executions and think a defendant ought to be able to present that argument on appeal ( and be taken seriously if the evidence warrants it ) .	Death Penalty should be done by the electric chair";
		String text4="Sun love one !";

		List<String> w1 = new ArrayList<String>(Arrays.asList(text1.split(" ")));
		List<String> w2 = new ArrayList<String>(Arrays.asList(text2.split(" ")));
		List<String> w3 = new ArrayList<String>(Arrays.asList(text3.split(" ")));
		List<String> w4 = new ArrayList<String>(Arrays.asList(text4.split(" ")));
		
		List<Double> v1= helper.getAveragedSentenceVector(w1);
		List<Double> v2= helper.getAveragedSentenceVector(w2);
		List<Double> v3= helper.getAveragedSentenceVector(w3);
		List<Double> v4= helper.getAveragedSentenceVector(w4);
		System.out.println("t1 + t2 "+VectorUtil.cosineSimilarity(v1,v2));
		System.out.println("t1 + t3 "+VectorUtil.cosineSimilarity(v1,v3));
		System.out.println("t2 + t3 "+VectorUtil.cosineSimilarity(v2,v3));
		System.out.println("t2 + t4 "+VectorUtil.cosineSimilarity(v2,v4));
		System.out.println("t1 + t4 "+VectorUtil.cosineSimilarity(v1,v4));
//		Assert.assertEquals(1.0, VectorUtil.cosineSimilarity(v1,v2),0.0000001);
		
	}
	
}
