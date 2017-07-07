package de.uni.due.ltl.interactiveStance.client.detectorViews;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExperimentConfiguration;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;

public class DetectorView_Simplified extends DetectorView_Base implements View {


	private HorizontalLayout favorExplanation;
	private HorizontalLayout againstExplanation;
	private Button favorExplanationIcon;
	private Label favorExplanationText;
	private Button againstExplanationIcon;
	private Label againstExplanationText;
	
	@Override
	protected void buildLayout() {
		favorExplanationText= new Label();
		favorExplanation= new HorizontalLayout();
		favorExplanationIcon= new Button();
		
		againstExplanationText=new Label();
		againstExplanation= new HorizontalLayout();
		againstExplanationIcon= new Button();
		
		againstExplanationText.addStyleName(ValoTheme.LABEL_H2);
		againstExplanationText.addStyleName("label-wrap");
		againstExplanationIcon.setIcon(VaadinIcons.ANGLE_DOUBLE_DOWN);
		againstExplanationIcon.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		againstExplanationIcon.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		againstExplanationIcon.addStyleName(ValoTheme.BUTTON_HUGE);
		againstExplanationIcon.setEnabled(false);

		favorExplanationIcon.setIcon(VaadinIcons.ANGLE_DOUBLE_DOWN);
		favorExplanationIcon.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		favorExplanationIcon.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		favorExplanationIcon.addStyleName(ValoTheme.BUTTON_HUGE);
		favorExplanationIcon.setEnabled(false);
		favorExplanationText.addStyleName(ValoTheme.LABEL_H2);
		favorExplanationText.addStyleName("label-wrap");

		favorExplanation.addComponent(favorExplanationIcon);
		favorExplanation.addComponent(favorExplanationText);
		favorExplanation.setWidth("100%");
		favorExplanation.setExpandRatio(favorExplanationText, 1.0f);

		againstExplanation.addComponent(againstExplanationIcon);
		againstExplanation.addComponent(againstExplanationText);
		againstExplanation.setWidth("100%");
		againstExplanation.setExpandRatio(againstExplanationText, 1.0f);

		selectedFavorTargetsContent.addComponent(favorExplanation);
		selectedAgainstTargetsContent.addComponent(againstExplanation);


		super.buildLayout();
	}
	
	
	public DetectorView_Simplified(ExperimentLogging logging,ExperimentConfiguration config) {
		super(config,logging);
	}

	
	@Override
	protected void configureGrids() {
		// add dragable icon before item.
        listOfAvailableTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("statements").setId("targetName");
        // The length of Target Name is often long. let it take all extra space.
        listOfAvailableTargets.getColumn("targetName").setExpandRatio(1);
        listOfAvailableTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfAvailableTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

        //set icon
        listOfSelectedFavorTargets.setIcon(VaadinIcons.PLUS_CIRCLE);
        listOfSelectedFavorTargets.setCaption("Favor Statements");
        listOfSelectedAgainstTargets.setIcon(VaadinIcons.MINUS_CIRCLE);
        listOfSelectedAgainstTargets.setCaption("Against Statements");
        // configure selection grid of favor and against
        listOfSelectedFavorTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("statements").setId("targetName");
        listOfSelectedFavorTargets.getColumn("targetName").setExpandRatio(1);
        listOfSelectedFavorTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfSelectedFavorTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

        listOfSelectedAgainstTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("statements").setId("targetName");
        listOfSelectedAgainstTargets.getColumn("targetName").setExpandRatio(1);
        listOfSelectedAgainstTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfSelectedAgainstTargets.setSelectionMode(Grid.SelectionMode.SINGLE);
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		this.favorExplanationText.setValue("drag down statements which could be made by someone who is in favor of "+service.getEvaluationScenario().getTarget());
		this.againstExplanationText.setValue("drag down statements which could be made by someone who is against "+service.getEvaluationScenario().getTarget());
	}


}
