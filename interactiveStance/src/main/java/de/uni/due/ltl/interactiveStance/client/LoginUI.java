package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

@Title("Login")
@Theme("valo")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class LoginUI extends UI implements LoginForm.LoginListener {

    public LoginUI() {

    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        Panel loginPanel = new Panel("Login");
        loginPanel.setWidth("50%");
        verticalLayout.addComponent(loginPanel);

        LoginForm loginForm = new LoginForm();
        loginPanel.setContent(loginForm);
        loginForm.addLoginListener(this);

        verticalLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
        setContent(verticalLayout);
    }

    @Override
    public void onLogin(LoginForm.LoginEvent loginEvent) {
        String username = loginEvent.getLoginParameter("username");
        String password = loginEvent.getLoginParameter("password");
        if (Authentification.authenticate(username, password)) {
            getPage().setLocation("/detector");
            getSession().close();
        } else {
            Notification errorNotif = new Notification("username or password incorrect.",
                    Notification.Type.ERROR_MESSAGE);
            errorNotif.setDelayMsec(1000);
            errorNotif.show(Page.getCurrent());
        }
}

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = LoginUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

    }
}
