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

    @UiFactory
    Canvas createCanvas() {
        return Canvas.createIfSupported();
    }

    public MainPanel(String firstName) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
