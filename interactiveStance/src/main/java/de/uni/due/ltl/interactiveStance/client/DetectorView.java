package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
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
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import org.vaadin.addon.JFreeChartWrapper;

import java.util.List;
import java.util.Set;


public class DetectorView extends VerticalLayout implements View {

    BackEnd service;
    TextField searchField = new TextField();
    TextField filter = new TextField();
    Grid<ExplicitTarget> listOfAvailableTargets = new Grid<>("Available Topics");
	Grid<ExplicitTarget> listOfSelectedFavorTargets = new Grid<>("Favor Topics");
	Grid<ExplicitTarget> listOfSelectedAgainstTargets = new Grid<>("Against Topics");
    Button searchButton = new Button("GO");
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
    HorizontalLayout piechartLayout = new HorizontalLayout();
    JFreeChartWrapper pieChart;
    TextField favorSelectionTextField = new TextField();
    TextField againstSelectionTextField = new TextField();
    

    public DetectorView() {
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
            this.service.newSearch(searchField.getValue());
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

        analysisButton.addClickListener(clickEvent -> {
            EvaluationResult result = service.analyse();
            Notification.show("SemEval: "+result.getSemEval() + System.lineSeparator()+" MicroF1: "+result.getMicroF());
        });

        analysisButton.addStyleName(ValoTheme.BUTTON_HUGE);
        analysisButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        analysisButton.setIcon(VaadinIcons.COGS);
        
        favorSelectionTextField.setIcon(VaadinIcons.PLUS_CIRCLE);
		againstSelectionTextField.setIcon(VaadinIcons.MINUS_CIRCLE);
        
    }

    /**
     * Here we stack the components together
     */
    private void buildLayout() {
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);
        searchLayout.setMargin(false);
        searchLayout.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);
        searchLayout.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER);

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
		VerticalLayout selectedFavorTargetsContent = new VerticalLayout();
		selectedFavorTargetsContent.addComponent(favorSelectionTextField);
		selectedFavorTargetsContent.addComponent(listOfSelectedFavorTargets);

		VerticalLayout selectedAgainstTargetsContent = new VerticalLayout();
		selectedAgainstTargetsContent.addComponent(againstSelectionTextField);
		selectedAgainstTargetsContent.addComponent(listOfSelectedAgainstTargets);

		// all selected content
		HorizontalLayout selectedTargetsContent = new HorizontalLayout();
		selectedTargetsContent.addComponent(selectedFavorTargetsContent);
		selectedTargetsContent.addComponent(selectedAgainstTargetsContent);
		selectedTargetsContent.setWidth("100%");
		selectedTargetsContent.setSpacing(true);

		this.addComponent(this.piechartLayout);
		this.addComponent(searchLayout);
		this.addComponent(filter);
		this.addComponent(listOfAvailableTargets);
		this.addComponent(analysisButton);
		this.addComponent(selectedTargetsContent);
        this.setSpacing(true);
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
        // transferring extra data along with drag&drop operation.
//        gridDragSource.setDragDataGenerator("targets", target -> {
//            JsonObject data = Json.createObject();
//            data.put("targetName", target.getTargetName());
//            data.put("instanceFavor", target.getInstancesInFavor());
//            data.put("instanceAgainst", target.getInstancesAgainst());
//            return data;
//        });
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

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // Moved loading data here, because don't touch data in backend before loading the view.
        service = BackEnd.loadData();
        JFreePieChart pc = new JFreePieChart();
        pieChart = pc.createPieChart(service);
        // Default Width*Height: 809*500
        pieChart.setWidth(480.0F, Sizeable.Unit.PIXELS);
        pieChart.setHeight(300.0F, Sizeable.Unit.PIXELS);
        this.piechartLayout.addComponent(pieChart);

        // initial filling of grid
        refresh_AvailableGrid();
        refresh_SelectedGrid();
    }
}