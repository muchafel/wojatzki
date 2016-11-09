package de.uni_due.ltl.util.iaa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class ConsolidationHelper {

	private JCas jcas;
	private Sentence sentence;
	private boolean minorityVote=true;
	
	public ConsolidationHelper(JCas jcas, Sentence sentence) {
		this.jcas = jcas;
		this.sentence = sentence;
	}




	private void annotateDebateStance(Debate_Stance_Container debate_Stance_Container, String annotator) {
		    rawTypes.Debate_Stance stanceAnno= new rawTypes.Debate_Stance(jcas,debate_Stance_Container.getBegin(),debate_Stance_Container.getEnd());
			stanceAnno.setAnnotator(annotator);
			stanceAnno.setPolarity(debate_Stance_Container.getPolarity());
			stanceAnno.addToIndexes();
	}

	public void consolidateStance(List<AnnotatorDecision> decisions) {
		annotateDebateStance(decisions.get(0).getStance(),decisions.get(0).getAnnotator());
		annotateDebateStance(decisions.get(1).getStance(),decisions.get(1).getAnnotator());
		annotateDebateStance(decisions.get(2).getStance(),decisions.get(2).getAnnotator());
		String majorityPolarity= majority(decisions.get(0).getStance().getPolarity(),decisions.get(1).getStance().getPolarity(),decisions.get(2).getStance().getPolarity());
		curated.Debate_Stance curatedStance= new curated.Debate_Stance(jcas,sentence.getBegin(),sentence.getEnd());
		curatedStance.setPolarity(majorityPolarity);
		curatedStance.addToIndexes();
	}




	private String majority(String polarity, String polarity2, String polarity3) {
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		fd.inc(polarity);
		fd.inc(polarity2);
		fd.inc(polarity3);
		if(fd.getCount(fd.getSampleWithMaxFreq())==1){
			return "NONE";
		}
		return fd.getSampleWithMaxFreq();
	}

	public void consolidateInsults(List<AnnotatorDecision> decisions, ArrayList<String> insultTags) {
		for(String insultTag: insultTags){
			if(decisions.get(0).getInsults() != null){
				annotateInsult(decisions.get(0).getInsults(),decisions.get(0).getAnnotator(),insultTag);
			}
			if(decisions.get(1).getInsults() != null){
				annotateInsult(decisions.get(1).getInsults(),decisions.get(1).getAnnotator(),insultTag);
			}
		    if(decisions.get(2).getInsults() != null){
		    	annotateInsult(decisions.get(2).getInsults(),decisions.get(2).getAnnotator(),insultTag);
		    }
			String majorityInsult= majorityInsult(decisions.get(0).getInsults(),decisions.get(1).getInsults(),decisions.get(2).getInsults(),insultTag);
			if(!majorityInsult.equals("NONE")){
				curated.Direct_Insult insultAnno=new curated.Direct_Insult(jcas,sentence.getBegin(),sentence.getEnd());
				insultAnno.setInsultTarget(majorityInsult);
				insultAnno.addToIndexes();
			}
		}
		
	}


	private String majorityInsult(List<InsultContainer> insults0, List<InsultContainer> insults1,
			List<InsultContainer> insults2, String insultTag) {
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		if (insults0 != null) {
			for (InsultContainer insult0 : insults0) {
				if (insult0.getTag().equals(insultTag)) {
					fd.inc(insultTag);
				}
			}
		}
		if (insults1 != null) {
			for (InsultContainer insult1 : insults1) {
				if (insult1.getTag().equals(insultTag)) {
					fd.inc(insultTag);
				}
			}
		}
		if (insults2 != null) {
			for (InsultContainer insult2 : insults2) {
				if (insult2.getTag().equals(insultTag)) {
					fd.inc(insultTag);
				}
			}
		}
		if(minorityVote){
			if(fd.getCount("FAVOR")>=1 && fd.getCount("AGAINST")==0){
				return "FAVOR";
			}
			if(fd.getCount("AGAINST")>=1 && fd.getCount("FAVOR")==0){
				return "AGAINST";
			}
		}
		if (fd.getCount(fd.getSampleWithMaxFreq()) <= 1) {
			return "NONE";
		}
		return fd.getSampleWithMaxFreq();
	}




	private void annotateInsult(List<InsultContainer> insults, String annotator, String insultTag) {
		for(InsultContainer insult: insults){
			if(insult.getTag().equals(insultTag)){
				rawTypes.Direct_Insult insultAnno= new rawTypes.Direct_Insult(jcas,insult.getBegin(),insult.getEnd());
				insultAnno.setAnnotator(annotator);
				insultAnno.setInsultTarget(insultTag);
				insultAnno.addToIndexes();
			}
		}
		
	}




	public void consolidateReferences(List<AnnotatorDecision> decisions, ArrayList<String> nonTextualReference) {
		for(String referenceTyp: nonTextualReference){
			if(decisions.get(0).getReferences()!=null){
				annotateReference(decisions.get(0).getReferences(),decisions.get(0).getAnnotator(),referenceTyp);
			}
			if(decisions.get(1).getReferences()!=null){
				annotateReference(decisions.get(1).getReferences(),decisions.get(1).getAnnotator(),referenceTyp);
			}
			if(decisions.get(2).getReferences()!=null){
				annotateReference(decisions.get(2).getReferences(),decisions.get(2).getAnnotator(),referenceTyp);
			}
			String majorityReference= majorityReference(decisions.get(0).getReferences(),decisions.get(1).getReferences(),decisions.get(2).getReferences(),referenceTyp);
			if(!majorityReference.equals("NONE")){
				curated.NonTextual_Content insultAnno=new curated.NonTextual_Content(jcas,sentence.getBegin(),sentence.getEnd());
				insultAnno.setSource(majorityReference);
				insultAnno.addToIndexes();
			}
		}
		
	}


	private void annotateReference(List<ReferenceContainer> references, String annotator, String referenceTyp) {
		for(ReferenceContainer insult: references){
			if(insult.getSource() != null){
				if(insult.getSource().equals(referenceTyp)){
					rawTypes.NonTextual_Content insultAnno= new rawTypes.NonTextual_Content(jcas,insult.getBegin(),insult.getEnd());
					insultAnno.setAnnotator(annotator);
					insultAnno.setSource(referenceTyp);
					insultAnno.addToIndexes();
				}
			}
		}
	}




	private String majorityReference(List<ReferenceContainer> references0, List<ReferenceContainer> references1,
			List<ReferenceContainer> references2, String referenceTyp) {
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		if(references0!=null){
			for(ReferenceContainer ref0:references0){
				if(ref0.getSource().equals(referenceTyp)){
					fd.inc(referenceTyp);
				}
			}
		}
		if(references1!=null){
			for(ReferenceContainer ref1:references1){
				if(ref1.getSource().equals(referenceTyp)){
					fd.inc(referenceTyp);
				}
			}
		}
		if(references2!=null){
			for(ReferenceContainer ref2:references2){
				if(ref2.getSource() != null){
					if(ref2.getSource().equals(referenceTyp)){
						fd.inc(referenceTyp);
					}
				}
			}
		}
		if(minorityVote){
			if(fd.getCount("FAVOR")>=1 && fd.getCount("AGAINST")==0){
				return "FAVOR";
			}
			if(fd.getCount("AGAINST")>=1 && fd.getCount("FAVOR")==0){
				return "AGAINST";
			}
		}
		if(fd.getCount(fd.getSampleWithMaxFreq())<=1){
			return "NONE";
		}
		return fd.getSampleWithMaxFreq();
	}




	public void consolidateExplicitTargetsSet1(List<AnnotatorDecision> decisions, ArrayList<String> targets_Set1) {
		for(String target: targets_Set1){
			if(decisions.get(0).getExplicitStances_Set1()!=null){
				annotateTarget1(decisions.get(0).getExplicitStances_Set1(),decisions.get(0).getAnnotator(),target);
			}
			if(decisions.get(1).getExplicitStances_Set1()!=null){
				annotateTarget1(decisions.get(1).getExplicitStances_Set1(),decisions.get(1).getAnnotator(),target);
			}
			if(decisions.get(2).getExplicitStances_Set1()!=null){
				annotateTarget1(decisions.get(2).getExplicitStances_Set1(),decisions.get(2).getAnnotator(),target);
			}
			String majorityPolarity= majorityPolarity(decisions.get(0).getExplicitStances_Set1(),decisions.get(1).getExplicitStances_Set1(),decisions.get(2).getExplicitStances_Set1(),target);
			curated.Explicit_Stance_Set1 explicitTarget_1=new curated.Explicit_Stance_Set1(jcas,sentence.getBegin(),sentence.getEnd());
			explicitTarget_1.setTarget(target);
			explicitTarget_1.setPolarity(majorityPolarity);
			explicitTarget_1.addToIndexes();
		}
	}




	private String majorityPolarity(List<Explicit_Stance_Container> explicitStances_Set0,
			List<Explicit_Stance_Container> explicitStances_Set1,
			List<Explicit_Stance_Container> explicitStances_Set2, String target) {
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		
		if(explicitStances_Set0!=null){
			for(Explicit_Stance_Container target1:explicitStances_Set0){
				if(target1.getTarget().equals(target)){
					fd.inc(target1.getPolarity());
				}
			}
		}
		
		if(explicitStances_Set1!=null){
			for(Explicit_Stance_Container target1:explicitStances_Set1){
				if(target1.getTarget().equals(target)){
					fd.inc(target1.getPolarity());
				}
			}
		}
		if(explicitStances_Set2!=null){
			for(Explicit_Stance_Container target2:explicitStances_Set2){
				if(target2.getTarget().equals(target)){
					fd.inc(target2.getPolarity());
				}
			}
		}
		if(minorityVote){
			if(fd.getCount("FAVOR")>=1 && fd.getCount("AGAINST")==0){
				return "FAVOR";
			}
			if(fd.getCount("AGAINST")>=1 && fd.getCount("FAVOR")==0){
				return "AGAINST";
			}
		}
		if(fd.getCount(fd.getSampleWithMaxFreq())<=1){
			return "NONE";
		}
		return fd.getSampleWithMaxFreq();
	}




	private void annotateTarget1(List<Explicit_Stance_Container> explicitStances_Set1, String annotator,
			String target) {
		for(Explicit_Stance_Container explicitStance: explicitStances_Set1){
			if(explicitStance.getTarget().equals(target)){
				rawTypes.Explicit_Stance_Set1 explicitStanceAnno= new rawTypes.Explicit_Stance_Set1(jcas,explicitStance.getBegin(),explicitStance.getEnd());
				explicitStanceAnno.setAnnotator(annotator);
				explicitStanceAnno.setTarget(target);
				explicitStanceAnno.setPolarity(explicitStance.getPolarity());
				explicitStanceAnno.addToIndexes();
			}
		}
		
	}




	public void consolidateExplicitTargetsSet2(List<AnnotatorDecision> decisions, ArrayList<String> targets_Set2) {
		for(String target: targets_Set2){
			if(decisions.get(0).getExplicitStances_Set2() != null){
				annotateTarget2(decisions.get(0).getExplicitStances_Set2(),decisions.get(0).getAnnotator(),target);
			}
			if(decisions.get(1).getExplicitStances_Set2() != null){
				annotateTarget2(decisions.get(1).getExplicitStances_Set2(),decisions.get(1).getAnnotator(),target);
			}
			if(decisions.get(2).getExplicitStances_Set2() != null){
				annotateTarget2(decisions.get(2).getExplicitStances_Set2(),decisions.get(2).getAnnotator(),target);
			}
			String majorityPolarity= majorityPolarity(decisions.get(0).getExplicitStances_Set2(),decisions.get(1).getExplicitStances_Set2(),decisions.get(2).getExplicitStances_Set2(),target);
			curated.Explicit_Stance_Set2 explicitStance_2=new curated.Explicit_Stance_Set2(jcas,sentence.getBegin(),sentence.getEnd());
			explicitStance_2.setTarget(target);
			explicitStance_2.setPolarity(majorityPolarity);
			explicitStance_2.addToIndexes();
		}
		
	}




	private void annotateTarget2(List<Explicit_Stance_Container> explicitStances_Set2, String annotator,
			String target) {
		for(Explicit_Stance_Container explicitStance: explicitStances_Set2){
			if(explicitStance.getTarget().equals(target)){
				rawTypes.Explicit_Stance_Set2 explicitStanceAnno= new rawTypes.Explicit_Stance_Set2(jcas,explicitStance.getBegin(),explicitStance.getEnd());
				explicitStanceAnno.setAnnotator(annotator);
				explicitStanceAnno.setTarget(target);
				explicitStanceAnno.setPolarity(explicitStance.getPolarity());
				explicitStanceAnno.addToIndexes();
			}
		}
		
	}




	
	

}
