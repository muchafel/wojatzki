package de.uni.due.ltl.interactiveStance.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.distribution.ZipfDistribution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PlotUtil {

	public static void plotDistributionChart(double upperBoundFixed, double lowerBoundFixed, double zipfLowerBound,
			double zipfUpperBound, ZipfDistribution zipfPositive, ZipfDistribution zipfNegative,
			XYSeries observedSeries, File targetFile, List<String> keys) throws IOException {
		//do fixed upper and lower bounds
				XYSeries upperBoundSeries = new XYSeries("Fixed Upper Bound");
				
				upperBoundSeries.add(0,upperBoundFixed);
				upperBoundSeries.add(keys.size(), upperBoundFixed);
				XYSeries lowerBoundSeries = new XYSeries("Lower Bound");
				lowerBoundSeries.add(0, lowerBoundFixed);
				lowerBoundSeries.add(keys.size(), lowerBoundFixed);
				
				//do upper and lower bounds according to zipf distribution
				
				System.out.println(zipfUpperBound);
				System.out.println(zipfLowerBound);
				XYSeries zipfUpperBoundSeries = new XYSeries("Zipf Upper Bound");
				zipfUpperBoundSeries.add(0,zipfUpperBound);
				zipfUpperBoundSeries.add(keys.size(),zipfUpperBound);
				XYSeries zipfLowerBoundSeries = new XYSeries("Zipf Lower Bound");
				zipfLowerBoundSeries.add(0, zipfLowerBound);
				zipfLowerBoundSeries.add(keys.size(), zipfLowerBound);

				
				//add interpolated points  
				XYSeries interpolatedPositive = new XYSeries("approx positive");
				XYSeries interpolatedNegative = new XYSeries("approx negative");
				int z=0;
				int j = keys.size();
				for (String key : keys) {
					interpolatedPositive.add(j, zipfPositive.probability(j));
					interpolatedNegative.add(z, -zipfNegative.probability(-1 * z + keys.size()));
					z++;
					j--;
				}
				
				
				//add series to plot
				final XYSeriesCollection dataset = new XYSeriesCollection();
				dataset.addSeries(observedSeries);
				dataset.addSeries(upperBoundSeries);
				dataset.addSeries(lowerBoundSeries);
				dataset.addSeries(interpolatedPositive);
				dataset.addSeries(interpolatedNegative);
				dataset.addSeries(zipfLowerBoundSeries);
				dataset.addSeries(zipfUpperBoundSeries);
				
				//set up chart
				JFreeChart xylineChart = ChartFactory.createXYLineChart("dice distribution", "", "Score", dataset,PlotOrientation.VERTICAL, true, true, false);
				NumberAxis range = (NumberAxis) xylineChart.getXYPlot().getRangeAxis();
		        range.setRange(-0.02, 0.02);
				
				int width = 1280; /* Width of the image */
				int height = 960; /* Height of the image */
				File XYChart = targetFile;
				ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);
		
	}

}
