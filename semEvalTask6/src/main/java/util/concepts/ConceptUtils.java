package util.concepts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import util.PreprocessingPipeline;
import util.SimilarityHelper;

public class ConceptUtils {
	
	
	
	private static final Map<String, List<String>> strictlyPolarConcepts = new HashMap<String, List<String>>();
    static {
    	strictlyPolarConcepts.put("HillaryClinton", new ArrayList<String>(
    		    Arrays.asList("money", "#tcot", "#whyimnotvotingforhillary")));
    	strictlyPolarConcepts.put("Atheism", new ArrayList<String>(
    		    Arrays.asList( "#peace", "#islam", "#freethinker","pray")));
    	strictlyPolarConcepts.put("FeministMovement", new ArrayList<String>(
    		    Arrays.asList("#spankafeminist", "@cooimemegirl")));
    	strictlyPolarConcepts.put("LegalizationofAbortion", new ArrayList<String>(
    		    Arrays.asList("womb", "#prolifegen", "marriage","#prolifeyouth","#alllivesmatter", "matter", "murder")));
    	strictlyPolarConcepts.put("ClimateChangeisaRealConcern", new ArrayList<String>(
    		    Arrays.asList( "atmosphere", "#sustainability", "water", "#mission", "future","#environment")));
    }
    
    private static final Map<String,String> strictlyPolarConceptPolarity = new HashMap<String,String>();
    static {
    	strictlyPolarConceptPolarity.put("money","AGAINST");
    	strictlyPolarConceptPolarity.put("#tcot","AGAINST");
    	strictlyPolarConceptPolarity.put("#whyimnotvotingforhillary","AGAINST");
//    	strictlyPolarConceptPolarity.put("love","AGAINST");
//    	strictlyPolarConceptPolarity.put("heart","AGAINST");
    	strictlyPolarConceptPolarity.put("#peace","FAVOR");
    	strictlyPolarConceptPolarity.put("#islam","AGAINST");
    	strictlyPolarConceptPolarity.put("#freethinker","FAVOR");
    	strictlyPolarConceptPolarity.put("pray","AGAINST");
    	strictlyPolarConceptPolarity.put("#spankafeminist","AGAINST");
    	strictlyPolarConceptPolarity.put("womb","AGAINST");
    	strictlyPolarConceptPolarity.put("#prolifegen","AGAINST");
    	strictlyPolarConceptPolarity.put("marriage","AGAINST");
    	strictlyPolarConceptPolarity.put("#prolifeyouth","AGAINST");
    	strictlyPolarConceptPolarity.put("matter","AGAINST");
    	strictlyPolarConceptPolarity.put("murder","AGAINST");
    	strictlyPolarConceptPolarity.put("atmosphere","FAVOR");
    	strictlyPolarConceptPolarity.put("water","FAVOR");
    	strictlyPolarConceptPolarity.put("#mission","FAVOR");
    	strictlyPolarConceptPolarity.put("future","FAVOR");
    	strictlyPolarConceptPolarity.put("#environment","FAVOR");
    }
    
    public static List<String> getStrictlyPolarConcepts(String target){
    	return strictlyPolarConcepts.get(target);
    }
    
    public static String getStrictlyPolarConceptPolarity(String concept){
    	return strictlyPolarConceptPolarity.get(concept);
    }
	
	public static Set<String> getConcepts(File folder, int topN,List<String> stopwords) throws IOException, UIMAException {
		// create favor and against fds foreach target
		FrequencyDistribution<String> fd = new FrequencyDistribution<String>();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, folder,
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
		
		Iterator<JCas> it= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos()).iterator();
		// iterate over all CASes that have been stored by the meta collector
		while (it.hasNext()) {
			JCas jcas = it.next();
			
			Collection<String> relevantTokens = ConceptUtils.getNouns(jcas,stopwords);

				// if tweet is against add tokens to favor frequency
				// distribution
				String outcome= JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome();
				if (outcome.equals("FAVOR")||outcome.equals("AGAINST")) {
					fd.incAll(relevantTokens);
				}
		}
		for(String concept: fd.getMostFrequentSamples(topN))System.out.println(concept+ " "+ fd.getCount(concept));
		
		return ConceptUtils.normalizeConcepts(fd.getMostFrequentSamples(topN),fd);
	}

	public static Set<String> normalizeConcepts(List<String> mostFrequentSamples, FrequencyDistribution<String> fd) {
		Set<String> normalized= new HashSet<String>(mostFrequentSamples);
		for(String conceptA : mostFrequentSamples){
			for(String conceptB : mostFrequentSamples){
				if(SimilarityHelper.wordsAreSimilar(conceptA, conceptB)){
					normalized.remove(conceptB);
					normalized.remove(conceptA);
					if(fd.getCount(conceptB)>fd.getCount(conceptA))normalized.add(conceptB);
					else normalized.add(conceptA);
				}
			}
		}
		return normalized;
	}

	public static Collection<String> getNouns(JCas jcas, List<String> stopwords) {
		Collection<String> nouns = new HashSet<String>();
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			// filter stopwords and punctuations
			String lowerCase = t.getCoveredText().toLowerCase();
			if (!stopwords.contains(lowerCase) && (t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NPS"))) {
				nouns.add(lowerCase);
			}
		}
		return nouns;
	}
}
