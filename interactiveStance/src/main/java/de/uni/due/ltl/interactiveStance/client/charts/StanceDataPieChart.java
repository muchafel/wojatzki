package de.uni.due.ltl.interactiveStance.client.charts;

import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.util.EvaluationScenarioUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;
import org.vaadin.addon.JFreeChartWrapper;

import java.awt.*;
import java.text.NumberFormat;

public class StanceDataPieChart {

    private DefaultPieDataset dataset = new DefaultPieDataset();

    public StanceDataPieChart() {
    }

    /**
     *
     * @param service Instace of Backend from which data are loaded
     * @return A wrapper of JFreeChart
     */
    public JFreeChartWrapper createPieChart(BackEnd service) {
        JFreeChart chart = createchart(createPieData(service), service.getEvaluationScenario().getTarget());
        return new JFreeChartWrapper(chart);
    }

    /**
     * create a JFreeChart object of pie chart.
     * @param dataset Data set of pie chart.
     * @return
     */
    private JFreeChart createchart(PieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart(
                title, // chart
                dataset, // data
                false, // include legend
                true,
                false);
        // set chart background transparent
//        chart.setBackgroundPaint(new Color(0, 0, 0, 0));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setBackgroundPaint(null);
        plot.setShadowPaint(null);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));

        // keep space between all blocks.
//        plot.setExplodePercent("FAVOR", 0.01);
//        plot.setExplodePercent("AGAINST", 0.05);
//        plot.setExplodePercent("NONE", 0.01);
        // set color of each part in pie chart.
        java.util.List<Comparable> keys = dataset.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals("FAVOR")) {
                plot.setSectionPaint("FAVOR", new Color(60, 196, 73));
            } else if (keys.get(i).equals("AGAINST")) {
                plot.setSectionPaint("AGAINST", new Color(234, 107, 93));
            } else if (keys.get(i).equals("NONE")) {
                plot.setSectionPaint("NONE", new Color(188, 183, 183));
            }
        }
        // no border
        plot.setOutlineVisible(false);
        return chart;
    }

    /**
     *
     * @param service Instace of Backend from which data are loaded
     * @return
     */
    public PieDataset createPieData(BackEnd service) {
        this.dataset.setValue("FAVOR", service.getEvaluationScenario().getTrainData().getNumberOfFavor());
        this.dataset.setValue("AGAINST", service.getEvaluationScenario().getTrainData().getNumberOfAgainst());
        this.dataset.setValue("NONE", service.getEvaluationScenario().getTrainData().getNumberOfNone());
        return this.dataset;
    }

}
