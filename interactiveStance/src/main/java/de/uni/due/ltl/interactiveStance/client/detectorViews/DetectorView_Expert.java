package de.uni.due.ltl.interactiveStance.client.detectorViews;

import com.mysql.cj.api.x.Result;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExperimentConfiguration;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.client.charts.StanceDataPieChart;
import de.uni.due.ltl.interactiveStance.coverage.CoverageResult;
import de.uni.due.ltl.interactiveStance.experimentLogging.CoverageEvent;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;

import org.vaadin.addon.JFreeChartWrapper;

import java.util.List;
import java.util.Set;


public class DetectorView_Expert extends DetectorView_Base implements View {

 public DetectorView_Expert(ExperimentLogging logging,ExperimentConfiguration config) {
		super(config,logging);
	}


private Button coverageButton;
	
	@Override
	protected void configureComponents() {
		super.configureComponents();
		coverageButton= new Button("Coverage ");
		coverageButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		coverageButton.setIcon(VaadinIcons.MAGIC);
		
		coverageButton.addClickListener(clickEvent -> {
              CoverageResult result = service.analyseCoverage();
              new CoverageEvent(logging,result.getCoverageSelection()).persist(false);
              Notification.show("coverage of your selection "+String.valueOf(result.getCoverageSelection()));
			});
		this.addComponent(coverageButton);
		
	}
 
 
	@Override
	protected void configureGrids() {
		// add draggable icon before item.
        listOfAvailableTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("statements").setId("targetName");
        listOfAvailableTargets.addColumn(ExplicitTarget::getInstancesInFavor).setCaption("instances in favor").setId("instancesInFavor");
        listOfAvailableTargets.addColumn(ExplicitTarget::getInstancesAgainst).setCaption("instances against").setId("instancesAgainst");
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
        listOfSelectedFavorTargets.addColumn(ExplicitTarget::getInstancesInFavor).setCaption("instances in favor").setId("instancesInFavor");
        listOfSelectedFavorTargets.addColumn(ExplicitTarget::getInstancesAgainst).setCaption("instances against").setId("instancesAgainst");
        listOfSelectedFavorTargets.getColumn("targetName").setExpandRatio(1);
        listOfSelectedFavorTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfSelectedFavorTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

        listOfSelectedAgainstTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("statements").setId("targetName");
        listOfSelectedAgainstTargets.addColumn(ExplicitTarget::getInstancesInFavor).setCaption("instances in favor").setId("instancesInFavor");
        listOfSelectedAgainstTargets.addColumn(ExplicitTarget::getInstancesAgainst).setCaption("instances against").setId("instancesAgainst");
        listOfSelectedAgainstTargets.getColumn("targetName").setExpandRatio(1);
        listOfSelectedAgainstTargets.getColumn("targetName").setDescriptionGenerator(ExplicitTarget::getTargetName);
        listOfSelectedAgainstTargets.setSelectionMode(Grid.SelectionMode.SINGLE);
		
	}

}