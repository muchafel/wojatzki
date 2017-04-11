package de.uni_due.ltl.catalanStanceDetection.cv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class ClassifierProbabilities extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String PARAM_LSTM_CERTAINTY_FILE = "lstmCertainityPath";
	@ConfigurationParameter(name = PARAM_LSTM_CERTAINTY_FILE, mandatory = true)
	private String lstmCertainityPath;
	
	public static final String PARAM_SVM_CERTAINTY_FILE = "svmCertainityPath";
	@ConfigurationParameter(name = PARAM_SVM_CERTAINTY_FILE, mandatory = true)
	private String svmCertainityPath;
	
	private Map<String,Double[]> id2Probabilities_lstm;
//	private Map<String,Double[]> id2Probabilities_svm;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
		System.out.println(lstmCertainityPath);
		
		
		id2Probabilities_lstm=getId2Probabilities(lstmCertainityPath);
//		id2Probabilities_svm=getId2Probabilities(svmCertainityPath);
		
		return true;
	}
	
	private Map<String, Double[]> getId2Probabilities(String path) throws ResourceInitializationException {
		Map<String,Double[]> id2Probabilities = new HashMap<String, Double[]>();
		System.out.println(path);
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					id2Probabilities.put(id, getProbabilityArray(prediction.split("=")[1]));
				}
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		
		return id2Probabilities;
	}

	private Double[] getProbabilityArray(String string) {
		System.out.println(string);
		string.replaceAll("\\[\\s+", "");
		string = string.replace("[ ", "");
		string = string.replace("[  ", "");
//		string = string.replace("[   ", "");
		string = string.replace("]", "");
		String[] probs_string= string.split("\\s+");
		Double[] probs = new Double[probs_string.length];
		int i=0;
		System.out.println(string);
		for(String probability : probs_string){
			if(probability.isEmpty())continue;
			System.out.println(probability);
			probs[i]=Double.valueOf(probability);
			i++;
		}
		return probs;
	}

	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget target) throws TextClassificationException {
		String docId = JCasUtil.selectSingle(view, DocumentMetaData.class).getDocumentId();
		Set<Feature> featList = new HashSet<Feature>();
//		System.out.println(docId);
//		System.out.println(JCasUtil.select(view, TextClassificationOutcome.class).iterator().next().getOutcome());
//		System.out.println(docId+ " against: "+ id2Probabilities_lstm.get(docId)[0]+ " favor: "+ id2Probabilities_lstm.get(docId)[1]+ " neutral"+id2Probabilities_lstm.get(docId)[2]);
		double max= getmax(id2Probabilities_lstm.get(docId));
		featList.add(new Feature("lstm_probability_against",  id2Probabilities_lstm.get(docId)[0]));
		featList.add(new Feature("lstm_probability_favor ",  id2Probabilities_lstm.get(docId)[1]));
		featList.add(new Feature("lstm_probability_none ",  id2Probabilities_lstm.get(docId)[2]));
		featList.add(new Feature("lstm_probability_certainity",  max));
		return featList;
	}

	private double getmax(Double[] doubles) {
		double max = 0;
		for (Double prob : doubles) {
			if(prob == null)continue;
			if (prob > max) {
				max = prob;
			}
		}
		return max;
	}


}
