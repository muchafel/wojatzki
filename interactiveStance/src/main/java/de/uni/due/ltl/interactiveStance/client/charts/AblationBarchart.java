package de.uni.due.ltl.interactiveStance.client.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.DecimalFormat;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.vaadin.addon.JFreeChartWrapper;


public class AblationBarchart {
//	private DefaultPieDataset dataset = new DefaultPieDataset();
	private float recommendedHeight = 0;
	private int itemSum = 0;

	public AblationBarchart() {
	}

	public float getRecommendedHeight() {
		return this.recommendedHeight;
	}

	private void setRecommendedHeight(int itemSum) {
		this.recommendedHeight = 100 + itemSum * 40; // 40 pixel for each bar item
	}


	private CategoryDataset createBarData(double f1_all, Map<String, Double> ablationFavor) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (String target : ablationFavor.keySet()) {
			dataset.addValue(f1_all - ablationFavor.get(target), "1", target);
			System.out.println(f1_all - ablationFavor.get(target)+" "+ target);
			itemSum++;
		}

		this.setRecommendedHeight(itemSum);
		
		return dataset;
	}

	private JFreeChart createchart(String label, CategoryDataset ablationData) {
		JFreeChart barChart = ChartFactory.createBarChart(label, "Targets", "Impact", ablationData,
				PlotOrientation.HORIZONTAL, false, true, false);

		CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0xeaebed));
		plot.getRangeAxis().setLowerBound(-1.0);
		plot.getRangeAxis().setUpperBound(1.0);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		plot.setRangeGridlinePaint(Color.white);
		// target label
		plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.2f);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
		// disable bar outlines...
		renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
//        GradientPaint gp0 = new GradientPaint(
//        		50, 50, Color.RED,
//                300, 100, Color.BLUE
//        );
//		 renderer.setSeriesPaint(1, gp0);

		renderer.setBaseItemLabelsVisible(true);
		DecimalFormat decimalFormat = new DecimalFormat("##,###.00");
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalFormat));
		renderer.setShadowVisible(false);
		renderer.setMaximumBarWidth((itemSum == 0)?0.05: ((this.recommendedHeight - 100)/(2*itemSum))/(this.recommendedHeight - 100));
		renderer.setBase(0);
		// remove style of bar painter
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setSeriesPaint(0, new Color(0x64bee5));

		return barChart;
	}

	public JFreeChartWrapper createChart(String label, double microF, Map<String, Double> ablationFavor) {
		JFreeChart chart = createchart(label, createBarData(microF, ablationFavor));
		return new JFreeChartWrapper(chart);
	}
}
