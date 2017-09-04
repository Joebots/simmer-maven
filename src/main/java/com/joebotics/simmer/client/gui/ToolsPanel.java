package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.gui.dialog.OptionsDialog;
import com.joebotics.simmer.client.gui.dialog.SchematicDialog;

import gwt.material.design.client.ui.MaterialButton;

public class ToolsPanel extends Composite {

    private static ToolsPanelUiBinder uiBinder = GWT.create(ToolsPanelUiBinder.class);

    interface ToolsPanelUiBinder extends UiBinder<Widget, ToolsPanel> {
    }

    @UiField
    MaterialButton optionsButton;

    @UiField
    MaterialButton fileButton;

    @UiField
    MaterialButton schematicButton;

    @UiField
    MaterialButton powerButton;

    @UiField
    OptionsDialog optionsDialog;

    @UiField
    SchematicDialog schematicDialog;

    public ToolsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("optionsButton")
    public void optionsButtonHandler(ClickEvent e) {
        optionsDialog.open();
    }

    @UiHandler("schematicButton")
    public void schematicButtonHandler(ClickEvent e) {
        schematicDialog.open();
    }

    @UiHandler("powerButton")
    public void powerButtonHandler(ClickEvent e) {
        closeBrowser();
    }

    public native void closeBrowser()
    /*-{
        $wnd.close();
    }-*/;
}
