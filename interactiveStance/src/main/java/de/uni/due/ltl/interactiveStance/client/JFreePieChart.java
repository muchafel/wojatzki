package de.uni.due.ltl.interactiveStance.client;

import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.vaadin.addon.JFreeChartWrapper;

import java.awt.*;
import java.text.NumberFormat;

public class JFreePieChart {

    private DefaultPieDataset dataset = new DefaultPieDataset();

    public JFreePieChart() {
    }

    /**
     *
     * @param service Instace of Backend from which data are loaded
     * @return A wrapper of JFreeChart
     */
    public JFreeChartWrapper createPieChart(BackEnd service) {
        JFreeChart chart = createchart(createPieData(service));
        return new JFreeChartWrapper(chart);
    }

    /**
     * create a JFreeChart object of pie chart.
     * @param dataset Data set of pie chart.
     * @return
     */
    private JFreeChart createchart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Class Distribution", // chart
                dataset, // data
                false, // include legend
                true,
                false);
        // set chart background transparent
        chart.setBackgroundPaint(new Color(0, 0, 0, 0));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setBackgroundPaint(new Color(0, 0, 0, 0));
        plot.setLabelGap(0.02);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));
        // keep space between all blocks.
        plot.setExplodePercent("FAVOR", 0.05);
        plot.setExplodePercent("AGAINST", 0.1);
        plot.setExplodePercent("NONE", 0.05);
        // set color of each part in pie chart.
        java.util.List<Comparable> keys = dataset.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals("FAVOR")) {
                plot.setSectionPaint("FAVOR", Color.GREEN);
            } else if (keys.get(i).equals("AGAINST")) {
                plot.setSectionPaint("AGAINST", Color.RED);
            } else if (keys.get(i).equals("NONE")) {
                plot.setSectionPaint("NONE", Color.GRAY);
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
        this.dataset.setValue("FAVOR", service.getTrainData().getNumberOfFavor());
        this.dataset.setValue("AGAINST", service.getTrainData().getNumberOfAgainst());
        this.dataset.setValue("NONE", service.getTrainData().getNumberOfNone());
        return this.dataset;
    }

}
