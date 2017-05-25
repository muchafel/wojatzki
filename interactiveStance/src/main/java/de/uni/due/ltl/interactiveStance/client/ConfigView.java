package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import de.uni.due.ltl.interactiveStance.util.EvaluationScenarioUtil;
import java.util.ArrayList;
import java.util.List;


public class ConfigView extends VerticalLayout implements View {

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
    private static String scenario = "Atheism";	// store scenario which is set up on config webpage.
    private List<String> scenarioItems;
    private ComboBox<String> scenarioComboBox = new ComboBox<>("Scenario");

//    JFreeChartWrapper pieChart;
//    Label pieChartLabel = new Label("inspect data");

    private List<String> modes = new ArrayList<>();
    private ComboBox<String> modeComboBox = new ComboBox<>("Experiment Mode");
    private Button startBtn = new Button("Start");

    public static String getScenario() {
        System.out.println("scenario: " + scenario);
        return scenario;
    }

    public ConfigView() {
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
            this.scenario = scenarioComboBox.getValue().replace(" ", "");
            getUI().getNavigator().navigateTo(MainUI.DETECTORVIEW);
        });
        FormLayout comboBoxFormLayout = new FormLayout();
        comboBoxFormLayout.addComponent(modeComboBox);
        HorizontalLayout experimentModeLayout = new HorizontalLayout();
        experimentModeLayout.addComponent(comboBoxFormLayout);
        experimentModeLayout.addComponent(startBtn);
        experimentModeLayout.setComponentAlignment(startBtn, Alignment.MIDDLE_CENTER);

        this.addComponent(scenarioHorizon);
        this.addComponent(experimentModeLayout);
        this.setComponentAlignment(experimentModeLayout, Alignment.MIDDLE_CENTER);
        this.setComponentAlignment(scenarioHorizon, Alignment.MIDDLE_CENTER);
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
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
