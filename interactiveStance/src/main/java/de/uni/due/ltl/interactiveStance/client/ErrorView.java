package de.uni.due.ltl.interactiveStance.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;


public class ErrorView extends VerticalLayout implements View {

    private Label label = new Label("Error 404 PAGE NOT FOUND");

    public ErrorView () {
        this.addComponent(label);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}