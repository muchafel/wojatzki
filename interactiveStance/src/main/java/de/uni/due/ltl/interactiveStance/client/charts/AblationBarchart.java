package de.uni.due.ltl.interactiveStance.client.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.NumberFormat;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
		for (String target : ablationFavor.keySet()) {
			dataset.addValue(f1_all - ablationFavor.get(target), "1", target);
			System.out.println(f1_all - ablationFavor.get(target)+" "+ target);
		}
		
		return dataset;
	}

	private JFreeChart createchart(String label, CategoryDataset ablationData) {
		JFreeChart barChart = ChartFactory.createBarChart(label, "Targets", "Impact", ablationData,
				PlotOrientation.HORIZONTAL, false, true, false);

		CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
		plot.getRangeAxis().setLowerBound(-1.0);
		plot.getRangeAxis().setUpperBound(1.0);
        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(
        		50, 50, Color.RED,
                300, 100, Color.BLUE
        );

		renderer.setShadowVisible(false);
		renderer.setMaximumBarWidth(.2);
		renderer.setBase(0);
//        renderer.setSeriesPaint(1, gp0);

		return barChart;
	}

	public JFreeChartWrapper createChart(String label, double microF, Map<String, Double> ablationFavor) {
		JFreeChart chart = createchart(label, createBarData(microF, ablationFavor));
		return new JFreeChartWrapper(chart);
	}
}
