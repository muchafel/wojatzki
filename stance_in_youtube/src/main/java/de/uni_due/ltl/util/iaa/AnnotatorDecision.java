package de.uni_due.ltl.util.iaa;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;

import webanno.custom.Debate_Stance;
import webanno.custom.Direct_Insult;
import webanno.custom.Explicit_Stance_Set1;
import webanno.custom.Explicit_Stance_Set2;
import webanno.custom.NonTextual_Content;

/**
 * container class that stores all decisions of one annotator with respect to one sentence
 * @author michael
 *
 */
public class AnnotatorDecision {
	private Debate_Stance_Container stance;
	private List<InsultContainer> insults;
	private List<ReferenceContainer> references;
	private List<Explicit_Stance_Container> explicitStances_set1;
	private List<Explicit_Stance_Container> explicitStances_set2;
	private String annotator;
	private int sentenceId;
	private String document;
	private String text;
	
	
	public AnnotatorDecision(String annotator, int sentenceCount, String document, String text) {
		this.annotator = annotator;
		this.sentenceId = sentenceCount;
		this.document = document;
		this.text=text;
	}
	
	public Debate_Stance_Container getStance() {
		return stance;
	}
	public void setStance(Debate_Stance stance, JCas jcas) {
		this.stance = new Debate_Stance_Container(stance.getPolarity(),stance.getBegin(),stance.getEnd(),stance.getCoveredText());
	}
	
	public List<Explicit_Stance_Container> getExplicitStances_Set1() {
		return explicitStances_set1;
	}
	public List<Explicit_Stance_Container> getExplicitStances_Set2() {
		return explicitStances_set2;
	}
	public void setExplicitStances_Set1(List<Explicit_Stance_Set1> explicitStances) {
		this.explicitStances_set1= new ArrayList<>();
		for(Explicit_Stance_Set1 anno: explicitStances){
			Explicit_Stance_Container container= new Explicit_Stance_Container(anno.getPolarity(), anno.getBegin(), anno.getEnd(), anno.getCoveredText(), anno.getTarget());
			this.explicitStances_set1.add(container);
		}
	}
	
	public void setExplicitStances_set2(List<Explicit_Stance_Set2> explicitStances_Set2) {
		this.explicitStances_set2= new ArrayList<>();
		for(Explicit_Stance_Set2 anno: explicitStances_Set2){
			Explicit_Stance_Container container= new Explicit_Stance_Container(anno.getPolarity(), anno.getBegin(), anno.getEnd(), anno.getCoveredText(), anno.getTarget());
			this.explicitStances_set2.add(container);
		}
	}
	
	public String getAnnotator() {
		return annotator;
	}
	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}
	public int getSentenceId() {
		return sentenceId;
	}
	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<InsultContainer> getInsults() {
		return insults;
	}

	public void setInsults(List<Direct_Insult> insults) {
		this.insults= new ArrayList<>();
		for(Direct_Insult anno: insults){
			InsultContainer container= new InsultContainer(anno.getBegin(), anno.getEnd(), anno.getCoveredText(), anno.getInsultTarget());
			this.insults.add(container);
		}
	}

	public List<ReferenceContainer> getReferences() {
		return references;
	}

	public void setReferences(List<NonTextual_Content> references) throws Exception {
		this.references = new ArrayList<>();
		for(NonTextual_Content anno: references){
			ReferenceContainer container= new ReferenceContainer(anno.getBegin(), anno.getEnd(), anno.getCoveredText(), anno.getSource());
			this.references.add(container);
		}
	}
	
}
