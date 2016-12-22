package de.uni_due.ltl.simpleClassifications;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.JCasId;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.ltl.util.TargetSets;
import preprocessing.SentenceStance;

public class ExplicitStanceSentencePredictions extends JCasAnnotator_ImplBase{

	protected Map<String, Map<String, Integer>> sentence_SET1_2id2Outcome;
	protected Map<String, Map<String, Integer>> sentence_SET2_2id2Outcome;
	
	public static final String PARAM_ID2OUTCOME_SUBTARGETS_FOLDER_PATH = "id2outcomeExplicitTargetsSentenceFolderPath";
	@ConfigurationParameter(name = PARAM_ID2OUTCOME_SUBTARGETS_FOLDER_PATH, mandatory = true)
	protected String id2outcomeSentenceSubTargetFolderPath;
	private int i=0;
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		
		sentence_SET1_2id2Outcome = new HashMap<String, Map<String, Integer>>();
		for (String target : TargetSets.targets_Set1) {
			System.out.println(id2outcomeSentenceSubTargetFolderPath + "/set1/" + target);
			sentence_SET1_2id2Outcome.put(target,Id2OutcomeUtil.getId2OutcomeMap(id2outcomeSentenceSubTargetFolderPath + "/set1/" + target + ".txt"));
		}

		sentence_SET2_2id2Outcome = new HashMap<String, Map<String, Integer>>();
		for (String target : TargetSets.targets_Set2) {
			System.out.println(id2outcomeSentenceSubTargetFolderPath + "/set2/" + target);
			sentence_SET2_2id2Outcome.put(target,Id2OutcomeUtil.getId2OutcomeMap(id2outcomeSentenceSubTargetFolderPath + "/set2/" + target + ".txt"));
		}
		
	}
	
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		int jcasId=JCasUtil.selectSingle(jcas, JCasId.class).getId();
		for(Sentence sentence: JCasUtil.select(jcas, Sentence.class)){
			System.out.println(String.valueOf(jcasId)+"_"+String.valueOf(i));
			for(String targetSet1: TargetSets.targets_Set1){
				Map<String, Integer> target2Prediction=sentence_SET1_2id2Outcome.get(targetSet1);
				int predictionInt= target2Prediction.get(String.valueOf(jcasId)+"_"+String.valueOf(i));
				SentenceStance sentenceStance= new SentenceStance(jcas, sentence.getBegin(), sentence.getEnd());
				sentenceStance.setPolarity(predictionInt);
				sentenceStance.setTarget(targetSet1);
				sentenceStance.addToIndexes();
			}
			for(String targetSet2: TargetSets.targets_Set2){
				Map<String, Integer> target2Prediction=sentence_SET2_2id2Outcome.get(targetSet2);
				System.out.println(targetSet2);
				int predictionInt= target2Prediction.get(String.valueOf(jcasId)+"_"+String.valueOf(i));
				SentenceStance sentenceStance= new SentenceStance(jcas, sentence.getBegin(), sentence.getEnd());
				sentenceStance.setPolarity(predictionInt);
				sentenceStance.setTarget(targetSet2);
				sentenceStance.addToIndexes();
			}
			i++;
		}
	}

}
