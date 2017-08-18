package de.uni.due.ltl.interactiveStance.client.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.elements.Rectangle;
import com.byteowls.vaadin.chartjs.options.scale.*;
import com.vaadin.ui.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AblationBarChartjs {

    private BarChartConfig barConfig;
    private List<String> labels;
    private List<Double> data;

    public AblationBarChartjs() {

    }
    public AblationBarChartjs(String legend, String datasetColor, double f1_all, Map<String, Double> ablationData) {
        this.labels = new ArrayList<>();
        this.data = new ArrayList<>();

        createBarData(f1_all, ablationData);

        this.barConfig = new BarChartConfig();
        LinearScale barScale = new LinearScale();
        barScale.ticks().stepSize(0.2);
        barScale.ticks().min(-1);
        barScale.ticks().max(1);
        barScale.ticks().beginAtZero(true);
        this.barConfig.horizontal();
        this.barConfig.
                data()
                    .labelsAsList(this.labels)
                    .addDataset(new BarDataset().backgroundColor(datasetColor).label("Dataset" + legend))
                    .and()
                .options()
                    .scales()
                        .add(Axis.X, barScale)
                        .add(Axis.Y, new DefaultScale().barPercentage(0.5))
                        .and()
                    .responsive(true)
                    .title()
                        .display(true)
                        .text(legend)
                        .and()
                    .elements()
                        .rectangle()
                            .borderWidth(2)
//                            .borderColor("rgb(0, 255, 0)")
                            .borderSkipped(Rectangle.RectangleEdge.LEFT)
                            .and()
                        .and()
                    .legend()
                        .fullWidth(false)
                        .position(Position.LEFT)
                        .and()
                    .done();

        List<String> labels = barConfig.data().getLabels();
        for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
            BarDataset lds = (BarDataset) ds;
            lds.dataAsList(this.data);
        }
    }

    public Component getChart() {
        ChartJs chart = new ChartJs(barConfig);
        chart.setJsLoggingEnabled(true);

        return chart;
    }

    private void createBarData(double f1_all, Map<String, Double> ablationFavor) {
        for (String target : ablationFavor.keySet()) {
//            this.labels.add(target.substring(0, Math.min(target.length(), 12)));
            this.labels.add(target);
            data.add(f1_all - ablationFavor.get(target));
            System.out.println(f1_all - ablationFavor.get(target)+" "+ target);
        }
    }
}
