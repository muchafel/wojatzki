package de.uni.due.ltl.interactiveStance.client;

import java.util.Map;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.client.charts.AblationBarchart;
import de.uni.due.ltl.interactiveStance.client.charts.PredictionQualityPieChart;

public class AblationView  extends VerticalLayout implements View {

	
	HorizontalLayout barcharts = new HorizontalLayout();
	private Map<String,Double> ablationFavor;
	private Map<String,Double> ablationAgainst;
	private BackEnd service;
	private EvaluationResult result;
	
	public AblationView(EvaluationResult result, BackEnd service) {
		this.service= service;
		this.result= result;
		barcharts.setWidth("100%");
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		this.ablationFavor=service.getAblation(true);
		this.ablationAgainst=service.getAblation(false);
		addData(ablationFavor,ablationAgainst);

	}


	private void addData(Map<String, Double> ablationFavor, Map<String, Double> ablationAgainst) {
		barcharts.addComponent(new AblationBarchart().createChart("FAVOR", result.getMicroF(),ablationFavor));
		barcharts.addComponent(new AblationBarchart().createChart("AGAINST", result.getMicroF(),ablationAgainst));
		
	}

}
