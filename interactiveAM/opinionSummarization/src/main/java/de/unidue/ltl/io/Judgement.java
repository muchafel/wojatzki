package de.unidue.ltl.io;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class Judgement {

	private int id;
	private int faultyBWSTuple=0;
	
	public Judgement(int id) {
		this.id = id;
		this.agree= new ArrayList<>();
		this.disagree= new ArrayList<>();
		this.best= "";
		this.worst= "";
	}
	private List<String> agree;
	private List<String> disagree;
	private String best;
	private String worst;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getAgree() {
		return agree;
	}
	public void addAgree(String agree) {
		this.agree.add(agree);
	}
	public List<String> getDisagree() {
		return disagree;
	}
	public void addDisAgree(String disagree) {
		this.disagree.add(disagree);
	}
	
	public String getBest() {
		return this.best;
	}
	public void addBest(String best) {
		this.best=best;
	}
	public String getWorst() {
		return this.worst;
	}
	public void addWorst(String worst) {
		this.worst=worst;
	}
	public void incFaultyBWSTuple() {
		this.faultyBWSTuple++;		
	}
	public int getFaultyBWSTuple() {
		return faultyBWSTuple;
	}
}
