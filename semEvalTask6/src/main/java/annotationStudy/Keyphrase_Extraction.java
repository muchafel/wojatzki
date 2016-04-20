package annotationStudy;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import io.TaskATweetReader;
import lexicons.StanceLexicon;
import util.CollocationMeasureHelper;
import util.PreprocessingPipeline;

public class Keyphrase_Extraction {

//	private static final String semEvalTarget="LegalizationofAbortion";
//	private static final String semEvalTarget="FeministMovement";
	private static final String semEvalTarget = "Atheism";

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();

		
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, "/Users/michael/ArgumentMiningCoprora/semEval2016/SemEval2016-Task6-testdata/xmls/tweets/taskA_targetWise/"+semEvalTarget+"/",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
//		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
//				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/"+semEvalTarget+"/",
//				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
//				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
		FrequencyDistribution<String> fd_chunks= new FrequencyDistribution<String>();
		FrequencyDistribution<String> fd_concepts= new FrequencyDistribution<String>();
		FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
		FrequencyDistribution<String> against = new FrequencyDistribution<String>();
		
		Iterator<JCas> it= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingChunkingFunctionalStanceAnno()).iterator();
		while (it.hasNext()) {
			JCas jcas = it.next();
//			System.out.println("-------------------------");
//			System.out.println(jcas.getDocumentText()+ " ");
			for(Chunk chunk: JCasUtil.select(jcas, Chunk.class)){
				if(chunk.getChunkValue().equals("NP")){
					fd_chunks.inc(chunk.getCoveredText());
//					System.out.println(chunk.getCoveredText()+ " "+ chunk.getChunkValue());
				}
			}
			
//			Collection<Token> relevantTokens = getRelevantTokens(jcas);
//			if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
//					.equals("FAVOR")) {
////				System.out.println("FAVOR");
//				favor = incAll(favor, relevantTokens);
//			}
//
//			// if tweet is against add tokens to favor frequency
//			// distribution
//			if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
//					.equals("AGAINST")) {
//				against = incAll(against, relevantTokens);
//			}
			
			
			for(Token t: JCasUtil.select(jcas, Token.class)){
//				System.out.println(chunk.getCoveredText()+ " "+ chunk.getChunkValue());
				if(t.getPos().getClass().getSimpleName().equals("NN") || t.getPos().getClass().getSimpleName().equals("NP")){
//					System.out.println(t.getCoveredText()+ " "+ t.getPos().getClass().getSimpleName());
					fd_concepts.inc(t.getLemma().getValue());
				}
			}
		}
		for(String chunk : fd_chunks.getMostFrequentSamples(250)){
			System.out.println(chunk+ "\t"+fd_chunks.getCount(chunk));
		}
		System.out.println("-------------");
		for(String t : fd_concepts.getMostFrequentSamples(150)){
			System.out.println(t+ "\t"+fd_concepts.getCount(t));
		}
//		Map<String, Float> lexicon = createLexiconMap(favor, against);
//		showLexicon(semEvalTarget, sortMap(lexicon));
	}

	private static Map<String, Float> createLexiconMap(FrequencyDistribution<String> favor,
			FrequencyDistribution<String> against) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		// add all cands. dublicates will be removed because map stores just
		// unique entries
		candidates.addAll(favor.getKeys());
		candidates.addAll(against.getKeys());

		CollocationMeasureHelper helper = new CollocationMeasureHelper(favor, against);

		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfDice(word));
		}

		return lexcicon;
	}

	private static FrequencyDistribution<String> incAll(FrequencyDistribution<String> favor,
			Collection<Token> relevantTokens) {
		for(Token t: relevantTokens){
//			favor.inc(t.getLemma().getValue());
			favor.inc(t.getCoveredText());
		}
		return favor;
	}

	private static Collection<Token> getRelevantTokens(JCas jcas) {
		Collection<Token> nouns= new ArrayList<Token>();
		for(Token t: JCasUtil.select(jcas, Token.class)){
			if(t.getPos().getClass().getSimpleName().equals("NN")){
				nouns.add(t);
			}
		}
		return nouns;
	}

	
	/**
	 * writes to the specified resource in the form Token:Stance
	 * 
	 * @param target
	 * @param lexcicon
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	private static void showLexicon(String target, Map<String, Float> lexicon) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		for (String key : lexicon.keySet()) {
			System.out.println(key + ":" + lexicon.get(key));
			writeToFile(key+"\t"+lexicon.get(key),DkproContext.getContext().getWorkspace().getAbsolutePath()+"/semevalTask6/prestudy_annotation/"+target);
		}
	}
	private static void writeToFile(String toPrint, String path) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+"/dice_scored_noun_caseSensitive.csv",true), "UTF-8"));
		try {
			out.write(toPrint+System.lineSeparator());
		} finally {
			out.close();
		}
		
	}

	private static Map<String, Float> sortMap(Map<String, Float> unsortMap) {
		// Convert Map to List
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
		for (Iterator<Map.Entry<String, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
