package de.uni.due.ltl.interactiveStance.client.charts;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.vaadin.addon.JFreeChartWrapper;

import com.vaadin.ui.Component;

public class AblationBarchart {
	private DefaultPieDataset dataset = new DefaultPieDataset();

	public AblationBarchart() {
	}


	private CategoryDataset createBarData(double f1_all, Map<String, Double> ablationFavor) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(String target: ablationFavor.keySet()){
			dataset.addValue(f1_all-ablationFavor.get(target), target, "");
		}
		return dataset;
	}

	private JFreeChart createchart(String label, CategoryDataset ablationData) {
		JFreeChart barChart = ChartFactory.createBarChart(
		         "Title",           
		         "Impact",            
		         label,
		         ablationData,          
		         PlotOrientation.VERTICAL,           
		         true, true, false);

		
		return barChart;
	}

	private DefaultPieDataset createPieData(double accuracy) {
		this.dataset.setValue("Correct", accuracy);
		this.dataset.setValue("Wrong", 1.0 - accuracy);
		return this.dataset;
	}

	public Component createChart(String label, double microF, Map<String, Double> ablationFavor) {
		JFreeChart chart = createchart(label, createBarData(microF,ablationFavor));
		return new JFreeChartWrapper(chart);
	}
}
