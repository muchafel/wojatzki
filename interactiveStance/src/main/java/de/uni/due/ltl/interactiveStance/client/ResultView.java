package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ClientConnector;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.client.charts.PredictionQualityPieChart;

public class ResultView extends VerticalLayout implements View {

	private EvaluationResult result;
	HorizontalLayout pieCharts = new HorizontalLayout();
	Label microF1Label= new Label();
	Label macroF1Label= new Label();
	
	Button backToDetectorBtn = new Button("Back");
	Button ablationBtn = new Button("Analyze Topic Contribution");

	public ResultView(EvaluationResult result, BackEnd service) {
		
		this.result=result;
		
		pieCharts.setWidth("100%");
		this.addComponent(pieCharts);
		this.addComponent(microF1Label);
		this.addComponent(macroF1Label);
		this.addComponent(ablationBtn);
		this.addComponent(backToDetectorBtn);
		
		
		ablationBtn.addClickListener(event -> {
			 ((MainUI) this.getUI()).showAblationView(result,service);
		});
		
		backToDetectorBtn.addClickListener(event -> {
			getUI().getNavigator().navigateTo(MainUI.DETECTORVIEW);
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		this.addResults(this.result);
	}

	/**
	 * add result data to charts and boxes
	 * @param result
	 */
	private void addResults(EvaluationResult result) {
		pieCharts.addComponent(new PredictionQualityPieChart().createPieChart("FAVOR", result.getfFAVOR()));
		pieCharts.addComponent(new PredictionQualityPieChart().createPieChart("AGAINST", result.getfAGAINST()));
		pieCharts.addComponent(new PredictionQualityPieChart().createPieChart("NONE", result.getfNONE()));
		microF1Label.setValue("MICRO F1 "+String.valueOf(result.getMicroF()));
		macroF1Label.setValue("MACRO F1 "+String.valueOf(result.getMacroF()));
	}

	/**
	 * code dublication refactor into superclass
	 */
	@Override
	public UI getUI() {
		ClientConnector connector = this;
		while (connector != null) {
			if (connector instanceof UI) {
				return (UI) connector;
			}
			connector = connector.getParent();
		}
		return null;
	}

}
