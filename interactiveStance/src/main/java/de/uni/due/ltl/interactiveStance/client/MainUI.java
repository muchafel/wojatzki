package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;

import javax.servlet.annotation.WebServlet;
import java.util.HashSet;
import java.util.Set;

@Theme("valo")
public class MainUI extends UI {

    private Navigator navigator;
    protected static final String LOGINVIEW = "login";
    protected static final String CONFIGVIEW = "config";
    protected static final String DETECTORVIEW = "detector";
    protected static final String ERRORVIEW = "error";
    protected static final String RESULTVIEW = "result";

    private MenuBar menuBar = null;
    private VerticalLayout placeholder = new VerticalLayout();

    @Override
    protected void init(VaadinRequest request) {
        buildOutline();

        navigator = new Navigator(this, placeholder);
        navigator.addView(LOGINVIEW, new LoginView());
        navigator.addView(CONFIGVIEW, new ConfigView());
        navigator.addView(DETECTORVIEW, new DetectorView());
        navigator.setErrorView(new ErrorView());

        navigator.navigateTo(LOGINVIEW);
    }

    private void buildOutline() {
        menuBar = new MenuBar();
        MenuBar.MenuItem homepage = menuBar.addItem("Homepage", null, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                getUI().getNavigator().navigateTo(LOGINVIEW);
            }
        });

        VerticalLayout mainLayout = new VerticalLayout(menuBar, placeholder);
        setContent(mainLayout);
    }

    public void hideMenubar() {
        if (menuBar != null) {
            menuBar.setVisible(false);
        }
    }

    public void showResult(EvaluationResult result){
        navigator.addView(RESULTVIEW, new ResultView( result));
        navigator.navigateTo(MainUI.RESULTVIEW);
    }
    
    public void showMenubar() {
        if (menuBar != null) {
            menuBar.setVisible(true);
        }
    }

    @WebServlet(urlPatterns = "/*", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MainUIServlet extends VaadinServlet {

    }
}
