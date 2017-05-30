package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.ServletException;
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

    private MenuBar menuBar;
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
                getPage().setLocation("/");
//                getUI().getNavigator().navigateTo(LOGINVIEW);
            }
        });

        VerticalLayout mainLayout = new VerticalLayout(menuBar, placeholder);
        setContent(mainLayout);
    }

    @WebServlet(urlPatterns = "/*", asyncSupported = true)
    @VaadinServletConfiguration(widgetset = "com.vaadin.client.widget.grid", ui = MainUI.class, productionMode = false)
    public static class MainUIServlet extends VaadinServlet {

    }
}