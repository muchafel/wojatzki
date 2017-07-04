package de.uni.due.ltl.interactiveStance.coverage;

import java.util.HashMap;

public class CoverageResult {

	private double coverageSelection;
	private HashMap<String, Double> targetToCoverage;
	
	
	public CoverageResult(double coverageSelection, HashMap<String, Double> targetToCoverage) {
		this.coverageSelection=coverageSelection;
		this.targetToCoverage=targetToCoverage;
	}


	public double getCoverageSelection() {
		return coverageSelection;
	}


	public void setCoverageSelection(double coverageSelection) {
		this.coverageSelection = coverageSelection;
	}


	public HashMap<String, Double> getTargetToCoverage() {
		return targetToCoverage;
	}


	public void setTargetToCoverage(HashMap<String, Double> targetToCoverage) {
		this.targetToCoverage = targetToCoverage;
	}

}
