package de.uni_due.ltl.simpleClassifications;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import de.uni_due.ltl.util.Id2OutcomeUtil;

public class LDA_Inferred_DFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String PARAM_USE_SET1 = "useTargetSet1ForClassification";
	@ConfigurationParameter(name = PARAM_USE_SET1, mandatory = true)
	private boolean useSet1;
	
	public static final String PARAM_USE_SET2 = "useTargetSet1ForClassification";
	@ConfigurationParameter(name = PARAM_USE_SET2, mandatory = true)
	private boolean useSet2;
	
	Map<String, List<Double>> set1id2Inference;
	Map<String, List<Double>> set2id2Inference;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			if (useSet1) {
				set1id2Inference = getidToInferenceMap("src/main/resources/topicModellingData/inferenceReddit.txt");
			}

			if (useSet2) {
				set2id2Inference = getidToInferenceMap("src/main/resources/topicModellingData/inferenceIdebate.txt");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	private Map<String, List<Double>> getidToInferenceMap(String pathname) throws IOException {
		Map<String, List<Double>> result= new HashMap<>();
		
		for(String line: FileUtils.readLines(new File(pathname))){
			List<Double> values= new ArrayList<>();
			String id= line.split("\t")[0];
			String inferences= line.split("\t")[1];
			for(String inf: inferences.split(" ")){
				values.add(Double.parseDouble(inf));
			}
			result.put(id,values);
		}
		return result;
	}

/**
 * TODO: extend to more than one top prediction
 * @param values
 * @return
 */
	private int getTopIndex(List<Double> values) {
		double maxV=0;
		int maxI=0;
		for(double d: values){
			if(d>maxV){
				maxV=d;
				maxI=values.indexOf(d);
			}
		}
		return maxI;
	}


	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		int i=getPositionInCas(target.getAddress(),jcas);
			String id=JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId()+"_"+String.valueOf(i);
			i++;
			if (useSet1) {
				int j=0;
				int top= getTopIndex(set1id2Inference.get(id));
				for(double value:set1id2Inference.get(id)){
					if(j==top){
						featList.add(new Feature("lda_inference_set1_Pred_"+j, 1));
					}else{
						featList.add(new Feature("lda_inference_set1_Pred_"+j, 0));
					}
					j++;
				}
//				for(double value:set1id2Inference.get(id)){
//					featList.add(new Feature("lda_inference_set1_"+String.valueOf(j), value));
//					j++;
//				}
			}

			if (useSet2) {
				int j=0;
				int top= getTopIndex(set1id2Inference.get(id));
				for(double value:set2id2Inference.get(id)){
					if(j==top){
						featList.add(new Feature("lda_inference_set2_Pred_"+j, 1));
					}else{
						featList.add(new Feature("lda_inference_set2_Pred_"+j, 0));
					}
					j++;
				}
//				for(double value:set2id2Inference.get(id)){
//					featList.add(new Feature("lda_inference_set2_"+String.valueOf(j), value));
//					j++;
//				}
			}
		return featList;
	}


	private int getPositionInCas(int address, JCas jcas) throws TextClassificationException  {
		int i=0;
		for(TextClassificationTarget unit: JCasUtil.select(jcas, TextClassificationTarget.class)){
			if(unit.getAddress()==address){
				return i;
			}
			i++;
		}
		throw new TextClassificationException(address+" not in jcas");
	}

}
