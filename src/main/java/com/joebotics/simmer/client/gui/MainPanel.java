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

import gwt.material.design.client.MaterialDesignBase;
import gwt.material.design.client.ui.MaterialNavBar;

public class MainPanel extends Composite {

    static {
        MaterialDesignBase.injectCss(SimmerUIClientBundle.INSTANCE.dialogsCss());
    }

    private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

    interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {

    }

    @UiField
    Canvas canvas;

    @UiField
    MaterialNavBar navBar;

    private Widget cuttentToolbar;

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiFactory
    Canvas createCanvas() {
        return Canvas.createIfSupported();
    }

    @UiHandler("openButton")
    public void onOpenButtonClick(ClickEvent event) {

    }

    @UiHandler("optionsButton")
    public void onOptionsButtonClick(ClickEvent event) {

    }

    @UiHandler("codeBlocksButton")
    public void onCodeBlocksButtonClick(ClickEvent event) {

    }

    @UiHandler("orderPartsButton")
    public void onOrderPartsButtonClick(ClickEvent event) {

    }

    @UiHandler("assistantDialog")
    public void onAssistantButtonClick(ClickEvent event) {

    }

    @UiHandler("exitButton")
    public void onExitButtonClick(ClickEvent event) {

    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setToolbar(Widget widget) {
        removeToolbar();
        cuttentToolbar = widget;
        navBar.add(cuttentToolbar);
    }

    public void removeToolbar() {
        if (cuttentToolbar != null) {
            cuttentToolbar.removeFromParent();
        }
        cuttentToolbar = null;
    }

}
