package de.uni.due.ltl.interactiveStance.client.detectorViews;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.Orientation;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Slider;

import de.uni.due.ltl.interactiveStance.backend.ExperimentConfiguration;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;

public class DetectorView_Expert_Adjustable extends DetectorView_Expert implements View {

	private Slider sensitivitySlider;
	
	public DetectorView_Expert_Adjustable(ExperimentLogging logging, ExperimentConfiguration config) {
		super(logging, config);
	}
	
	@Override
	protected void configureComponents() {
		
		sensitivitySlider=new Slider("Model Sensitivity", 0, 100);
		sensitivitySlider.setDescription("Sets how sensitive the model is for polarity markers. \n If the sensitivity is high even slight hints on polarity are used. \nIf the sensitivity is low only the stromgest indicators will be used for classification.");
		sensitivitySlider.setOrientation(SliderOrientation.HORIZONTAL);
		
		//TODO do that at the right place!
		advancedSettings.addComponent(sensitivitySlider);

		sensitivitySlider.addValueChangeListener(event -> {
		    int value = event.getValue().intValue();
		    new AdjustmentEvent(logging, value).persist(false);
		    service.adjustAnalyzer(value);
		});
		super.configureComponents();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		sensitivitySlider.setValue(service.getAnalyzerAdjustment());
	}

}
