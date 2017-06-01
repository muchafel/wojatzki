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

    public static String getScenario() {
        System.out.println("scenario: " + scenario);
        return scenario;
    }
    
    public static String getExperimentMode() {
    	System.out.println("mode: " + experimentMode);
		return experimentMode;
	}

    public ConfigView() {
    	//SetUp lists
        scenarioItems = EvaluationScenarioUtil.formatTargets();
        scenarioComboBox.setItems(scenarioItems);
        scenarioComboBox.setSelectedItem(scenarioItems.get(0));
        
        modes=EvaluationScenarioUtil.getExperimentalModes();
        modeComboBox.setItems(modes);
        modeComboBox.setSelectedItem("Fixed Threshold");
        
        //layout
        FormLayout scenarioFormLayout = new FormLayout();
        scenarioFormLayout.addComponent(scenarioComboBox);
        // workaround, let it align to center. https://github.com/vaadin/framework/issues/6504
        HorizontalLayout scenarioHorizon = new HorizontalLayout();
        scenarioHorizon.addComponent(scenarioFormLayout);

       
        startBtn.addClickListener(event -> {
            this.scenario = scenarioComboBox.getValue().replace(" ", "");
            this.experimentMode=modeComboBox.getValue();
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
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

	

}
