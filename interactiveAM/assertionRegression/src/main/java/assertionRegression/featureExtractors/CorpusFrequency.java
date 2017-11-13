package assertionRegression.featureExtractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.dkpro.tc.api.type.TextClassificationTarget;

import assertionRegression.annotationTypes.Issue;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.sentiment.type.StanfordSentimentAnnotation;

public class CorpusFrequency  extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	
	private Map<String, FrequencyDistribution<String>> freqs;
	
	public static final String PARAM_CORPUS_FOLDER= "corpus";
	@ConfigurationParameter(name = PARAM_CORPUS_FOLDER, mandatory = true)
	private String folder;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
		freqs= new HashMap<>();
		System.out.println(folder);
		for(File file: new File(folder).listFiles()){
			try {
				freqs.put(file.getName().split("_")[0], getFreq(file));
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}
		System.out.println("freqs loaded");
		return true;
	}

	private FrequencyDistribution<String> getFreq(File file) throws IOException {
		FrequencyDistribution<String> result= new FrequencyDistribution<>();
		for(String line: FileUtils.readLines(file,"UTF-8")){
			
			result.incAll(new ArrayList<>(Arrays.asList(line.split(" "))));
		}
		return result;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		Issue issue= JCasUtil.selectSingle(jcas,Issue.class);
//		System.out.println(issue.getIssue());
		FrequencyDistribution<String> freq= freqs.get(issue.getIssue());
//		System.out.println(freq.getKeys().size());
		int i=0;
		double score= 0.0;
		double min= 1;
		double max= 0.0;
		
		for(Token t: JCasUtil.select(jcas, Token.class)){
			if(freq.getKeys().contains(t.getCoveredText().toLowerCase())){
				double currentScore= freq.getCount(t.getCoveredText().toLowerCase())/(double)freq.getN();
				score+=currentScore;
				if(currentScore<min){
					min=currentScore;
				}
				if(currentScore>max){
					max=currentScore;
				}
				i++;
			}
		}
			
		if(i==0){
			featList.add(new Feature("MEAN_FREQ",0));
			featList.add(new Feature("MAX_FREQ",0));
			featList.add(new Feature("MIN_FREQ",0));
		}else{
			featList.add(new Feature("MAX_FREQ",max));
			featList.add(new Feature("MIN_FREQ",min));
			featList.add(new Feature("MEAN_FREQ",score/(double)i));
		}
		
		
		return featList;
	}

}
