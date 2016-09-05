package annotators.stackedAnnotators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.JCasId;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.ml.ModelSerialization_ImplBase;
import org.dkpro.tc.core.ml.TCMachineLearningAdapter;
import org.dkpro.tc.core.util.SaveModelUtils;
import org.dkpro.tc.evaluation.Id2Outcome;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import groovyjarjarantlr.collections.impl.Vector;
import predictedTypes.ClassifiedSubTarget;

public class Stacked_SubTargetClassificationId2Outcome extends JCasAnnotator_ImplBase{
	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList("secularism", "Same-sexmarriage",
			"religious_freedom", "Conservative_Movement", "Freethinking", "Islam", "No_evidence_for_religion", 
			"Supernatural_Power_Being", "Life_after_death", "Christianity"
//			,"USA"
			));
	
	private Map<String, Map<String,String>> subTargetToModel;
//	private TCMachineLearningAdapter mlAdapter;
	private String learningMode = Constants.LM_SINGLE_LABEL;
	private String featureMode = Constants.FM_DOCUMENT;
	 public static final String PARAM_TC_MODEL_LOCATION = "tcModel";
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		subTargetToModel = new HashMap<String, Map<String,String>>();
		// System.out.println(tcModelLocations);
		for (String subTarget : subTargets) {
			File modelFile=new File("src/main/resources/models/id2Outcome/"+subTarget+".txt");
			try {
				subTargetToModel.put(subTarget, getId2OutcomeMapping(modelFile));
			} catch (Exception e) {
				new ResourceInitializationException(e);
			}
		}
		System.out.println("stacked models loaded");
	}
	
	
	private Map<String, String> getId2OutcomeMapping(File modelFile) throws Exception {
		Map<String, String> id2Outcome= new HashMap<String,String>();
		try (BufferedReader br = new BufferedReader(new FileReader(modelFile))) {
			String line = null;
			
			Map<String,String> vectorToLabel=null;
			while (( line= br.readLine()) != null) {
				
				if(line.startsWith("#labels")){
					List<String> labels=Id2Outcome.getLabels(line);
					Map<String,Integer> mapping=Id2Outcome.classNamesToMapping(labels);
					vectorToLabel=getVectorToLabel(mapping);
				}
				if(!line.startsWith("#")){
					String prediction=line.split(";")[0];
					String id=prediction.split("=")[0];
					String outCome=null;
					outCome=vectorToLabel.get((prediction.split("=")[1]));
					id2Outcome.put(id, outCome);
				}
			}
		} 
		return id2Outcome;
	}

	
	private Map<String, String> getVectorToLabel(Map<String, Integer> mapping) throws Exception {
		Map<String, String> result= new HashMap<>();
		//handle two class classification
		if(mapping.keySet().size()==2){
			for(String label: mapping.keySet()){
				if(mapping.get(label)==0){
					result.put("1,0", label);
				}else if(mapping.get(label)==1){
					result.put("0,1", label);
				}
			}
		} 
		//handle three class classification
		else if(mapping.keySet().size()==3){
			for(String label: mapping.keySet()){
				if(mapping.get(label)==0){
					result.put("1,0,0", label);
				}else if(mapping.get(label)==1){
					result.put("0,1,0", label);
				}else if(mapping.get(label)==2){
					result.put("0,0,1", label);
				}
			}
		}
		//else throw exception
		else{
			throw new Exception("label index size is not in the range 2-3");
		}
		return result;
	}


	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (String subTarget : subTargetToModel.keySet()) {
			Map<String, String> id2Outcome=subTargetToModel.get(subTarget);
			String id= String.valueOf(JCasUtil.selectSingle(jcas, JCasId.class).getId());
			
			// annotate the outcomes
			ClassifiedSubTarget annotation = new ClassifiedSubTarget(jcas);
			
			// FIXME weak and dangerous heuristic (is only necessary due to faulty id2outcome report
			if(id2Outcome.get(id) == null || id2Outcome.get(id).equals("null")){
				annotation.setClassificationOutcome("NONE");
			}else{
				annotation.setClassificationOutcome(id2Outcome.get(id));
			}
			annotation.setSubTarget(subTarget);
			annotation.setBegin(0);
			annotation.setEnd(jcas.getDocumentText().length());
			annotation.addToIndexes();
			System.out.println(JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId()+ " "+subTarget+" "+annotation.getClassificationOutcome());

		}
	}
}
