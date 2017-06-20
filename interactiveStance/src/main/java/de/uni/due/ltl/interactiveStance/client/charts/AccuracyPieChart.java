package de.uni.due.ltl.interactiveStance.client.charts;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jsoup.nodes.Element;
import org.vaadin.addon.JFreeChartWrapper;

import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Extension;
import com.vaadin.server.Resource;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import com.vaadin.ui.declarative.DesignContext;

import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import elemental.json.JsonObject;

public class AccuracyPieChart {
	private DefaultPieDataset dataset = new DefaultPieDataset();

	public AccuracyPieChart() {
	}

	public JFreeChartWrapper createPieChart(String label, double accuracyFAVOR) {
		JFreeChart chart = createchart(label, createPieData(accuracyFAVOR));
		return new JFreeChartWrapper(chart);
	}

	private JFreeChart createchart(String label, DefaultPieDataset defaultPieDataset) {
		JFreeChart chart = ChartFactory.createPieChart(label, // chart
				dataset, // data
				false, // include legend
				true, false);
		chart.setBackgroundPaint(new Color(0, 0, 0, 0));

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setBackgroundPaint(new Color(0, 0, 0, 0));
		plot.setLabelGap(0.02);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}", NumberFormat.getNumberInstance(),NumberFormat.getPercentInstance()));
		plot.setOutlineVisible(true);
		return chart;
	}

	private DefaultPieDataset createPieData(double accuracy) {
		this.dataset.setValue("Correct", accuracy);
		this.dataset.setValue("Wrong", 1.0 - accuracy);
		return this.dataset;
	}

}
