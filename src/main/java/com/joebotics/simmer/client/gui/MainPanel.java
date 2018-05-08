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

import com.joebotics.simmer.client.SimmerController;
import com.joebotics.simmer.client.gui.views.AssistantView;
import com.joebotics.simmer.client.gui.views.BlocksView;
import com.joebotics.simmer.client.gui.views.CodeView;
import com.sun.istack.internal.Nullable;

import gwt.material.design.client.MaterialDesignBase;
import gwt.material.design.client.ui.MaterialContainer;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialPanel;

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

    @UiField
    MaterialPanel mainContainer;

    @UiField
    MaterialContainer contentContainer;

    private Widget cuttentToolbar;

    protected SimmerController controller;

    private RunToolbar runToolbar;

    public MainPanel(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));
        this.controller = controller;
    }

    @UiFactory
    Canvas createCanvas() {
        return Canvas.createIfSupported();
    }

    @UiHandler("optionsButton")
    public void onOptionsButtonClick(ClickEvent event) {

    }

    @UiHandler("orderPartsButton")
    public void onOrderPartsButtonClick(ClickEvent event) {

    }

    @UiHandler("assistantButton")
    public void onAssistantButtonClick(ClickEvent event) {
        setContent(new AssistantView(), null);
    }

    @UiHandler("circuitButton")
    public void onCircuitButtonClick(ClickEvent event) {
        setContent(null, new MainToolbar(controller));
    }

    @UiHandler("blocksButton")
    public void onBlockButtonClick(ClickEvent event) {
        setContent(new BlocksView(), getRunToolbar());
    }

    @UiHandler("codeButton")
    public void onCodeButtonClick(ClickEvent event) {
        setContent(new CodeView(), getRunToolbar());
    }

    @UiHandler("controlsButton")
    public void onControlsButtonClick(ClickEvent event) {

    }

    private RunToolbar getRunToolbar() {
        if (runToolbar == null) {
            runToolbar = new RunToolbar(controller);
        }
        return runToolbar;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Widget getMainContainer() {
        return mainContainer;
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

    public void setContent(@Nullable Widget content, Widget toolbar) {
        if (toolbar != null) {
            setToolbar(toolbar);
        } else {
            removeToolbar();
        }

        contentContainer.clear();

        if (content != null) {
            canvas.setVisible(false);
            contentContainer.add(content);
        } else {
            canvas.setVisible(true);
        }
    }

}
