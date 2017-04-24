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
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.ProgressIndicator;

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
	Grid listOfSelectedTargets = new Grid("Selected Targets");
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
		listOfAvailableTargets.getColumn("targetName").setWidth(500);
		listOfAvailableTargets.removeColumn("id");
		listOfAvailableTargets.setSelectionMode(Grid.SelectionMode.SINGLE);

		listOfAvailableTargets.addSelectionListener(e -> {
			listOfAvailableTargets.getContainerDataSource().removeItem(listOfAvailableTargets.getSelectedRow());
			service.selectTarget((ExplicitTarget) listOfAvailableTargets.getSelectedRow());
			refresh_SelectedGrid();
		});

		// configure selection grid
		listOfSelectedTargets.setContainerDataSource(new BeanItemContainer<>(ExplicitTarget.class));
		listOfSelectedTargets.setColumnOrder("targetName", "instancesInFavor", "instancesAgainst");
		listOfSelectedTargets.getColumn("targetName").setWidth(500);
		listOfSelectedTargets.removeColumn("id");
		listOfSelectedTargets.setSelectionMode(Grid.SelectionMode.SINGLE);
		listOfSelectedTargets.addSelectionListener(e -> {
			listOfSelectedTargets.getContainerDataSource().removeItem(listOfSelectedTargets.getSelectedRow());
			service.deselectTarget((ExplicitTarget) listOfSelectedTargets.getSelectedRow());
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
		HorizontalLayout actions = new HorizontalLayout(filter,searchButton);
		actions.setWidth("50%");
		filter.setWidth("50%");
		actions.setExpandRatio(filter, 1);


		VerticalLayout left = new VerticalLayout(actions, listOfAvailableTargets, analysisButton,listOfSelectedTargets);
		left.setSpacing(true);
		listOfAvailableTargets.setSizeFull();
		listOfAvailableTargets.setHeightByRows(6);
		listOfSelectedTargets.setSizeFull();
		listOfSelectedTargets.setHeightByRows(4);

		HorizontalLayout mainLayout = new HorizontalLayout(left);
		mainLayout.setSizeFull();

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
		listOfSelectedTargets.setContainerDataSource(
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
