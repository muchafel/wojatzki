package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;


public class LoginView extends VerticalLayout implements View, LoginForm.LoginListener {

    public LoginView() {
        initialize();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @Override
    public void onLogin(LoginForm.LoginEvent event) {
        String username = event.getLoginParameter("username");
        String password = event.getLoginParameter("password");
        if (Authentification.authenticate(username, password)) {
            getUI().getNavigator().navigateTo(MainUI.CONFIGVIEW);
        } else {
            getUI().getNavigator().navigateTo(MainUI.ERRORVIEW);
//            Notification errorNotif = new Notification("username or password incorrect.",
//                    Notification.Type.ERROR_MESSAGE);
//            errorNotif.setDelayMsec(1000);
//            errorNotif.show(Page.getCurrent());
        }
    }
    private void initialize() {
        this.setSizeFull();
        Panel loginPanel = new Panel("Login");
        loginPanel.setWidth("50%");
        this.addComponent(loginPanel);

//        LoginForm loginForm = new LoginForm();
        LoginRegisterFrom loginForm = new LoginRegisterFrom();
        loginPanel.setContent(loginForm);
        loginForm.addLoginListener(this);

        this.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
    }
}

class LoginRegisterFrom extends LoginForm {

    private String registerButtonCaption = "Register";
    private Button registerButton = new Button(registerButtonCaption);

    @Override
    protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(userNameField);
        layout.addComponent(passwordField);
        HorizontalLayout loginRegister = new HorizontalLayout(loginButton, registerButton);
        layout.addComponent(loginRegister);
        return layout;
    }
}