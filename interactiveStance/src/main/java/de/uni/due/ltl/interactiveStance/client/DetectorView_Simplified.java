package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;

public class DetectorView_Simplified extends DetectorView_Base implements View {

	@Override
	protected void configureComponents() {
		searchField.setPlaceholder("search term");
        searchField.addValueChangeListener(event -> {
           filter.setVisible(false);
        });

        // configure available grid
        filter.setDescription("filter");
        filter.setPlaceholder("Filter Retrieved Statements");
        filter.addValueChangeListener(e -> refresh_AvailableGrid(e.getValue()));
        filter.setVisible(false);

        searchButton.addClickListener(clickEvent -> {
            this.service.newSearch(searchField.getValue());
            filter.setValue("");
            filter.setVisible(true);
            refresh_AvailableGrid();
            refresh_SelectedGrid();
        });

        breaklineLabel.setWidth("20%");

        // add draggable icon before item.
        listOfAvailableTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("targetName").setId("targetName");
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
        listOfSelectedFavorTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("targetName").setId("targetName");
        listOfSelectedFavorTargets.getColumn("targetName").setExpandRatio(1);
        listOfSelectedFavorTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfSelectedFavorTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

        listOfSelectedAgainstTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("targetName").setId("targetName");
        listOfSelectedAgainstTargets.getColumn("targetName").setExpandRatio(1);
        listOfSelectedAgainstTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfSelectedAgainstTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

        analysisButton.addClickListener(clickEvent -> {
//            Notification.show("SemEval: "+result.getSemEval() + System.lineSeparator()+" MicroF1: "+result.getMicroF());
            if (((ListDataProvider<ExplicitTarget>)listOfSelectedFavorTargets.getDataProvider()).getItems().isEmpty() &&
                    ((ListDataProvider<ExplicitTarget>)listOfSelectedAgainstTargets.getDataProvider()).getItems().isEmpty()) {
                Notification notification = new Notification("Select at least one explicit target before analysis",
                        Notification.Type.WARNING_MESSAGE);
                notification.setDelayMsec(1000);
                notification.show(Page.getCurrent());
            } else {
                EvaluationResult result = service.analyse();
                ((MainUI) this.getUI()).showResult(result, service);
            }
            
        });

        analysisButton.addStyleName(ValoTheme.BUTTON_HUGE);
        analysisButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        analysisButton.setIcon(VaadinIcons.COGS);
	}

}
