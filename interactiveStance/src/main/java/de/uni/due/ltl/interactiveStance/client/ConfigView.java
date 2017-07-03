package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import de.uni.due.ltl.interactiveStance.util.EvaluationScenarioUtil;
import java.util.ArrayList;
import java.util.List;


public class ConfigView extends VerticalLayout implements View {

	 //current config
    private static String scenario = "Atheism";	// store scenario which is set up on config webpage.
    private static String experimentMode = "Fixed Threshold"; // store mode which is set up during config.
    
    //lists for scenarios
    private List<String> scenarioItems;
    private ComboBox<String> scenarioComboBox = new ComboBox<>("Scenario");

    //lists for modes
    private List<String> modes;
    private ComboBox<String> modeComboBox = new ComboBox<>("Experiment Mode");
  
    private Button startBtn = new Button("Start");
    private CheckBox simpleModeCheckBox = new CheckBox("Simple Mode");


    public static String getScenario() {
        return scenario;
    }
    
    public static String getExperimentMode() {
		return experimentMode;
	}

    public ConfigView() {
    	//SetUp lists
        scenarioItems = EvaluationScenarioUtil.formatTargets();
        scenarioComboBox.setItems(scenarioItems);
        scenarioComboBox.setSelectedItem(scenarioItems.get(0));
        
        modes = EvaluationScenarioUtil.getExperimentalModes();
        modeComboBox.setItems(modes);
        modeComboBox.setSelectedItem("Fixed Threshold");
        simpleModeCheckBox.setValue(true);

        //layout, for your information to remind there has an issue of alignment in vaadin
//        FormLayout scenarioFormLayout = new FormLayout();
//        scenarioFormLayout.addComponent(scenarioComboBox);
//        workaround, let it align to center. https://github.com/vaadin/framework/issues/6504
//        HorizontalLayout scenarioHorizon = new HorizontalLayout();
//        scenarioHorizon.addComponent(scenarioFormLayout);

       
		startBtn.addClickListener(event -> {
			this.scenario = scenarioComboBox.getValue().replace(" ", "");
			this.experimentMode = modeComboBox.getValue();
			// create a simplified or expert detector and access it
			((MainUI) this.getUI()).showDetectorView(simpleModeCheckBox.getValue());
		});

        Panel configPanel = new Panel();
        configPanel.setCaption("Basis Configurations");

        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(scenarioComboBox);
        formLayout.addComponent(modeComboBox);
        formLayout.addComponent(simpleModeCheckBox);
        formLayout.addComponent(startBtn);
        formLayout.setMargin(true);

        configPanel.setContent(formLayout);
        configPanel.setSizeUndefined();

        this.addComponent(configPanel);
        this.setComponentAlignment(configPanel, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        ((MainUI) this.getUI()).showMenubar();
    }

	

}
