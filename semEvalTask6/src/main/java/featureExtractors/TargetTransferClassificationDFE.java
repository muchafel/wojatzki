package featureExtractors;

import java.io.BufferedReader;
import java.io.File;
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
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import util.SimilarityHelper;

public class TargetTransferClassificationDFE extends FeatureExtractorResource_ImplBase
implements DocumentFeatureExtractor{

	public static final String PARAM_ID2OUTCOMETARGET_FOLDER = "id2outcomeTargetFolder";
	@ConfigurationParameter(name = PARAM_ID2OUTCOMETARGET_FOLDER, mandatory = true)
	private String id2outcomeFolder;

	private Map<String,Map<String, Integer>> id2Outcome_perTargets;
	private Map<String,List<String>> targetToTopINouns;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map aAdditionalParams) throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		id2Outcome_perTargets = getId2OutcomeMap(id2outcomeFolder);
		try {
			targetToTopINouns= readTargetWiseTopINouns();
//			System.out.println("Top 60 Nouns for Targets:");
//			for(String key: targetToTopINouns.keySet())System.out.println(key+" "+targetToTopINouns.get(key));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private Map<String, List<String>> readTargetWiseTopINouns() throws FileNotFoundException, IOException {
		Map<String, List<String>> targetToTopINouns= new HashMap<String, List<String>>();
		File folder= new File("src/main/resources/top60Nouns"); 
		for(File target: folder.listFiles()){
			targetToTopINouns.put(target.getName(), getTop60Nouns(target));
		}
		return targetToTopINouns;
	}

	private List<String> getTop60Nouns(File target) throws FileNotFoundException, IOException {
		List<String> top60Nouns = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(target.getAbsolutePath()))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				for(String noun: line.split(","))top60Nouns.add(noun.replace(" ", ""));
			}
		}
		return top60Nouns;
	}

	private Map<String, Map<String, Integer>> getId2OutcomeMap(String path) {
		Map<String, Map<String, Integer>> result= new HashMap<String, Map<String, Integer>>();
		
		File folder = new File(path);
//		System.out.println(path);
		File[] listOfFiles = folder.listFiles();
//		for(File f: listOfFiles){
//			System.out.println(f.getAbsolutePath());
//		}
		for(File f: listOfFiles){
			Map<String, Integer> id2Outcome= new HashMap<String,Integer>();
			try (BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()+"/id2Outcome.txt"))) {
				String line = null;
				while (( line= br.readLine()) != null) {
					if(!line.startsWith("#")){
						String prediction=line.split(";")[0];
						String id=prediction.split("=")[0];
						int outCome=getOutcomeFromPrediction(prediction.split("=")[1].split(";")[0]);
						id2Outcome.put(id, outCome);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result.put(f.getName(), id2Outcome);
		}
		return result;
	}

	private int getOutcomeFromPrediction(String outcome) {
		if(outcome.equals("1"))return 1;
		else return -1;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		String docId = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
		Set<Feature> featList = new HashSet<Feature>();
		for(String target : id2Outcome_perTargets.keySet()){
//			System.out.println(target);
			if(id2Outcome_perTargets.get(target).get(docId) != null && jcasContainsTopINoun(jcas,target)){
//				System.out.println(docId+ " "+ id2Outcome_perTargets.get(target).get(docId));
				featList.add(new Feature("stacked_outcome_"+target,  id2Outcome_perTargets.get(target).get(docId)));
			}else{
//				System.out.println(docId+ " 0");
				featList.add(new Feature("stacked_outcome_"+target,  0));
			}
		}
//		System.out.println(docId+ " "+ id2Outcome_ngram.get(docId));
//		featList.add(new Feature("stacked_ngram_outcome",  id2Outcome_ngram.get(docId)));
		return featList;
	}

	private boolean jcasContainsTopINoun(JCas jcas, String target) {
		List<String> topINouns = targetToTopINouns.get(target);
		for(Token t: JCasUtil.select(jcas, Token.class)){
			if(t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NNS")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NPS")){
				if(topINouns.contains(t.getCoveredText().toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}

}
