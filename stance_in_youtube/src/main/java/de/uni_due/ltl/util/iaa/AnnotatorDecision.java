package de.uni_due.ltl.util.iaa;

import java.util.List;

import webanno.custom.Debate_Stance_Set1;
import webanno.custom.ExplicitStance;

/**
 * container class that stores all decisions of one annotator with respect to one sentence
 * @author michael
 *
 */
public class AnnotatorDecision {
	private Debate_Stance_Set1 stance;
	private List<ExplicitStance> explicitStances;
	private String annotator;
	private int sentenceId;
	private String document;
	
	
	public AnnotatorDecision(String annotator, int sentenceCount, String document) {
		this.annotator = annotator;
		this.sentenceId = sentenceCount;
		this.document = document;
	}
	
	public Debate_Stance_Set1 getStance() {
		return stance;
	}
	public void setStance(Debate_Stance_Set1 stance) {
		this.stance = stance;
	}
	public List<ExplicitStance> getExplicitStances() {
		return explicitStances;
	}
	public void setExplicitStances(List<ExplicitStance> explicitStances) {
		this.explicitStances = explicitStances;
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
	
}
