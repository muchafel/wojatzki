package de.uni.due.ltl.interactiveStance.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.ZipfDistribution;

import nl.peterbloem.powerlaws.Continuous;

public class ZipfDistributionsContainer {

 private double zipfUpperBound;
 private double zipfLowerBound;
 private ZipfDistribution zipfPositive;
 private ZipfDistribution zipfNegative;
	
	public ZipfDistributionsContainer(StanceLexicon stanceLexicon, double percentil) {
		List<Double> dataPositive= new ArrayList<>();
		List<Double> dataNegative= new ArrayList<>();
		
		//reverse order and data for the 
		int j = 0;
		List<String> keys = new ArrayList<String>(stanceLexicon.getKeys());
        Collections.reverse(keys);
        for(String key : keys){
        	double currentPolarity= stanceLexicon.getStancePolarity(key);
        	if(currentPolarity>0){
        		dataPositive.add(currentPolarity);
        	}
        	if(currentPolarity<0){
        		dataNegative.add(-currentPolarity);
        	}
			j++;
        }		
		
		System.out.println("fit");
		
		//fit the function, transform to a Zipf distribution
		Continuous distributionPositive = Continuous.fit(dataPositive).fit();
		zipfPositive= new ZipfDistribution(keys.size(), distributionPositive.exponent());
		Continuous distributionNegative = Continuous.fit(dataNegative).fit();
		zipfNegative= new ZipfDistribution(keys.size(), distributionNegative.exponent());
		
		zipfUpperBound= stanceLexicon.getCumulatedPercentageBound(zipfPositive, percentil);
		// IMPORTANT we have to change the sign of the cut of as zipf is only defined in the positive space 
		zipfLowerBound= -stanceLexicon.getCumulatedPercentageBound(zipfNegative, percentil);
	}

	
	public double getZipfUpperBound() {
		return zipfUpperBound;
	}

	public void setZipfUpperBound(double zipfUpperBound) {
		this.zipfUpperBound = zipfUpperBound;
	}

	public double getZipfLowerBound() {
		return zipfLowerBound;
	}

	public void setZipfLowerBound(double zipfLowerBound) {
		this.zipfLowerBound = zipfLowerBound;
	}

	public ZipfDistribution getZipfPositive() {
		return zipfPositive;
	}

	public void setZipfPositive(ZipfDistribution zipfPositive) {
		this.zipfPositive = zipfPositive;
	}

	public ZipfDistribution getZipfNegative() {
		return zipfNegative;
	}

	public void setZipfNegative(ZipfDistribution zipfNegative) {
		this.zipfNegative = zipfNegative;
	}

}
