package assertionRegression.featureExtractors;

import java.io.File;
import java.io.IOException;
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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class NRCSentiment extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	public static final String PARAM_PREDICTION_FILE_CLASSES= "sentimentClassFile";
	@ConfigurationParameter(name = PARAM_PREDICTION_FILE_CLASSES, mandatory = true)
	private File predictedClassesFile;
	private Map<String,String> id2Class;
	
	public static final String PARAM_PREDICTION_FILE_SCORES= "sentimentScoreFile";
	@ConfigurationParameter(name = PARAM_PREDICTION_FILE_SCORES, mandatory = true)
	private File predictedScoreFile;
	private Map<String,String> id2Score;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
		try {
			System.out.println("read "+predictedClassesFile.getAbsolutePath());
			id2Class=getMapping(predictedClassesFile);
			System.out.println("read "+predictedScoreFile.getAbsolutePath());
			id2Score=getMapping(predictedScoreFile);
		} catch (IOException e) {
			new ResourceInitializationException(e);
		}
		
		return true;
	}
	
	private Map<String, String> getMapping(File file) throws IOException {
		Map<String,String> result=new HashMap();
		for(String line: FileUtils.readLines(file, "UTF-8")) {
			String[] parts= line.split("\t");
			result.put(parts[0], parts[2]);
		}
		return result;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget arg1) throws TextClassificationException {
		
		Set<Feature> featList = new HashSet<Feature>();
		
		//TODO: adapt to unit mode
		DocumentMetaData md= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
		String predictedClass=id2Class.get(md.getDocumentId());
		if(predictedClass.equals("neutral")) {
			featList.add(new Feature("PredcitedSentimentClass",0));
		}else if(predictedClass.equals("negative")){
			featList.add(new Feature("PredcitedSentimentClass",-1));
		}else if(predictedClass.equals("positive")){
			featList.add(new Feature("PredcitedSentimentClass",1));
		}
		
		String scoreStrings=id2Score.get(md.getDocumentId());
		String[] scores=scoreStrings.split(" ");
		featList.add(new Feature("sentiment_positive_score",Double.valueOf(scores[0].split(":")[1])));
		featList.add(new Feature("sentiment_negative_score",Double.valueOf(scores[1].split(":")[1])));
		featList.add(new Feature("sentiment_neutral_score",Double.valueOf(scores[2].split(":")[1])));
		
		
		return featList;
	}

}
