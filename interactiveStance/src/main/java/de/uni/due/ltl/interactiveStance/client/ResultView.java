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

import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.client.charts.AccuracyPieChart;

public class ResultView extends VerticalLayout implements View {

	HorizontalLayout pieCharts = new HorizontalLayout();
	Label microF1Label= new Label();
	Label macroF1Label= new Label();
	
	Button backToDetectorBtn = new Button("Back");

	public ResultView(EvaluationResult result) {
		
		this.addResults(result);
		
		pieCharts.setWidth("100%");
		this.addComponent(pieCharts);
		this.addComponent(microF1Label);
		this.addComponent(macroF1Label);
		this.addComponent(backToDetectorBtn);
		
		backToDetectorBtn.addClickListener(event -> {
			getUI().getNavigator().navigateTo(MainUI.DETECTORVIEW);
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	/**
	 * add result data to charts and boxes
	 * @param result
	 */
	private void addResults(EvaluationResult result) {
		pieCharts.addComponent(new AccuracyPieChart().createPieChart("FAVOR", result.getAccuracyFAVOR()));
		pieCharts.addComponent(new AccuracyPieChart().createPieChart("AGAINST", result.getAccuracyAGAINST()));
		pieCharts.addComponent(new AccuracyPieChart().createPieChart("NONE", result.getAccuracyNONE()));
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
