package de.uni_due.ltl.simpleClassifications;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import curated.Direct_Insult;
import curated.Explicit_Stance_Set1;
import curated.Explicit_Stance_Set2;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.uni_due.ltl.util.TargetSets;

public class CuratedStancesAnnotator extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for(Sentence sentence: JCasUtil.select(jcas, Sentence.class)){
			if(JCasUtil.selectCovered(webanno.custom.Debate_Stance.class, sentence).size()>1){
				throw new AnalysisEngineProcessException("more than one debate stance in : "+sentence.getCoveredText(), null);
			}
			System.out.println(sentence.getCoveredText()); 
//			System.out.println(JCasUtil.selectCovered(DocumentMetaData.class, sentence).iterator().next().getDocumentTitle());
			webanno.custom.Debate_Stance debateStance= JCasUtil.selectCovered(webanno.custom.Debate_Stance.class, sentence).iterator().next();
			curated.Debate_Stance curatedDebateStance= new curated.Debate_Stance(jcas, debateStance.getBegin(), debateStance.getEnd());
			if(explicitDebateStance(sentence).equals("NONE")){
				curatedDebateStance.setPolarity("NONE");
			}else{
				curatedDebateStance.setPolarity(debateStance.getPolarity());
			}
			
			curatedDebateStance.addToIndexes();
			
			for(String explicitTarget: TargetSets.targets_Set1){
				webanno.custom.Explicit_Stance_Set1 explicitTargetObject= getPolarityForTarget1(explicitTarget,sentence);
				curated.Explicit_Stance_Set1 explicitStanceSet1= new curated.Explicit_Stance_Set1(jcas);
				
				if(explicitTargetObject != null){
					explicitStanceSet1.setPolarity(explicitTargetObject.getPolarity());
					explicitStanceSet1.setBegin(explicitTargetObject.getBegin());
					explicitStanceSet1.setEnd(explicitTargetObject.getEnd());
				}else{
					explicitStanceSet1.setPolarity("NONE");
					explicitStanceSet1.setBegin(sentence.getBegin());
					explicitStanceSet1.setEnd(sentence.getEnd());
				}
					
				explicitStanceSet1.setTarget(explicitTarget);
				explicitStanceSet1.addToIndexes();
			}
			
			for(String explicitTarget: TargetSets.targets_Set2){
				webanno.custom.Explicit_Stance_Set2 explicitTargetObject= getPolarityForTarget2(explicitTarget,sentence);
				curated.Explicit_Stance_Set2 explicitStanceSet2= new curated.Explicit_Stance_Set2(jcas);
				
				if(explicitTargetObject != null){
					explicitStanceSet2.setPolarity(explicitTargetObject.getPolarity());
					explicitStanceSet2.setBegin(explicitTargetObject.getBegin());
					explicitStanceSet2.setEnd(explicitTargetObject.getEnd());
				}else{
					explicitStanceSet2.setPolarity("NONE");
					explicitStanceSet2.setBegin(sentence.getBegin());
					explicitStanceSet2.setEnd(sentence.getEnd());
				}
					
				explicitStanceSet2.setTarget(explicitTarget);
				explicitStanceSet2.addToIndexes();
			}
			
//			for(webanno.custom.Explicit_Stance_Set1 explicitStance: JCasUtil.selectCovered(webanno.custom.Explicit_Stance_Set1.class, sentence)){
//				curated.Explicit_Stance_Set1 explicitStanceSet1= new curated.Explicit_Stance_Set1(jcas, explicitStance.getBegin(), explicitStance.getEnd());
//				explicitStanceSet1.setPolarity(explicitStance.getPolarity());
//				explicitStanceSet1.setTarget(explicitStance.getTarget());
//				explicitStanceSet1.addToIndexes();
//			}
//			for(webanno.custom.Explicit_Stance_Set2 explicitStance: JCasUtil.selectCovered(webanno.custom.Explicit_Stance_Set2.class, sentence)){
//				curated.Explicit_Stance_Set2 explicitStanceSet2= new curated.Explicit_Stance_Set2(jcas, explicitStance.getBegin(), explicitStance.getEnd());
//				explicitStanceSet2.setPolarity(explicitStance.getPolarity());
//				explicitStanceSet2.setTarget(explicitStance.getTarget());
//				explicitStanceSet2.addToIndexes();
//			}
			for(webanno.custom.Direct_Insult directInsult: JCasUtil.selectCovered(webanno.custom.Direct_Insult.class, sentence)){
				curated.Direct_Insult directInsult1_curated= new curated.Direct_Insult(jcas, directInsult.getBegin(), directInsult.getEnd());
				directInsult1_curated.setInsultTarget(directInsult.getInsultTarget());
				directInsult1_curated.addToIndexes();
			}
			for(webanno.custom.NonTextual_Content nonText: JCasUtil.selectCovered(webanno.custom.NonTextual_Content.class, sentence)){
				curated.NonTextual_Content nonText_curated= new curated.NonTextual_Content(jcas, nonText.getBegin(), nonText.getEnd());
				nonText_curated.setSource(nonText.getSource());
				nonText_curated.addToIndexes();
			}
			
		}
	}

	private String explicitDebateStance(Sentence sentence) {
		for(webanno.custom.Explicit_Stance_Set1 explicitStance: JCasUtil.selectCovered(webanno.custom.Explicit_Stance_Set1.class, sentence)){
			if(explicitStance.getTarget().equals("Death Penalty (Debate)")){
				return explicitStance.getPolarity();
			}
		}
		return "UnPRESENT";
	}

	private webanno.custom.Explicit_Stance_Set2 getPolarityForTarget2(String explicitTarget, Sentence sentence) {
		for(webanno.custom.Explicit_Stance_Set2 explicitStance: JCasUtil.selectCovered(webanno.custom.Explicit_Stance_Set2.class, sentence)){
			if(explicitStance.getTarget().equals(explicitTarget)){
				return explicitStance;
			}
		}
		return null;
	}

	private webanno.custom.Explicit_Stance_Set1 getPolarityForTarget1(String explicitTarget, Sentence sentence) {
		for(webanno.custom.Explicit_Stance_Set1 explicitStance: JCasUtil.selectCovered(webanno.custom.Explicit_Stance_Set1.class, sentence)){
			if(explicitStance.getTarget().equals(explicitTarget)){
				return explicitStance;
			}
		}
		return null;
	}

}
