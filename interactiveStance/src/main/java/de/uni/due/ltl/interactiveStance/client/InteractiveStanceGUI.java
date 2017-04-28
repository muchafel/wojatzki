package de.uni.due.ltl.interactiveStance.client;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.backend.BackEnd;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;

/**
 * main GUI class; use this to start the application
 * @author michael
 *
 */
@Title("Interactive Stance Detection")
@Theme("valo")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class InteractiveStanceGUI extends UI {

	TextField filter = new TextField("Filter Term");
	Grid listOfAvailableTargets = new Grid("Available Targets");
	Grid listOfSelectedFavorTargets = new Grid("Selected Targets of Favor");
	Grid listOfSelectedAgainstTargets = new Grid("Selected Targets of Against");
	Button searchButton= new Button("Search");
	Button analysisButton = new Button("Analysis");
	BackEnd service = BackEnd.loadData();

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

		// configure available grid
		filter.setDescription("filter");
		filter.addValueChangeListener(e -> refresh_AvailableGrid(e.getValue()));
		
		searchButton.addClickListener(clickEvent -> {
			service.newSearch(filter.getValue());
			refresh_AvailableGrid();
			refresh_SelectedGrid();
		});
		
		listOfAvailableTargets.setContainerDataSource(new BeanItemContainer<>(ExplicitTarget.class));
		listOfAvailableTargets.setColumnOrder("targetName", "instancesInFavor", "instancesAgainst");
		// The length of Target Name is often long. let it take all extra space.
		listOfAvailableTargets.getColumn("targetName").setExpandRatio(1);
		listOfAvailableTargets.removeColumn("id");
		listOfAvailableTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

		listOfAvailableTargets.addSelectionListener(e -> {
			listOfAvailableTargets.getContainerDataSource().removeItem(listOfAvailableTargets.getSelectedRow());
			service.selectTarget((ExplicitTarget) listOfAvailableTargets.getSelectedRow());
			refresh_SelectedGrid();
		});

		// configure selection grid of favor and against
		listOfSelectedFavorTargets.setContainerDataSource(new BeanItemContainer<>(ExplicitTarget.class));
		listOfSelectedFavorTargets.setColumnOrder("targetName", "instancesInFavor", "instancesAgainst");
		listOfSelectedFavorTargets.getColumn("targetName").setExpandRatio(1);
		listOfSelectedFavorTargets.removeColumn("id");
		listOfSelectedFavorTargets.setSelectionMode(Grid.SelectionMode.SINGLE);
		listOfSelectedFavorTargets.addSelectionListener(e -> {
			listOfSelectedFavorTargets.getContainerDataSource().removeItem(listOfSelectedFavorTargets.getSelectedRow());
			service.deselectTarget((ExplicitTarget) listOfSelectedFavorTargets.getSelectedRow());
			refresh_AvailableGrid();
		});

		listOfSelectedAgainstTargets.setContainerDataSource(new BeanItemContainer<>(ExplicitTarget.class));
		listOfSelectedAgainstTargets.setColumnOrder("targetName", "instancesInFavor", "instancesAgainst");
		listOfSelectedAgainstTargets.getColumn("targetName").setExpandRatio(1);
		listOfSelectedAgainstTargets.removeColumn("id");
		listOfSelectedAgainstTargets.setSelectionMode(Grid.SelectionMode.SINGLE);
		listOfSelectedAgainstTargets.addSelectionListener(e -> {
			listOfSelectedAgainstTargets.getContainerDataSource().removeItem(listOfSelectedAgainstTargets.getSelectedRow());
			service.deselectTarget((ExplicitTarget) listOfSelectedAgainstTargets.getSelectedRow());
			refresh_AvailableGrid();
		});

		// initial filling of grid
		refresh_AvailableGrid();

		analysisButton.addClickListener(clickEvent -> {
//			Notification.show("Run Analysis of "+service.printSelectedTargets());

			// Set polling frequency to 0.5 seconds.
			EvaluationResult result= service.analyse();
			Notification.show("microSemEval: "+result.getMicroSemEval() + System.lineSeparator()+" microF1: "+result.getMicroF());
		});

		analysisButton.addStyleName(ValoTheme.BUTTON_HUGE);
		analysisButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		analysisButton.setIcon(FontAwesome.COGS);
	}

	/**
	 * Here we stack the components together
	 */
	private void buildLayout() {
		FormLayout filterWrapper = new FormLayout(filter);
		filterWrapper.setMargin(false);
		HorizontalLayout actions = new HorizontalLayout(filterWrapper, searchButton);
		actions.setSpacing(true);
		actions.setComponentAlignment(searchButton, Alignment.MIDDLE_CENTER);

		HorizontalLayout selectedTargetsContent = new HorizontalLayout();
		selectedTargetsContent.addComponent(listOfSelectedFavorTargets);
		selectedTargetsContent.addComponent(listOfSelectedAgainstTargets);
		selectedTargetsContent.setWidth("100%");
		selectedTargetsContent.setSpacing(true);
		VerticalLayout left = new VerticalLayout(actions, listOfAvailableTargets, analysisButton, selectedTargetsContent);
		left.setSpacing(true);

		listOfAvailableTargets.setWidth("100%");
		listOfAvailableTargets.setHeightMode(HeightMode.ROW);
		listOfAvailableTargets.setHeightByRows(6.0D);

		listOfSelectedFavorTargets.setWidth("100%");
		listOfSelectedFavorTargets.setHeightMode(HeightMode.ROW);
		listOfSelectedFavorTargets.setHeightByRows(4.0D);
		listOfSelectedAgainstTargets.setWidth("100%");
		listOfSelectedAgainstTargets.setHeightMode(HeightMode.ROW);
		listOfSelectedAgainstTargets.setHeightByRows(4.0D);

		HorizontalLayout mainLayout = new HorizontalLayout(left);
		mainLayout.setWidth("100%");
		mainLayout.setMargin(true);

		setContent(mainLayout);
	}

	private void refresh_AvailableGrid() {
		refresh_AvailableGrid(filter.getValue());
	}

	private void refresh_AvailableGrid(String stringFilter) {
		listOfAvailableTargets.setContainerDataSource(
				new BeanItemContainer<>(ExplicitTarget.class, service.getAllAvailableTargets(stringFilter)));
	}

	private void refresh_SelectedGrid() {
		listOfSelectedFavorTargets.setContainerDataSource(
				new BeanItemContainer<>(ExplicitTarget.class, service.getAllSelectedTargets()));
	}

	/*
	 * You can specify additional servlet parameters like the URI and UI class
	 * name and turn on production mode when you have finished developing the
	 * application.
	 */
	@WebServlet(urlPatterns = "/*")
	@VaadinServletConfiguration(ui = InteractiveStanceGUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {


	}

}
