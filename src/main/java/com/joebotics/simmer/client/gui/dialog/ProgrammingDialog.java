package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.gui.Bgpio;

import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialModal;

public class ProgrammingDialog extends Composite {

    private static ProgrammingDialogUiBinder uiBinder = GWT.create(ProgrammingDialogUiBinder.class);

    interface ProgrammingDialogUiBinder extends UiBinder<Widget, ProgrammingDialog> {
    }

    @UiField
    MaterialModal modal;

    @UiField
    MaterialIcon btnRun;

    @UiField
    MaterialIcon btnDebug;

    @UiField
    MaterialIcon btnClose;

    @UiField
    MaterialIcon btnExpand;

    @UiField
    MaterialIcon btnCollapse;

    @UiField
    HTMLPanel blocklyPanel;

    @UiField
    HTMLPanel toolboxPanel;

    @UiField
    MaterialColumn codeTab;

    @UiField
    MaterialColumn consoleTab;

    private Bgpio.Params params;
    private Element workspacePlayground;

    public ProgrammingDialog() {
        initWidget(uiBinder.createAndBindUi(this));
        blocklyPanel.setHeight("300px");

        params = new Bgpio.Params();
        params.media = "blockly/media/";
        params.toolbox = toolboxPanel.getElement().getFirstChildElement();
        params.sounds = false;

        Bgpio.codePanel = codeTab.getElement();
    }

    @UiHandler("btnRun")
    public void btnRunHandler(ClickEvent event) {
        Bgpio.RunMode.run();
    }

    @UiHandler("btnCollapse")
    public void btnCollapseHandler(ClickEvent event) {
        modal.setFullscreen(false);
        blocklyPanel.setHeight("300px");
        Bgpio.resize();
    }

    @UiHandler("btnExpand")
    public void btnExpandHandler(ClickEvent event) {
        modal.setFullscreen(true);
        blocklyPanel.setHeight("500px");
        Bgpio.resize();
    }

    @UiHandler("btnClose")
    public void btnCloseHandler(ClickEvent event) {
        modal.close();
    }

    @UiHandler("modal")
    public void modalOpeningHandler(OpenEvent<MaterialModal> event) {
        Bgpio.resize();
        Bgpio.setBlocks(DOM.getElementById("startBlocks"));
    }

    public void open() {
        if (workspacePlayground == null) {
            workspacePlayground = Bgpio.init(blocklyPanel.getElement(), params);
        }
        modal.open();
    }
}