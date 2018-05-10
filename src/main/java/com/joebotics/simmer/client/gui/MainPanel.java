package com.joebotics.simmer.client.gui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.SimmerController;
import com.joebotics.simmer.client.gui.dialog.CircuitsDialog;
import com.joebotics.simmer.client.gui.dialog.EditDialog;
import com.joebotics.simmer.client.gui.dialog.SchematicDialog;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.gui.views.AssistantView;
import com.joebotics.simmer.client.gui.views.BlocksView;
import com.joebotics.simmer.client.gui.views.CodeView;
import com.joebotics.simmer.client.gui.views.ControlsView;
import com.joebotics.simmer.client.gui.views.OptionsView;
import com.joebotics.simmer.client.gui.views.OrderPartsView;
import com.sun.istack.internal.Nullable;

import gwt.material.design.client.MaterialDesignBase;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialContainer;
import gwt.material.design.client.ui.MaterialModal;
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
    MaterialPanel rootContainer;

    @UiField
    MaterialContainer contentContainer, canvasContainer;

    @UiField
    CircuitsDialog circuitsDialog;

    @UiField
    EditDialog editDialog;

    @UiField
    SchematicDialog addDialog;

    @UiField
    HTMLPanel activeComponentsContainer;

    @UiField
    MaterialButton addButton;

    @UiField
    MaterialModal notImplementedModal;

    private Widget currentToolbar;

    private SimmerController controller;

    private RunToolbar runToolbar;

    public MainPanel(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));
        this.controller = controller;

        if (LoadFile.isSupported()) {
            LoadFile loadFile = new LoadFile((Simmer.getInstance()));
            Simmer.getInstance().setLoadFileInput(loadFile);
            rootContainer.add(loadFile);
        }
    }

    @UiFactory
    Canvas createCanvas() {
        return Canvas.createIfSupported();
    }

    @UiHandler("optionsButton")
    public void onOptionsButtonClick(ClickEvent event) {
        setContent(new OptionsView(), null, false);
    }

    @UiHandler("orderPartsButton")
    public void onOrderPartsButtonClick(ClickEvent event) {
        setContent(new OrderPartsView(), null, false);
    }

    @UiHandler("assistantButton")
    public void onAssistantButtonClick(ClickEvent event) {
        setContent(new AssistantView(), null, false);
    }

    @UiHandler("circuitButton")
    public void onCircuitButtonClick(ClickEvent event) {
        setContent(null, new MainToolbar(controller), true);
    }

    @UiHandler("blocksButton")
    public void onBlockButtonClick(ClickEvent event) {
        setContent(new BlocksView(), getRunToolbar(), true);
    }

    @UiHandler("codeButton")
    public void onCodeButtonClick(ClickEvent event) {
        setContent(new CodeView(), getRunToolbar(), false);
    }

    @UiHandler("controlsButton")
    public void onControlsButtonClick(ClickEvent event) {
        setContent(new ControlsView(), null, true);
    }

    @UiHandler("addButton")
    public void onAddButtonClick(ClickEvent event) {
        addDialog.open();
    }

    @UiHandler("closeModal")
    public void onCloseModalClick(ClickEvent event) {
        notImplementedModal.close();
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

    public void setContainerSize(int width, int height) {
        rootContainer.setWidth(width + "px");
        contentContainer.setHeight(height + "px");

        addButton.setBottom(Math.round(height * .28f));
    }

    public void setToolbar(Widget widget) {
        removeToolbar();
        currentToolbar = widget;
        navBar.add(currentToolbar);
    }

    public void removeToolbar() {
        if (currentToolbar != null) {
            currentToolbar.removeFromParent();
        }
        currentToolbar = null;
    }

    public void setContent(@Nullable Widget content, @Nullable Widget toolbar, boolean isAddButtonVisible) {
        if (toolbar != null) {
            setToolbar(toolbar);
        } else {
            removeToolbar();
        }

        contentContainer.clear();

        if (content != null) {
            canvasContainer.setVisible(false);
            activeComponentsContainer.setVisible(false);
            contentContainer.setVisible(true);
            contentContainer.add(content);
        } else {
            canvasContainer.setVisible(true);
            activeComponentsContainer.setVisible(true);
            contentContainer.setVisible(false);
            contentContainer.clear();
        }

        addButton.setVisible(isAddButtonVisible);
    }

    public boolean isCanvasVisible() {
        return canvasContainer.isVisible();
    }

    public void showCircuitsDialog() {
        circuitsDialog.open();
    }

    public EditDialog getEditDialog() {
        return editDialog;
    }

    public void showNotImplementedModal() {
        notImplementedModal.open();
    }
}
