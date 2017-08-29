package com.joebotics.simmer.client.gui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {

    private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

    interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
    }

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    Canvas canvas;
    
    @UiField
    HTMLPanel burner;
    
    @UiField
    HTMLPanel burnerControls;

    @UiFactory
    Canvas createCanvas() {
        return Canvas.createIfSupported();
    }

    public MainPanel(String firstName) {
        initWidget(uiBinder.createAndBindUi(this));
        burner.getElement().addClassName("burner-placeholder");
        burnerControls.getElement().addClassName("burner-сontrols-placeholder");
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
