package de.uni.due.ltl.interactiveStance.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.fitting.GaussianFitter;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optimization.fitting.HarmonicFitter;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.fitting.WeightedObservedPoint;
import org.apache.commons.math3.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.mysql.cj.api.x.Result;

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
		return lexicon.keySet();
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
		XYSeries series = new XYSeries(target);
		int i = 0;
		ArrayList<Double> xValues = new ArrayList<>();
		ArrayList<Double> yValues = new ArrayList<>();

		for (String key : lexicon.keySet()) {
			series.add(lexicon.keySet().size() - i, lexicon.get(key));
			i++;
		}
		
		GaussNewtonOptimizer optimizer =new GaussNewtonOptimizer(true);
		LevenbergMarquardtOptimizer opt= new LevenbergMarquardtOptimizer();
		PolynomialFitter fitter = new PolynomialFitter(3,opt);
		
		ArrayList<String> keys = new ArrayList<String>(lexicon.keySet());
		int m = 0;
		int a=0;
		System.out.println(keys.size());
		for(int k=keys.size()-1; k>=0;k--){
//			if(m==a*(keys.size()-1)/22){
//				System.out.println(m);
//				System.out.println(a);
//				if(m==0){
//					fitter.addObservedPoint(new WeightedObservedPoint(1000.0, (double) m, (double) (lexicon.get(keys.get(k)))));
//				}
//				if(lexicon.get(keys.get(k))>0){
					if(m==0){
						fitter.addObservedPoint(new WeightedObservedPoint(1000.0, (double) m, (double) (lexicon.get(keys.get(k)))));
					}else if(m==keys.size()-1){
						fitter.addObservedPoint(new WeightedObservedPoint(1000.0, (double) m, (double) (lexicon.get(keys.get(k)))));
					}
					else{
						fitter.addObservedPoint(new WeightedObservedPoint(1.0, (double) m, (double) (lexicon.get(keys.get(k)))));
					}
//				}
				
//				if(m==keys.size()-1){
//					fitter.addObservedPoint(new WeightedObservedPoint(1000.0, (double) m, (double) (lexicon.get(keys.get(k)))));
//				}
//				a++;
//			}
//			fitter.addObservedPoint(new WeightedObservedPoint(1.0, (double) m, (double) (lexicon.get(keys.get(k)))));
			m++;
		}
		System.out.println("fit");
		
		PolynomialFunction func= new PolynomialFunction(fitter.fit());
//		PolynomialFunction func= new PolynomialFunction(fitter.fit(new PolynomialFunction.Parametric(), new double[]{0, 0, 0, -0.01}));
		System.out.println(func.toString());
		int z = 0;
//		System.out.println(func.derivative());
		XYSeries interpolated = new XYSeries("approx");
		for(WeightedObservedPoint p: fitter.getObservations()){
			interpolated.add(p.getX(), func.value(p.getX()));
		}
//		for(int k=keys.size()-1; k>=0;k--){
//			interpolated.add((double)z, func.value(z));
//			z++;
//		}
		
//		ArrayList<String> keys = new ArrayList<String>(lexicon.keySet());
//		int m = 0;
//		for(int k=keys.size()-1; k>=0;k--){
//			xValues.add((double) (m));
//			yValues.add((double) (lexicon.get(keys.get(k))));
//            m++;
//        }
		
		//
//		SplineInterpolator interpolator = new SplineInterpolator();
//		PolynomialSplineFunction function = interpolator.interpolate(
//				ArrayUtils.toPrimitive(xValues.toArray(new Double[xValues.size()])),
//				ArrayUtils.toPrimitive(yValues.toArray(new Double[yValues.size()])));
//
//		System.out.println(function.getPolynomials());
		
		
//		XYSeries interpolated = new XYSeries("Upper Bound");
//		for (double xvalue : xValues) {
//			interpolated.add(xvalue, function.value(xvalue));
//		}

		XYSeries upperBound = new XYSeries("Upper Bound");
		upperBound.add(0,

				getNthPositivePercent(upperPercentage));
		upperBound.add(lexicon.keySet().size(), getNthPositivePercent(upperPercentage));

		XYSeries lowerBopund = new XYSeries("Lower Bound");
		lowerBopund.add(0, getNthNegativePercent(lower));
		lowerBopund.add(lexicon.keySet().size(), getNthNegativePercent(lower));

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		dataset.addSeries(upperBound);
		dataset.addSeries(lowerBopund);
		 dataset.addSeries(interpolated);
		JFreeChart xylineChart = ChartFactory.createXYLineChart("dice distribution", "", "Score", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		int width = 1280; /* Width of the image */
		int height = 960; /* Height of the image */
		File XYChart = new File("src/main/resources/plots/" + target + ".jpeg");
		ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);

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
