package featureExtractors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;

public class StackedFeatureDFE extends FeatureExtractorResource_ImplBase
implements DocumentFeatureExtractor{

	public static final String PARAM_ID2OUTCOME_FILE_PATH = "id2outcomeFilePath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_FILE_PATH, mandatory = true)
	private String id2outcomeFilePath;

	private Map<String, Integer> id2Outcome_ngram;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		id2Outcome_ngram = getId2OutcomeMap(id2outcomeFilePath);
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
		if(outcome.equals("1,0"))return 1;
		else return 0;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		String docId = DocumentMetaData.get(jcas).getDocumentId();
		Set<Feature> featList = new HashSet<Feature>();
		featList.add(new Feature("stacked_ngram_outcome",  id2Outcome_ngram.get(docId)));
		return featList;
	}

}
