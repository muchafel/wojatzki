package de.uni.due.ltl.interactiveStance.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.zip.ZipEightByteInteger;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.fitting.GaussianFitter;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.optimization.fitting.HarmonicFitter;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.fitting.WeightedObservedPoint;
import org.apache.commons.math3.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.mysql.cj.api.x.Result;

import de.uni.due.ltl.interactiveStance.util.PlotUtil;
import nl.peterbloem.powerlaws.Continuous;

/**
 * Wrapper for the generated stance lexicons provides reading-from-text methods
 * and getter functionalities
 * 
 * @author michael
 *
 */
public class StanceLexicon {

	private Map<String, Float> lexicon;
	private boolean sorted = false;

	public StanceLexicon(String path) {
		lexicon = generateLexicon(path);
	}

	public StanceLexicon(Map<String, Float> lexicon) {
		this.lexicon = lexicon;
	}

	/**
	 * loads a serialized lexicon generates the lexicon according to the
	 * specified resource
	 */
	private HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
				// escape colon/ tokens thta conatin colon
				if (currentString.split(":").length > 2)
					continue;
				String[] entry = currentString.split(":");
				lexicon.put(entry[0], Float.valueOf(entry[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}

	/**
	 * returns 0 if there is no entry in the lexicon else returns stance value
	 */
	public float getStancePolarity(String word) {
		float value = 0.0f;
		if (lexicon.containsKey(word)) {
			value = lexicon.get(word);
		}
		return value;
	}

	/**
	 * returns positive bound - n percent
	 * 
	 * @param i
	 * @return
	 */
	public float getNthPositivePercent(int i) {
		lexicon = sort();
		// keyset size -1 (as it is not length)
		int lastIndex = (lexicon.keySet().size() - 1);
		float bound = this.lexicon.get((this.lexicon.keySet().toArray())[lastIndex]);
		float result = bound - (bound * i / 100);
		// System.out.println(i + "th positive item " +
		// (this.lexicon.keySet().toArray())[i]);
		return result;
	}

	/**
	 * returns negative bound - n percent
	 * 
	 * @param i
	 * @return
	 */
	public float getNthNegativePercent(int i) {
		lexicon = sort();
		float bound = this.lexicon.get((this.lexicon.keySet().toArray())[0]);
		float result = bound - (bound * i / 100);
		return result;
	}

	public float getNthPositive(int i) {
		lexicon = sort();
		// keyset size -1 (as it is not length)
		i = (lexicon.keySet().size() - 1) - i;
		// System.out.println(i + "th positive item " +
		// (this.lexicon.keySet().toArray())[i]);
		return this.lexicon.get((this.lexicon.keySet().toArray())[i]);

	}

	public float getNthNegative(int i) {
		lexicon = sort();
		// System.out.println(i+"th negative item
		// "+(this.lexicon.keySet().toArray())[i]);
		return this.lexicon.get((this.lexicon.keySet().toArray())[i]);

	}

	public Set<String> getKeys() {
		return sort().keySet();
	}

	public void plotChart(String target) throws IOException {
		lexicon = sort();
		XYSeries series = new XYSeries(target);
		int i = 0;
		for (String key : lexicon.keySet()) {
			series.add(lexicon.keySet().size() - i, lexicon.get(key));
			i++;
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart xylineChart = ChartFactory.createXYLineChart("dice distribution", "", "Score", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		int width = 1280; /* Width of the image */
		int height = 960; /* Height of the image */
		File XYChart = new File("src/main/resources/plots/" + target + ".jpeg");
		ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);

	}

	public void plotChartAndThreshold(String target, int upperPercentage, int lower) throws IOException {
		lexicon = sort();
		XYSeries observedSeries = new XYSeries(target);
	
		int i = 0;
		for (String key : lexicon.keySet()) {
			observedSeries.add(lexicon.keySet().size() - i, lexicon.get(key));
			i++;
		}
		List<Double> dataPositive= new ArrayList<>();
		List<Double> dataNegative= new ArrayList<>();
		
		//reverse order and data for the 
		int j = 0;
        List<String> keys = new ArrayList<String>(lexicon.keySet());
        Collections.reverse(keys);
        for(String key : keys){
        	if((double) (lexicon.get(key))>0){
        		dataPositive.add((double) (lexicon.get(key)));
        	}
        	if((double) (lexicon.get(key))<0){
        		dataNegative.add(-(double) (lexicon.get(key)));
        	}
			j++;
        }		
		
		System.out.println("fit");
		
		//fit the function, transform to a Zipf distribution
		Continuous distributionPositive = Continuous.fit(dataPositive).fit();
		ZipfDistribution zipfPositive= new ZipfDistribution(keys.size(), distributionPositive.exponent());
		Continuous distributionNegative = Continuous.fit(dataNegative).fit();
		ZipfDistribution zipfNegative= new ZipfDistribution(keys.size(), distributionNegative.exponent());
		
		double upperBoundFixed= getNthPositivePercent(upperPercentage);
		double lowerBoundFixed= getNthNegativePercent(lower);
		double zipfUpperBound= getCumulatedPercentageBound(zipfPositive, 0.95);
		// IMPORTANT we have to change the sign of the cut of as zipf is only defined in the positive space 
		double zipfLowerBound= -getCumulatedPercentageBound(zipfNegative, 0.95);
		
		PlotUtil.plotDistributionChart(upperBoundFixed,lowerBoundFixed, zipfLowerBound, zipfUpperBound, zipfPositive, zipfNegative, observedSeries,new File("src/main/resources/plots/" + target + ".jpeg"),keys);
	}

	/**
	 * search for a quantile in the zipf distribution that splits according to the specified threshold
	 * returns the corresponding probability then
	 * TODO  use exact values? double zipfUpperBound= 1/Math.pow(zipfPositive.getNumericalMean(), zipfPositive.getExponent());
	 * @param zipfD
	 * @param percentage
	 * @return
	 */
	public double getCumulatedPercentageBound(ZipfDistribution zipfD, double percentage) {
		int counter=zipfD.inverseCumulativeProbability(percentage);
		double result= zipfD.probability(counter);
		System.out.println("cut off at position "+counter+ " prob "+result);
		return result;
	}

	public String prettyPrint() {
		lexicon = sort();
		StringBuilder sb = new StringBuilder();
		for (String key : lexicon.keySet()) {
			sb.append(key + " : " + lexicon.get(key) + "\n");
		}
		return sb.toString();
	}

	/**
	 * sorts the vector by value (weird java 8 style)
	 * 
	 * @param tempVector
	 * @return
	 */
	private Map<String, Float> sort() {
		if (sorted) {
			return this.lexicon;
		}
		this.sorted = true;
		Map<String, Float> sortedMap = lexicon.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return sortedMap;
	}

}
