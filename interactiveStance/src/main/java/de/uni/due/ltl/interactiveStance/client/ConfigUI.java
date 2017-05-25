package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.util.EvaluationScenarioUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.vaadin.addon.JFreeChartWrapper;

import javax.servlet.annotation.WebServlet;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Title("Login")
@Theme("valo")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class ConfigUI extends UI {


    /* complex to user provides a formatted file. Instead of uploader now using a dropdown box.
    class FileUploader implements Upload.Receiver, Upload.SucceededListener {
        public File file;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            FileOutputStream fos = null;
            try {
                // Open the file for writing.
                file = new File("/tmp/uploads/" + filename);
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file<br/>",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
            return fos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            Notification.show("upload successfully.");
        }
    }

    Label configLabel = new Label("Configuration");
    FileUploader receiver = new FileUploader();
    Upload selectedFile = new Upload();
    */
	static String scenario = null;	// store scenario which is set up on config webpage.
    List<String> scenarioItems;
    ComboBox<String> scenarioComboBox = new ComboBox<>("Scenario");
   
//    JFreeChartWrapper pieChart;
//    Label pieChartLabel = new Label("inspect data");

    List<String> modes = new ArrayList<>();
    ComboBox<String> modeComboBox = new ComboBox<>("Experiment Mode");
    Button startBtn = new Button("Start");

    public static String getScenario() {
    	return scenario;
    }
    
    @Override
    protected void init(VaadinRequest request) {
        scenarioItems = EvaluationScenarioUtil.formatTargets();
        scenarioComboBox.setItems(scenarioItems);
        scenarioComboBox.setSelectedItem(scenarioItems.get(0));
        FormLayout scenarioFormLayout = new FormLayout();
        scenarioFormLayout.addComponent(scenarioComboBox);
        // workaround, let it align to center. https://github.com/vaadin/framework/issues/6504
        HorizontalLayout scenarioHorizon = new HorizontalLayout();
        scenarioHorizon.addComponent(scenarioFormLayout);

//        pieChart = createPieChart(service);
//        // Default Width*Height: 809*500
//        pieChart.setWidth(480.0F, Unit.PIXELS);
//        pieChart.setHeight(300.0F, Unit.PIXELS);
//        HorizontalLayout piechartLayout = new HorizontalLayout();
//        piechartLayout.addComponent(pieChart);
//        piechartLayout.addComponent(pieChartLabel);
//        piechartLayout.setComponentAlignment(pieChartLabel, Alignment.MIDDLE_CENTER);

        modes.add("Default");
        modes.add("Smart");
        modes.add("Stupid");
        modeComboBox.setItems(modes);
        modeComboBox.setSelectedItem("Default");
        startBtn.addClickListener(event -> {
        	scenario = scenarioComboBox.getValue();
            getPage().setLocation("/detector");
        });
        FormLayout comboBoxFormLayout = new FormLayout();
        comboBoxFormLayout.addComponent(modeComboBox);
        HorizontalLayout experimentModeLayout = new HorizontalLayout();
        experimentModeLayout.addComponent(comboBoxFormLayout);
        experimentModeLayout.addComponent(startBtn);
        experimentModeLayout.setComponentAlignment(startBtn, Alignment.MIDDLE_CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(scenarioHorizon);
        verticalLayout.addComponent(experimentModeLayout);
        verticalLayout.setComponentAlignment(experimentModeLayout, Alignment.MIDDLE_CENTER);
        verticalLayout.setComponentAlignment(scenarioHorizon, Alignment.MIDDLE_CENTER);
        // Pie chart
//        verticalLayout.addComponent(piechartLayout);
//        verticalLayout.setComponentAlignment(piechartLayout, Alignment.MIDDLE_CENTER);
        
        // File uploader
//        selectedFile.setReceiver(receiver);
//        selectedFile.setImmediateMode(false);
//        verticalLayout.addComponent(configLabel);
//        verticalLayout.addComponent(selectedFile);
//        verticalLayout.setComponentAlignment(configLabel, Alignment.TOP_CENTER);
//        verticalLayout.setComponentAlignment(selectedFile, Alignment.MIDDLE_CENTER);
//        selectedFile.addSucceededListener(receiver);

        setContent(verticalLayout);
    }

    /**
     *
     * @param service Instace of Backend from which data are loaded
     * @return A wrapper of JFreeChart
     */
    public static JFreeChartWrapper createPieChart(BackEnd service) {
        JFreeChart chart = createchart(createPieData(service));
        return new JFreeChartWrapper(chart);
    }

    /**
     * create a JFreeChart object of pie chart.
     * @param dataset Data set of pie chart.
     * @return
     */
    private static JFreeChart createchart(PieDataset dataset) {
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
        plot.setExplodePercent("AGAINST", 0.05);
        plot.setExplodePercent("NONE", 0.05);
        // set color of each part in pie chart.
        List<Comparable> keys = dataset.getKeys();
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
    private static PieDataset createPieData(BackEnd service) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("FAVOR", 3);
        dataset.setValue("AGAINST", 4);
        dataset.setValue("NONE", 3);
//        dataset.setValue("FAVOR", service.getTrainData().getNumberOfFavor());
//        dataset.setValue("AGAINST", service.getTrainData().getNumberOfAgainst());
//        dataset.setValue("NONE", service.getTrainData().getNumberOfNone());
        return dataset;
    }

//    @WebServlet(urlPatterns = "/config/*")
//    @VaadinServletConfiguration(ui = ConfigUI.class, productionMode = false)
//    public static class ConfigUIServlet extends VaadinServlet {
//
//    }
}

