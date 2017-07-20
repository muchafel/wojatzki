package de.uni.due.ltl.interactiveStance.client.detectorViews;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
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
import de.uni.due.ltl.interactiveStance.client.ConfigView;
import de.uni.due.ltl.interactiveStance.client.MainUI;
import de.uni.due.ltl.interactiveStance.client.charts.StanceDataPieChart;
import de.uni.due.ltl.interactiveStance.experimentLogging.AnalysisEvent;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.experimentLogging.FilterEvent;
import de.uni.due.ltl.interactiveStance.experimentLogging.ResultEvent;
import de.uni.due.ltl.interactiveStance.experimentLogging.SearchEvent;
import de.uni.due.ltl.interactiveStance.experimentLogging.TargetSelectedEvent;

import org.vaadin.addon.JFreeChartWrapper;

import java.util.List;
import java.util.Set;


public abstract class DetectorView_Base extends VerticalLayout implements View {

	protected ExperimentLogging logging;
	protected ExperimentConfiguration config;
    BackEnd service;
    TextField searchField = new TextField();
    TextField filter = new TextField();
    Grid<ExplicitTarget> listOfAvailableTargets = new Grid<>();
	Grid<ExplicitTarget> listOfSelectedFavorTargets = new Grid<>();
	Grid<ExplicitTarget> listOfSelectedAgainstTargets = new Grid<>();
    Button searchButton = new Button("Retrieve");
    Button analysisButton = new Button("Analysis");
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
    VerticalLayout piechartLayout = new VerticalLayout();
    Panel piechartPanel = new Panel(piechartLayout);
    JFreeChartWrapper pieChart;
//    Label favorSelectionTextField = new Label();
//    Label againstSelectionTextField = new Label();
    Label gap = new Label();
    protected Label gridsBaseDesc = new Label("Choose Statements which are in favor or against ");
    protected Label gridsTargetDesc = new Label();
    protected VerticalLayout gridsDescription = new VerticalLayout();
    Label analysisLabel = new Label();
//    Label breaklineLabel = new Label("<hr/>", ContentMode.HTML);
    Label availableCaption = new Label("Available Statements");
    protected VerticalLayout selectedFavorTargetsContent = new VerticalLayout();
    protected VerticalLayout selectedAgainstTargetsContent = new VerticalLayout();
    HorizontalLayout controlsPanel= new HorizontalLayout();

    public DetectorView_Base(ExperimentConfiguration config,ExperimentLogging logging) {
    	this.logging=logging;
    	this.config=config;
        configureComponents();
        buildLayout();
    }

    /**
     * Here we configure properties of our components
     */
    protected void configureComponents(){
        gap.setHeight("1em");

    	searchField.setPlaceholder("search term");
    	searchField.setWidth(400, Unit.PIXELS);
    	searchField.focus();
    	
        searchField.addValueChangeListener(event -> {
           filter.setVisible(false);
        });
        
		searchField.addShortcutListener(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				search(searchField.getValue());
			}
		});

        // configure available grid
        filter.setDescription("filter");
        filter.setPlaceholder("Filter Retrieved Statements");
		filter.addValueChangeListener(e -> {
			new FilterEvent(logging, e.getValue()).persist(false);
			refresh_AvailableGrid(e.getValue());
		});
        filter.setVisible(false);

        searchButton.addClickListener(clickEvent -> {
        	search(searchField.getValue());
        });

//        breaklineLabel.setWidth("20%");

        configureGrids();

		analysisButton.addClickListener(clickEvent -> {
			// Notification.show("SemEval: "+result.getSemEval() +
			// System.lineSeparator()+" MicroF1: "+result.getMicroF());
			if (((ListDataProvider<ExplicitTarget>) listOfSelectedFavorTargets.getDataProvider()).getItems().isEmpty()
					&& ((ListDataProvider<ExplicitTarget>) listOfSelectedAgainstTargets.getDataProvider()).getItems()
							.isEmpty()) {
				Notification notification = new Notification("Select at least one explicit target before analysis",
						Notification.Type.WARNING_MESSAGE);
				notification.setDelayMsec(1000);
				notification.show(Page.getCurrent());
			} else {
				new AnalysisEvent(logging, service.getAllSelectedFavorTargets(), service.getAllSelectedAgainstTargets())
						.persist(false);
				EvaluationResult result = service.analyse();
				new ResultEvent(logging, result, service.getAllSelectedFavorTargets(),
						service.getAllSelectedAgainstTargets()).persist(false);
				((MainUI) this.getUI()).showResult(result, service);
			}
            
        });

        analysisButton.addStyleName(ValoTheme.BUTTON_HUGE);
        analysisButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        analysisButton.setIcon(VaadinIcons.COGS);
    }

	private void search(String searchFieldString) {
		new SearchEvent(logging, searchFieldString).persist(false);
        this.service.newSearch(searchFieldString);
        filter.setValue("");
        filter.setVisible(true);
        refresh_AvailableGrid();
        refresh_SelectedGrid();
	}

	protected abstract void configureGrids();

	/**
     * Here we stack the components together
     */
    protected void buildLayout() {
        HorizontalLayout searchLayout = new HorizontalLayout(availableCaption, searchField, searchButton, filter);
        searchLayout.setMargin(false);
        searchLayout.setWidth("100%");
        searchLayout.setExpandRatio(availableCaption, 1);
        searchLayout.setComponentAlignment(availableCaption, Alignment.MIDDLE_LEFT);
        searchLayout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);
        searchLayout.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER);

        gridsDescription.addComponent(gridsBaseDesc);
        gridsDescription.addComponent(gridsTargetDesc);
        gridsDescription.setComponentAlignment(gridsBaseDesc, Alignment.MIDDLE_CENTER);
        gridsDescription.setComponentAlignment(gridsTargetDesc, Alignment.MIDDLE_CENTER);
        gridsDescription.setSpacing(false);
        gridsDescription.setMargin(false);

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

		// selected favor targets
		
//		selectedFavorTargetsContent.addComponent(favorSelectionTextField);
		selectedFavorTargetsContent.addComponent(listOfSelectedFavorTargets);
        selectedFavorTargetsContent.setMargin(false);

//		selectedAgainstTargetsContent.addComponent(againstSelectionTextField);
		selectedAgainstTargetsContent.addComponent(listOfSelectedAgainstTargets);
        selectedAgainstTargetsContent.setMargin(false);

		// all selected content
		HorizontalLayout selectedTargetsContent = new HorizontalLayout();
		selectedTargetsContent.addComponent(selectedFavorTargetsContent);
		selectedTargetsContent.addComponent(selectedAgainstTargetsContent);
		selectedTargetsContent.setWidth("100%");
        selectedTargetsContent.setExpandRatio(selectedFavorTargetsContent, 1.0f);
        selectedTargetsContent.setExpandRatio(selectedAgainstTargetsContent, 1.0f);
		selectedTargetsContent.setSpacing(true);
		
		controlsPanel.addComponent(analysisButton);

		this.addComponent(piechartPanel);
		this.addComponent(gap);
		this.addComponent(gridsDescription);
//		this.setComponentAlignment(analysisLabel, Alignment.MIDDLE_CENTER);
//		this.addComponent(breaklineLabel);
//      this.setComponentAlignment(breaklineLabel, Alignment.MIDDLE_CENTER);
        this.addComponent(searchLayout);
        this.setComponentAlignment(searchLayout, Alignment.MIDDLE_RIGHT);
//		this.addComponent(filter);
		this.addComponent(listOfAvailableTargets);
		this.addComponent(selectedTargetsContent);
        this.addComponent(controlsPanel);
        controlsPanel.setComponentAlignment(analysisButton, Alignment.BOTTOM_RIGHT);
        this.setComponentAlignment(controlsPanel, Alignment.BOTTOM_RIGHT);
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
                    if (draggedItems == null) {
                        return ;
                    }

                    // Add dragged items to the target Grid
                    if (source.equals(availableDrag)) {
                        ((ListDataProvider<ExplicitTarget>) listOfAvailableTargets.getDataProvider()).getItems()
                                .removeAll(draggedItems);
                        listOfAvailableTargets.getDataProvider().refreshAll();

                        // available to selected favor
                        if (gridDropTarget.equals(selectedFavorDrop)) {
                            for (ExplicitTarget item: draggedItems) {
                            	new TargetSelectedEvent(logging, "FAVOR", item.getTargetName(),true).persist(false);
                                service.selectFavorTarget(item);
                            }
                        } else if (gridDropTarget.equals(selectedAgainstDrop)) {
                            for (ExplicitTarget item: draggedItems) {
                            	new TargetSelectedEvent(logging, "AGAINST", item.getTargetName(),true).persist(false);
                            	service.selectAgainstTarget(item);
                            }
                        }
                    } else if (source.equals(selectedFavorDrag)) {
                        ((ListDataProvider<ExplicitTarget>) listOfSelectedFavorTargets.getDataProvider()).getItems()
                                .removeAll(draggedItems);
                        listOfSelectedFavorTargets.getDataProvider().refreshAll();

                        if (gridDropTarget.equals(availableDrop)) {
                            for (ExplicitTarget item: draggedItems) {
                            	new TargetSelectedEvent(logging, "FAVOR", item.getTargetName(),false).persist(false);
                                service.deselectFavorTarget(item);
                            }
                        }

                    } else if (source.equals(selectedAgainstDrag)) {
                        ((ListDataProvider<ExplicitTarget>) listOfSelectedAgainstTargets.getDataProvider()).getItems()
                                .removeAll(draggedItems);
                        listOfSelectedAgainstTargets.getDataProvider().refreshAll();

                        if (gridDropTarget.equals(availableDrop)) {
                            for (ExplicitTarget item: draggedItems) {
                            	new TargetSelectedEvent(logging, "AGAINST", item.getTargetName(),false).persist(false);
                                service.deselectAgainstTarget(item);
                            }
                        }
                    }

                    ListDataProvider<ExplicitTarget> dataProvider = (ListDataProvider<ExplicitTarget>)
                            event.getComponent().getDataProvider();
                    List<ExplicitTarget> items = (List<ExplicitTarget>) dataProvider.getItems();

                    int index = items.size();
                    // Calculate the target row's index
                    if (event.getDropTargetRow().isPresent()) {
                        // drag and drop at the title column in same grid will get the index -1.
                        if (items.indexOf(event.getDropTargetRow().get()) == -1) {
                            index = 0;
                        } else {
                            index = items.indexOf(event.getDropTargetRow().get()) + (
                                    event.getDropLocation() == DropLocation.BELOW ? 1 : 0);
                        }
                    }

                    items.addAll(index, draggedItems);
                    dataProvider.refreshAll();

                    // Remove reference to dragged items
                    draggedItems = null;
                }
            });
        });
    }

    protected void refresh_AvailableGrid() {
        refresh_AvailableGrid(filter.getValue());
    }

    protected void refresh_AvailableGrid(String stringFilter) {
        listOfAvailableTargets.setItems(service.getAllAvailableTargets(stringFilter));
    }

    // refresh two selected targets (favor and against)
    protected void refresh_SelectedGrid() {
        listOfSelectedFavorTargets.setItems(service.getAllSelectedFavorTargets());
        listOfSelectedAgainstTargets.setItems(service.getAllSelectedAgainstTargets());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
		((MainUI) this.getUI()).showMenubar();

		System.out.println(event.getOldView() instanceof ConfigView);
		if (event.getOldView() instanceof ConfigView) {
			service = BackEnd.loadData(logging,config);
		}

		Label basicResultLabel = new Label("Composition of Training Data");
		StanceDataPieChart pc = new StanceDataPieChart();
		pieChart = pc.createPieChart(service);
		// Default Width*Height: 809*500
		pieChart.setWidth(480.0F, Sizeable.Unit.PIXELS);
		pieChart.setHeight(300.0F, Sizeable.Unit.PIXELS);
		this.piechartLayout.removeAllComponents();
        this.piechartLayout.addComponent(basicResultLabel);
        this.piechartLayout.addComponent(pieChart);
        this.piechartLayout.setComponentAlignment(basicResultLabel, Alignment.MIDDLE_CENTER);
        this.piechartLayout.setComponentAlignment(pieChart, Alignment.MIDDLE_CENTER);

        this.gridsTargetDesc.setValue(service.getEvaluationScenario().getTarget());
        this.gridsTargetDesc.addStyleName(ValoTheme.LABEL_H3);
        this.gridsTargetDesc.addStyleName("label-wrap");
//        this.analysisLabel.setValue("Choose Statements which are in favor or against "+service.getEvaluationScenario().getTarget());
//        this.analysisLabel.addStyleName(ValoTheme.LABEL_H2);
//        this.analysisLabel.addStyleName("label-wrap");
		// initial filling of grid
		refresh_AvailableGrid();
		refresh_SelectedGrid();
    }
}