package de.uni.due.ltl.interactiveStance.client;

import javax.servlet.annotation.WebServlet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.vaadin.addon.JFreeChartWrapper;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * main GUI class; use this to start the application
 * @author michael
 *
 */
@Title("Interactive Stance Detection")
@Theme("valo")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class InteractiveStanceGUI extends UI {

	TextField searchField = new TextField();
	TextField filter = new TextField();
	Grid<ExplicitTarget> listOfAvailableTargets = new Grid<>("Available Targets");
	Grid<ExplicitTarget> listOfSelectedFavorTargets = new Grid<>("Selected Targets of Favor");
	Grid<ExplicitTarget> listOfSelectedAgainstTargets = new Grid<>("Selected Targets of Against");
	Button searchButton = new Button("GO");
	Button analysisButton = new Button("Analysis");
	BackEnd service = BackEnd.loadData();
	// keep the reference of dragged items.
	Set<ExplicitTarget> draggedItems;
	// drag source: available, drop target: selected
	GridDragSource<ExplicitTarget> selectedFavorDrag = new GridDragSource<>(listOfSelectedFavorTargets);
	GridDragSource<ExplicitTarget> selectedAgainstDrag = new GridDragSource<>(listOfSelectedAgainstTargets);
	GridDropTarget<ExplicitTarget> availableDrop = new GridDropTarget<>(listOfAvailableTargets,
			DropMode.ON_TOP_OR_BETWEEN);
	// drag source: selected, drop target: available
	GridDragSource<ExplicitTarget> availableDrag = new GridDragSource<>(listOfAvailableTargets);
	GridDropTarget<ExplicitTarget> selectedFavorDrop = new GridDropTarget<>(listOfSelectedFavorTargets,
			DropMode.ON_TOP_OR_BETWEEN);
	GridDropTarget<ExplicitTarget> selectedAgainstDrop = new GridDropTarget<>(listOfSelectedAgainstTargets,
			DropMode.ON_TOP_OR_BETWEEN);
    JFreeChartWrapper pieChart;
    Label pieChartLabel = new Label("inspect data");

	/**
	 * entry point for GUI
	 */
	@Override
	protected void init(VaadinRequest request) {
		configureComponents();
		buildLayout();
	}

	/**
	 * Here we configure properties of our components
	 */
	private void configureComponents() {
		searchField.setPlaceholder("Get Targets Candidates");
		
		// configure available grid
		filter.setDescription("filter");
		filter.setPlaceholder("Filter Term");
		filter.addValueChangeListener(e -> refresh_AvailableGrid(e.getValue()));

		searchButton.addClickListener(clickEvent -> {
			service.newSearch(searchField.getValue());
			refresh_AvailableGrid();
			refresh_SelectedGrid();
		});

		// add draggable icon before item.
		listOfAvailableTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("targetName").setId("targetName");
		listOfAvailableTargets.addColumn(ExplicitTarget::getInstancesInFavor).setCaption("instancesInFavor").setId("instancesInFavor");
		listOfAvailableTargets.addColumn(ExplicitTarget::getInstancesAgainst).setCaption("instancesAgainst").setId("instancesAgainst");
		// The length of Target Name is often long. let it take all extra space.
		listOfAvailableTargets.getColumn("targetName").setExpandRatio(1);
		listOfAvailableTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

		// configure selection grid of favor and against
		listOfSelectedFavorTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("targetName").setId("targetName");
		listOfSelectedFavorTargets.addColumn(ExplicitTarget::getInstancesInFavor).setCaption("instancesInFavor").setId("instancesInFavor");
		listOfSelectedFavorTargets.addColumn(ExplicitTarget::getInstancesAgainst).setCaption("instancesAgainst").setId("instancesAgainst");
		listOfSelectedFavorTargets.getColumn("targetName").setExpandRatio(1);
		listOfSelectedFavorTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

		listOfSelectedAgainstTargets.addColumn(target -> VaadinIcons.ELLIPSIS_V.getHtml() + " " + target.getTargetName(), new HtmlRenderer()).setCaption("targetName").setId("targetName");
		listOfSelectedAgainstTargets.addColumn(ExplicitTarget::getInstancesInFavor).setCaption("instancesInFavor").setId("instancesInFavor");
		listOfSelectedAgainstTargets.addColumn(ExplicitTarget::getInstancesAgainst).setCaption("instancesAgainst").setId("instancesAgainst");
		listOfSelectedAgainstTargets.getColumn("targetName").setExpandRatio(1);
		listOfSelectedAgainstTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

		// initial filling of grid
		refresh_AvailableGrid();
		refresh_SelectedGrid();

		analysisButton.addClickListener(clickEvent -> {
			EvaluationResult result= service.analyse();
			Notification.show("SemEval: "+result.getSemEval() + System.lineSeparator()+" MicroF1: "+result.getMicroF());
		});

		analysisButton.addStyleName(ValoTheme.BUTTON_HUGE);
		analysisButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		analysisButton.setIcon(VaadinIcons.COGS);
	}


	/**
	 * Here we stack the components together
	 */
	private void buildLayout() {		
		HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);
		searchLayout.setMargin(false);
		searchLayout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);
		searchLayout.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER);
		
		pieChart = ConfigUI.createPieChart(service);
        // Default Width*Height: 809*500
        pieChart.setWidth(480.0F, Unit.PIXELS);
        pieChart.setHeight(300.0F, Unit.PIXELS);
        HorizontalLayout piechartLayout = new HorizontalLayout();
        piechartLayout.addComponent(pieChart);
        piechartLayout.addComponent(pieChartLabel);
        piechartLayout.setComponentAlignment(pieChartLabel, Alignment.MIDDLE_CENTER);

		listOfAvailableTargets.setWidth("100%");
		listOfAvailableTargets.setHeightMode(HeightMode.ROW);
		listOfAvailableTargets.setHeightByRows(6.0D);

		listOfSelectedFavorTargets.setWidth("100%");
		listOfSelectedFavorTargets.setHeightMode(HeightMode.ROW);
		listOfSelectedFavorTargets.setHeightByRows(4.0D);
		listOfSelectedAgainstTargets.setWidth("100%");
		listOfSelectedAgainstTargets.setHeightMode(HeightMode.ROW);
		listOfSelectedAgainstTargets.setHeightByRows(4.0D);

		setDragFromAvailable();
		setDragFromSelected();

		HorizontalLayout selectedTargetsContent = new HorizontalLayout();
		selectedTargetsContent.addComponent(listOfSelectedFavorTargets);
		selectedTargetsContent.addComponent(listOfSelectedAgainstTargets);
		selectedTargetsContent.setWidth("100%");
		selectedTargetsContent.setSpacing(true);

		VerticalLayout left = new VerticalLayout(pieChart, searchLayout, filter, listOfAvailableTargets, 
				analysisButton, selectedTargetsContent);
		left.setSpacing(true);

		HorizontalLayout mainLayout = new HorizontalLayout(left);
		mainLayout.setWidth("100%");
		mainLayout.setMargin(true);

		setContent(mainLayout);
	}

	/**
	 * 	Drag item from available list to selected list.
	 */
	private void setDragFromAvailable() {
		configureGridDragSource(availableDrag);
		configureGridDropTarget(selectedFavorDrop);
		configureGridDropTarget(selectedAgainstDrop);
	}
	/**
	 * Let item back to available list.
	 */
	private void setDragFromSelected() {
		configureGridDragSource(selectedFavorDrag);
		configureGridDragSource(selectedAgainstDrag);
		configureGridDropTarget(availableDrop);
	}

	private void configureGridDragSource(GridDragSource<ExplicitTarget> gridDragSource) {
		gridDragSource.setEffectAllowed(EffectAllowed.MOVE);
		gridDragSource.addGridDragStartListener(event -> {
			draggedItems = event.getDraggedItems();
		});
		gridDragSource.setDragDataGenerator(target -> {
			JsonObject data = Json.createObject();
			data.put("targetName", target.getTargetName());
			data.put("instanceFavor", target.getInstancesInFavor());
			data.put("instanceAgainst", target.getInstancesAgainst());
			return data;
		});
	}

	/**
	 * configure drop effect, drop listener of a Grid object.
	 * @param gridDropTarget
	 */
	private void configureGridDropTarget(GridDropTarget<ExplicitTarget> gridDropTarget) {
		gridDropTarget.setDropEffect(DropEffect.MOVE);
		gridDropTarget.addGridDropListener(event -> {
			event.getDragSourceExtension().ifPresent(source -> {
				if (source instanceof GridDragSource) {
					// Add dragged items to the target Grid
					if (source.equals(availableDrag)) {
						((ListDataProvider<ExplicitTarget>) listOfAvailableTargets.getDataProvider()).getItems()
								.removeAll(draggedItems);
						listOfAvailableTargets.getDataProvider().refreshAll();

						// available to selected favor
						if (gridDropTarget.equals(selectedFavorDrop)) {
							for (ExplicitTarget item: draggedItems) {
								service.selectFavorTarget(item);
							}
						} else if (gridDropTarget.equals(selectedAgainstDrop)) {
							for (ExplicitTarget item: draggedItems) {
								service.selectAgainstTarget(item);
							}
						}
					} else if (source.equals(selectedFavorDrag)) {
						((ListDataProvider<ExplicitTarget>) listOfSelectedFavorTargets.getDataProvider()).getItems()
								.removeAll(draggedItems);
						listOfSelectedFavorTargets.getDataProvider().refreshAll();

						if (gridDropTarget.equals(availableDrop)) {
							for (ExplicitTarget item: draggedItems) {
								service.deselectFavorTarget(item);
							}
						}

					} else if (source.equals(selectedAgainstDrag)) {
						((ListDataProvider<ExplicitTarget>) listOfSelectedAgainstTargets.getDataProvider()).getItems()
								.removeAll(draggedItems);
						listOfSelectedAgainstTargets.getDataProvider().refreshAll();

						if (gridDropTarget.equals(availableDrop)) {
							for (ExplicitTarget item: draggedItems) {
								service.deselectAgainstTarget(item);
							}
						}
					}

					ListDataProvider<ExplicitTarget> dataProvider = (ListDataProvider<ExplicitTarget>)
							event.getComponent().getDataProvider();
					List<ExplicitTarget> items = (List<ExplicitTarget>) dataProvider.getItems();

					// Calculate the target row's index
					int index = items.indexOf(event.getDropTargetRow()) + (
							event.getDropLocation() == DropLocation.BELOW ? 1 : 0);

					items.addAll(index, draggedItems);
					dataProvider.refreshAll();

					// Remove reference to dragged items
					draggedItems = null;
				}
			});
		});
	}

	private void refresh_AvailableGrid() {
		refresh_AvailableGrid(filter.getValue());
	}

	private void refresh_AvailableGrid(String stringFilter) {
		listOfAvailableTargets.setItems(service.getAllAvailableTargets(stringFilter));
	}

	// refresh two selected targets (favor and against)
	private void refresh_SelectedGrid() {
		listOfSelectedFavorTargets.setItems(service.getAllSelectedFavorTargets());
		listOfSelectedAgainstTargets.setItems(service.getAllSelectedAgainstTargets());
	}

	/*
	 * You can specify additional servlet parameters like the URI and UI class
	 * name and turn on production mode when you have finished developing the
	 * application.
	 */
	@WebServlet(urlPatterns = "/detector/*") // change URI of this website to "/detector"
	@VaadinServletConfiguration(ui = InteractiveStanceGUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {

	}
}
