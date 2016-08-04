package featureExtractors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * assigns feature values based on a id2outcome file from a previous experiment (one value for char n-grams; one for word n-grams)
 * @author michael
 *
 */
public class StackedNGramAnnotator_id2outcomeDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	public static final String PARAM_ID2OUTCOME_WORDNGRAM_FILE_PATH = "id2outcomeWordNgramFilePath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_WORDNGRAM_FILE_PATH, mandatory = true)
	private String id2outcomeWordNgramsFilePath;
	
	public static final String PARAM_ID2OUTCOME_CHARNGRAM_FILE_PATH = "id2outcomeCharNgramFilePath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_CHARNGRAM_FILE_PATH, mandatory = true)
	private String id2outcomeCharNgramsFilePath;

	private Map<String, Integer> id2Outcome_word_ngram;
	private Map<String, Integer> id2Outcome_char_ngram;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
//		System.out.println(id2outcomeFilePath);
		id2Outcome_word_ngram = getId2OutcomeMap(id2outcomeWordNgramsFilePath);
		id2Outcome_char_ngram= getId2OutcomeMap(id2outcomeCharNgramsFilePath);
		return true;
	}
	
	
	private Map<String, Integer> getId2OutcomeMap(String path) {
		Map<String, Integer> id2Outcome= new HashMap<String,Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while (( line= br.readLine()) != null) {
				if(!line.startsWith("#")){
					String prediction=line.split(";")[0];
					String id=prediction.split("=")[0];
					int outCome=getOutcomeFromPrediction(prediction.split("=")[1]);
					id2Outcome.put(id, outCome);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id2Outcome;
	}

	private int getOutcomeFromPrediction(String outcome) {
//		System.out.println(outcome);
		if(outcome.equals("1,0,0"))return 1;
		else if(outcome.equals("0,1,0"))return 2;
		else return 0;
	}

	@Override
	public Set<Feature> extract(JCas jcas,TextClassificationTarget target) throws TextClassificationException {
		String docId = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
		Set<Feature> featList = new HashSet<Feature>();
//		System.out.println(docId+ " "+ id2Outcome_word_ngram.get(docId)+" "+ id2Outcome_char_ngram.get(docId));
		featList.add(new Feature("stacked_word_ngram_outcome",  id2Outcome_word_ngram.get(docId)));
		featList.add(new Feature("stacked_char_ngram_outcome",  id2Outcome_char_ngram.get(docId)));
		return featList;
	}

}
