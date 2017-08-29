package com.joebotics.simmer.client.gui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.gui.dialog.OptionsDialog;

import gwt.material.design.client.ui.MaterialLink;

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
        /*
         * if (canvas == null) { // fire circuit broken event here // {source:
         * simmer, component: ce, message:
         * "Voltage_source/wire_loop_with_no_resistance!"} String message =
         * MessageI18N.getMessage(
         * "Not_working._You_need_a_browser_that_supports_the_CANVAS_element.");
         * JSEventBusProxy.fireError(SimmerEvents.SYSTEM_ERROR, message);
         * RootPanel.get().add(new Label(message)); return; }
         */
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
